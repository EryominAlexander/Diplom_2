import static data.StellarBurgersData.*;
import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import json.CreatedUserData;
import json.LoginData;
import json.UserData;
import lib.ApiCore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class RegisterUserTest {
    private String testEmail;
    private String testName;
    private String testPassword;

    @Before
    public void setUp(){
        RestAssured.baseURI = BASE_URL;
        Random random = new Random();
        testEmail = "testEmail" + random.nextInt(1000000) + "@yandex.ru";
        testName = "testName" + random.nextInt(1000000);
        testPassword = "testPassword" + random.nextInt(1000000);
    }
    @Test
    public void createUniqueUserTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );
        Response createUserResponse = apiCore.postCreateUser( userData );
        apiCore.checkResponsePostAuthRegister(createUserResponse, testEmail, testName);
    }
    @Test
    public void createNotUniqueUserTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );
        Response positiveResponse = apiCore.postCreateUser( userData );
        Response negativeResponse = apiCore.postCreateUser( userData );
        apiCore.checkNegativeResponsePostAuthRegisterUserExists(negativeResponse);
    }

    @After
    public void quit(){
        LoginData userLoginData = new LoginData(testEmail, testPassword);
        CreatedUserData createdUserData = given()
                .header("Content-Type", "application/json")
                .and()
                .body(userLoginData)
                .when()
                .post(POST_AUTH_LOGIN)
                .as(CreatedUserData.class);

        given()
                .header("Content-Type", "application/json")
                .and()
                .header("Authorization",createdUserData.getAccessToken())
                .when()
                .delete(DELETE_AUTH_USER);
    }
}
