package lib;
import static data.StellarBurgersData.*;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import json.CreatedUserData;
import json.LoginData;
import json.UserData;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class ApiCore {
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
    public void checkPositiveLoginResponse(Response response, String email, String name){
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("accessToken", notNullValue())
                .and().assertThat().body("refreshToken", notNullValue())
                .and().assertThat().body("user.email", equalTo(email.toLowerCase()))
                .and().assertThat().body("user.name", equalTo(name))
                .and().statusCode(200);
    }
    @Step("Проверка ответа POST /api/auth/login после авторизации с некорректными данными")
    public void checkNegativeLoginResponse(Response response){
        response.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo(LOGIN_WITH_WRONG_CRED))
                .and().statusCode(401);
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
    @Step("Получение токена авторизации")
    public String getToken(String email, String password){
        LoginData userLoginData = new LoginData(email, password);
        CreatedUserData createdUserData = given()
                .header("Content-Type", "application/json")
                .and()
                .body(userLoginData)
                .when()
                .post(POST_AUTH_LOGIN)
                .as(CreatedUserData.class);
        return createdUserData.getAccessToken();
    }
    @Step("Обновление данных пользователя PATCH /api/auth/user")
    public Response updateUser(Map<String, String> editData, String token){

        return given()
                .header("Content-Type", "application/json")
                .and()
                .header("Authorization", token)
                .and()
                .body(editData)
                .when()
                .patch(PATCH_EDIT_USER);

    }
    @Step("Проверка ответа PATCH /api/auth/register. Положительные ответ")
    public void checkResponseEditedUser(Response response, String testEmail, String testName){
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.email", equalTo(testEmail.toLowerCase()))
                .and().assertThat().body("user.name", equalTo(testName))
                .and().statusCode(200);
    }
    @Step("Проверка ответа PATCH /api/auth/register. Негативный ответ")
    public void checkNegativeResponseEditedUser(Response response){
        response.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo(EDIT_USER_WITH_OUT_AUTH))
                .and().statusCode(401);
    }
}
