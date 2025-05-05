import static data.StellarBurgersData.*;
import static data.StellarBurgersData.USER_ALREADY_EXISTS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import json.CreatedUserData;
import json.LoginData;
import json.UserData;
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
        UserData userData = new UserData( testEmail, testName, testPassword );
        Response createUserResponse = postCreateUser( userData );
        checkResponsePostAuthRegister(createUserResponse, testEmail, testName);
    }
    @Test
    public void createNotUniqueUserTest(){
        UserData userData = new UserData( testEmail, testName, testPassword );
        Response positiveResponse = postCreateUser( userData );
        Response negativeResponse = postCreateUser( userData );
        checkNegativeResponsePostAuthRegisterUserExists(negativeResponse);
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
    @Step("Создание пользователя POST /api/auth/register")
    public Response postCreateUser(UserData userData){
        return given()
                .header("Content-Type", "application/json")
                .and()
                .body(userData)
                .when()
                .post(POST_AUTH_REGISTER);
    }
    @Step("Проверка ответа POST /api/auth/register. Положительные ответ")
    public void checkResponsePostAuthRegister(Response response, String testEmail, String testName){
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("accessToken", notNullValue())
                .and().assertThat().body("user.email", equalTo(testEmail.toLowerCase()))
                .and().assertThat().body("user.name", equalTo(testName))
                .and().assertThat().body("refreshToken", notNullValue())
                .and().statusCode(200);
    }
    @Step("Проверка ответа POST /api/auth/register. Пользователь уже создан")
    public void checkNegativeResponsePostAuthRegisterUserExists(Response response){
        response.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo(USER_ALREADY_EXISTS))
                .and().statusCode(403);
    }
}
