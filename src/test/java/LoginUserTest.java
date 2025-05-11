import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import json.CreatedUserData;
import json.LoginData;
import json.UserData;
import lib.ApiCore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static data.StellarBurgersData.*;
import static io.restassured.RestAssured.given;

public class LoginUserTest {
    private String testEmail;
    private String testName;
    private String testPassword;

    @Before
    public void setUp(){
        RestAssured.baseURI = BASE_URL;
        Random random = new Random();
        Faker faker = new Faker();

        FakeValuesService fakeValuesService = new FakeValuesService(
                new Locale("en-GB"), new RandomService());
        testEmail = fakeValuesService.bothify("????##@yandex.ru");
        Matcher emailMatcher = Pattern.compile("\\w{8}\\d{3}@yandex.ru").matcher(testEmail);
        testName = faker.name().firstName();
        testPassword = "testPassword" + random.nextInt(1000000);
    }
    @Test
    public void existedUserLoginTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData(testEmail, testName, testPassword);
        Response responseCreateUser = apiCore.postCreateUser(userData);
        Response responseLoginUser = apiCore.postLogin(testEmail, testPassword);
        apiCore.checkPositiveLoginResponse(responseLoginUser, testEmail, testName);
    }
    @Test
    public void wrongLoginTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData(testEmail, testName, testPassword);
        Response responseCreateUser = apiCore.postCreateUser(userData);
        Response responseNegativeLogin = apiCore.postLogin( "wrong" + testEmail , testPassword);
        apiCore.checkNegativeLoginResponse(responseNegativeLogin);

    }
    @Test
    public void wrongPasswordTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData(testEmail, testName, testPassword);
        Response responseCreateUser = apiCore.postCreateUser(userData);
        Response responseNegativeLogin = apiCore.postLogin(testEmail,"wrong" + testPassword);
        apiCore.checkNegativeLoginResponse(responseNegativeLogin);
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
}
