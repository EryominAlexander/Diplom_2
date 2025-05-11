import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import json.*;
import lib.ApiCore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static data.StellarBurgersData.*;
import static data.StellarBurgersData.DELETE_AUTH_USER;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class GetOrderTest {
    private String testEmail;
    private String testName;
    private String testPassword;
    @Before
    public void setUp() {
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
    public void getUserOrderWithOutAuthTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );

        Response createUserResponse = apiCore.postCreateUser( userData );
        String token = apiCore.getToken(testEmail, testPassword);

        List<String> idList = new ArrayList<>();
        idList.add("61c0c5a71d1f82001bdaaa6d");
        Map<String, List<String>> ingredients = new HashMap<>();
        ingredients.put("ingredients", idList );

        Response createOrderResponse = apiCore.createOrder(ingredients, token);
        Response negativeResponseGetUserOrders = apiCore.getUserOrders("");
        apiCore.checkNegativeResponseGetUserOrders(negativeResponseGetUserOrders);
    }
    @Test
    public void getUserOrderWithAuthTest(){
        ApiCore apiCore = new ApiCore();
        UserData userData = new UserData( testEmail, testName, testPassword );
        //create user
        Response createUserResponse = apiCore.postCreateUser( userData );
        String token = apiCore.getToken(testEmail, testPassword);

        List<String> idList = new ArrayList<>();
        idList.add("61c0c5a71d1f82001bdaaa6d");
        Map<String, List<String>> ingredients = new HashMap<>();
        ingredients.put("ingredients", idList );

        //create order
        Response createOrderResponse = apiCore.createOrder(ingredients, token);

        UserOrders userOrders = given()
                .header("Content-Type", "application/json")
                .and()
                .header("Authorization", token)
                .get(GET_USER_ORDERS)
                .as(UserOrders.class);

        List<OrderData> orderList = userOrders.getOrders();
        List<String> ingredientsList = new ArrayList<>();
        for ( OrderData data : orderList){
            ingredientsList = data.getIngredients();
        }
        Response getUserOrders = apiCore.getUserOrders(token);
        apiCore.checkResponseGetUserOrders(getUserOrders);
        assertEquals(idList, ingredientsList );
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
