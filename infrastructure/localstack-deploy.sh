#!/bin/bash
set -e # Stops the script if any command fails

# --- Configuration ---
export AWS_ACCESS_KEY_ID="test"
export AWS_SECRET_ACCESS_KEY="test"
export AWS_DEFAULT_REGION="us-east-1"

STACK_NAME="patient-management"
S3_BUCKET_NAME="s3-patient-management"
TEMPLATE_KEY="localstack.template.json"
TEMPLATE_FILE="./cdk.out/$TEMPLATE_KEY"
LOCALSTACK_ENDPOINT="http://localhost:4566"

# The S3 URL where CloudFormation will look for the template.
# LocalStack handles this URL internally.
TEMPLATE_S3_URL="http://$S3_BUCKET_NAME.s3.localhost.localstack.cloud:4566/$TEMPLATE_KEY"


echo "Using AWS Access Key ID: $AWS_ACCESS_KEY_ID"
echo "LocalStack Endpoint: $LOCALSTACK_ENDPOINT"

# 1. Delete stack if it exists
echo "Attempting to delete stack: $STACK_NAME (if it exists)"
# We use '|| true' here to ignore the error if the stack doesn't exist
aws --endpoint-url=$LOCALSTACK_ENDPOINT cloudformation delete-stack --stack-name $STACK_NAME || true


# 2. Create S3 bucket
echo "Creating S3 bucket: $S3_BUCKET_NAME (if it doesn't exist)"
# The 'mb' is "make/create bucket", "true"(return 0) force script to keep executing although this line fails
aws --endpoint-url=$LOCALSTACK_ENDPOINT s3 mb "s3://$S3_BUCKET_NAME" || true

# 3. Upload cloudformation.json template to S3
echo "Uploading template $TEMPLATE_FILE to s3://$S3_BUCKET_NAME/$TEMPLATE_KEY"
aws --endpoint-url=$LOCALSTACK_ENDPOINT s3 cp "$TEMPLATE_FILE" "s3://$S3_BUCKET_NAME/$TEMPLATE_KEY"

# --- DEPLOYMENT FIX ---
# The previous `cloudformation deploy` failed because its *internal* S3 PUT operation
# was not respecting the LocalStack endpoint/credentials correctly.
# We now use `create-stack` and reference the template via the S3 URL.

# 4. Deploy the stack using the template's S3 URL
# Error: "aws ... cloudformation deploy ..." automatically uploading (or "staging") the template to S3 itself before creating the stack.
# it was confused and tried to talk to the real AWS S3
echo "Deploying stack $STACK_NAME from S3 URL: $TEMPLATE_S3_URL"

# use "create-stack" "update-stack"
# tell the cloudformation to fetch template from Localstack s3
aws --endpoint-url=$LOCALSTACK_ENDPOINT cloudformation create-stack \
    --stack-name $STACK_NAME \
    --template-url "$TEMPLATE_S3_URL" \
    --capabilities CAPABILITY_IAM \
    --disable-rollback

# 5. Wait for deployment to complete
echo "Waiting for stack $STACK_NAME creation to complete..."
aws --endpoint-url=$LOCALSTACK_ENDPOINT cloudformation wait stack-create-complete \
    --stack-name $STACK_NAME

echo "Stack $STACK_NAME deployed successfully."

# 6. Get Load Balancer DNS
echo "Fetching Load Balancer DNS (if available)..."
aws --endpoint-url=$LOCALSTACK_ENDPOINT elbv2 describe-load-balancers \
    --query "LoadBalancers[0].DNSName" --output text

