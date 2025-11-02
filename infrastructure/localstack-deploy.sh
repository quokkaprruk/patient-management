#!/bin/bash
set -e

LOCALSTACK_ENDPOINT="http://localhost:4566"
STACK_NAME="patient-management"
S3_BUCKET_NAME="test-bucket"
TEMPLATE_KEY="localstack.template.json"
TEMPLATE_FILE="./cdk.out/$TEMPLATE_KEY"
# The S3 URL where CloudFormation will look for the template.
# LocalStack handles this URL internally.
TEMPLATE_S3_URL="http://$S3_BUCKET_NAME.s3.localhost.localstack.cloud:4566/$TEMPLATE_KEY"

echo "AWS_DEFAULT_REGION: $AWS_DEFAULT_REGION"
echo "LocalStack Endpoint: $LOCALSTACK_ENDPOINT"

# 1. Delete stack if it exists
echo "Attempting to delete stack: $STACK_NAME (if it exists)"
# We use '|| true' here to ignore the error if the stack doesn't exist
aws --endpoint-url=$LOCALSTACK_ENDPOINT cloudformation delete-stack \
    --stack-name $STACK_NAME || true
aws --endpoint-url=$LOCALSTACK_ENDPOINT cloudformation wait stack-delete-complete \
    --stack-name $STACK_NAME || true


# 2. Create an S3 Bucket in LocalStack
echo "Creating S3 bucket: $S3_BUCKET_NAME (if it doesn't exist)"
aws --endpoint-url=$LOCALSTACK_ENDPOINT s3 mb "s3://$S3_BUCKET_NAME" || true

# 3. Verify the Bucket
aws --endpoint-url=$LOCALSTACK_ENDPOINT s3 ls

# 4. Verify the Bucket
echo "Copy template file to S3 bucket"
if aws --endpoint-url=$LOCALSTACK_ENDPOINT s3 cp "$TEMPLATE_FILE" "s3://$S3_BUCKET_NAME/$TEMPLATE_KEY"; then

# 5. Deploy the stack using the template's S3 URL
# Error: "aws ... cloudformation deploy ..." automatically uploading (or "staging") the template to S3 itself before creating the stack.
# it was confused and tried to talk to the real AWS S3
  aws --endpoint-url=$LOCALSTACK_ENDPOINT cloudformation create-stack \
      --stack-name $STACK_NAME \
      --template-url "$TEMPLATE_S3_URL" \
      --capabilities CAPABILITY_IAM \
      --disable-rollback
else
  echo "Template upload failed. Stack creation aborted."
fi

# 6. Wait for deployment to complete
echo "Waiting for stack $STACK_NAME creation to complete..."
aws --endpoint-url=$LOCALSTACK_ENDPOINT cloudformation wait stack-create-complete \
    --stack-name $STACK_NAME

echo "Stack $STACK_NAME deployed successfully."

# 7. Get Load Balancer DNS
echo "Fetching Load Balancer DNS (if available)..."
aws --endpoint-url=$LOCALSTACK_ENDPOINT elbv2 describe-load-balancers \
    --query "LoadBalancers[0].DNSName" --output text