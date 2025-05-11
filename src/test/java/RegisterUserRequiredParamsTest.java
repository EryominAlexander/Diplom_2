import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import json.UserData;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static data.StellarBurgersData.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class RegisterUserRequiredParamsTest {
    private String email;
    private String name;
    private String password;

    public RegisterUserRequiredParamsTest(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
    @Parameterized.Parameters
    public static Object[][] data(){
        return new Object[][]{
                {"" ,"testName" + RandomStringUtils.randomAlphabetic(10), "testPassword"},
                {"testEmail" + RandomStringUtils.randomAlphabetic(10) + "@yandex.ru", "", "testPassword" },
                {"testEmail" + RandomStringUtils.randomAlphabetic(10) + "@yandex.ru", "testName" + RandomStringUtils.randomAlphabetic(10),""}
        };
    }
    @Before
    public void setUp(){
        RestAssured.baseURI = BASE_URL;
    }
    @Test
    public void createUserWithOutRequiredParams(){
        UserData userData = new UserData(email, name, password);
        Response negativeResponse = postCreateUser(userData);
        checkNegativeResponsePostAuthRegisterUserWithOutRequiredParam(negativeResponse);

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
    @Step("Проверка ответа POST /api/auth/register. Не переданы обязательные параметры для создания пользователя")
    public void checkNegativeResponsePostAuthRegisterUserWithOutRequiredParam(Response response){
        response.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo(NO_REQUIRED_PARAM_FOR_CREATE_USER))
                .and().statusCode(403);
    }
}
