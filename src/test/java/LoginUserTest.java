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

import static data.StellarBurgersData.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest {
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
    public void existedUserLoginTest(){
        UserData userData = new UserData(testEmail, testName, testPassword);
        Response responseCreateUser = postCreateUser(userData);
        Response responseLoginUser = postLogin(testEmail, testPassword);
        checkPositiveLoginResponse(responseLoginUser);
    }
    @Test
    public void wrongLoginTest(){
        UserData userData = new UserData(testEmail, testName, testPassword);
        Response responseCreateUser = postCreateUser(userData);
        Response responseNegativeLogin = postLogin( "wrong" + testEmail , testPassword);
        checkNegativeLoginResponse(responseNegativeLogin);

    }
    @Test
    public void wrongPasswordTest(){
        UserData userData = new UserData(testEmail, testName, testPassword);
        Response responseCreateUser = postCreateUser(userData);
        Response responseNegativeLogin = postLogin(testEmail,"wrong" + testPassword);
        checkNegativeLoginResponse(responseNegativeLogin);
    }
    @After
    public void deleteData(){
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
    @Step("Корректная авторизация пользователя POST /api/auth/login")
    public Response postLogin(String email, String password){
        LoginData loginData = new LoginData(email, password);
        return given()
                .header("Content-Type", "application/json")
                .and()
                .body(loginData)
                .when()
                .post(POST_AUTH_LOGIN);
    }
    @Step("Проверка ответа POST /api/auth/login после корректной авторизации")
    public void checkPositiveLoginResponse(Response response){
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("accessToken", notNullValue())
                .and().assertThat().body("refreshToken", notNullValue())
                .and().assertThat().body("user.email", equalTo(testEmail.toLowerCase()))
                .and().assertThat().body("user.name", equalTo(testName))
                .and().statusCode(200);
    }
    @Step("Проверка ответа POST /api/auth/login после авторизации с некорректными данными")
    public void checkNegativeLoginResponse(Response response){
        response.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo(LOGIN_WITH_WRONG_CRED))
                .and().statusCode(401);
    }
}
