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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static data.StellarBurgersData.*;
import static io.restassured.RestAssured.given;


public class EditUserTest {
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
    public void editEmailWithAuthTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );

        Response createUserResponse = apiCore.postCreateUser( userData );
        String token = apiCore.getToken(testEmail, testPassword);

        String newUserEmail = "new" + testEmail;
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newUserEmail);

        Response editUserResponse = apiCore.updateUser(editData, token);
        apiCore.checkResponseEditedUser(editUserResponse, newUserEmail, testName);
        testEmail = newUserEmail;
    }


    @Test
    public void editNameWithAuthTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );

        Response createUserResponse = apiCore.postCreateUser( userData );
        String token = apiCore.getToken(testEmail, testPassword);

        String newUserName = "new" + testName;
        Map<String, String> editData = new HashMap<>();
        editData.put("name", newUserName);

        Response editUserResponse = apiCore.updateUser(editData, token);
        apiCore.checkResponseEditedUser(editUserResponse, testEmail, newUserName);
        testName = newUserName;
    }

    @Test
    public void editPasswordWithAuthTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );

        Response createUserResponse = apiCore.postCreateUser( userData );
        String token = apiCore.getToken(testEmail, testPassword);

        String newPassword = "new" + testPassword;
        Map<String, String> editData = new HashMap<>();
        editData.put("password", newPassword);

        Response editUserResponse = apiCore.updateUser(editData, token);
        apiCore.checkResponseEditedUser(editUserResponse, testEmail, testName);
        testPassword = newPassword;
    }

    @Test
    public void editAllDataWithAuthTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );

        Response createUserResponse = apiCore.postCreateUser( userData );
        String token = apiCore.getToken(testEmail, testPassword);

        String newUserEmail = "new" + testEmail;
        String newUserName = "new" + testName;
        String newPassword = "new" + testPassword;
        Map<String, String> editData = new HashMap<>();
        editData.put("password", newPassword);
        editData.put("name", newUserName);
        editData.put("email", newUserEmail);

        Response editUserResponse = apiCore.updateUser(editData, token);
        apiCore.checkResponseEditedUser(editUserResponse, newUserEmail, newUserName);
        testPassword = newPassword;
        testName = newUserName;
        testEmail = newUserEmail;
    }
    @Test
    public void editAllDataWithOutAuthTest(){

        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );

        Response createUserResponse = apiCore.postCreateUser( userData );

        String newUserEmail = "new" + testEmail;
        String newUserName = "new" + testName;
        String newPassword = "new" + testPassword;
        Map<String, String> editData = new HashMap<>();
        editData.put("password", newPassword);
        editData.put("name", newUserName);
        editData.put("email", newUserEmail);

        Response editUserResponse = apiCore.updateUser(editData, "");
        apiCore.checkNegativeResponseEditedUser(editUserResponse);
    }
    @Test
    public void editEmailWithOutAuthTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );

        Response createUserResponse = apiCore.postCreateUser( userData );

        String newUserEmail = "new" + testEmail;
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newUserEmail);

        Response editUserResponse = apiCore.updateUser(editData, "");
        apiCore.checkNegativeResponseEditedUser(editUserResponse);
    }


    @Test
    public void editNameWithOutAuthTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );

        Response createUserResponse = apiCore.postCreateUser( userData );

        String newUserName = "new" + testName;
        Map<String, String> editData = new HashMap<>();
        editData.put("name", newUserName);

        Response editUserResponse = apiCore.updateUser(editData, "");
        apiCore.checkNegativeResponseEditedUser(editUserResponse);
    }

    @Test
    public void editPasswordWithOutAuthTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );

        Response createUserResponse = apiCore.postCreateUser( userData );

        String newPassword = "new" + testPassword;
        Map<String, String> editData = new HashMap<>();
        editData.put("password", newPassword);

        Response editUserResponse = apiCore.updateUser(editData, "");
        apiCore.checkNegativeResponseEditedUser(editUserResponse);
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
