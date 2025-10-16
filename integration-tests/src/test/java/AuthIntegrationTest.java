import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {

    @BeforeAll
    static void setUp(){
        // 4004 is the address of api-gateway
        RestAssured.baseURI = "http://localhost:4004/";
    }

    @Test
    public void shouldReturnOKWithValidToken(){
        // 1. Arrange
        // 2. act
        // 3. assert
        String loginPayload = """
               {
                "email": "testuser@test.com",
                "password": "password123"
               }
              """;
        Response res = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();
        System.out.println("Generated Token: " + res.jsonPath().getString("token"));

    }

    @Test
    public void shouldReturnUnauthorizedOnInvalidLogin(){

        String loginPayload = """
               {
                "email": "invaliduser@test.com",
                "password": "password123"
               }
              """;
        given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);


    }
}
