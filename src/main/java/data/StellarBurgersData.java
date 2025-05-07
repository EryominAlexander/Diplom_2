package data;

public class StellarBurgersData {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    public static final String POST_AUTH_REGISTER = "/api/auth/register";
    public static final String POST_AUTH_LOGIN = "/api/auth/login";
    public static final String DELETE_AUTH_USER = "/api/auth/user";
    public static final String PATCH_EDIT_USER = "/api/auth/user";
    public static final String POST_CREATE_ORDER = "/api/orders";
    public static final String GET_USER_ORDERS = "/api/orders";

    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String NO_REQUIRED_PARAM_FOR_CREATE_USER = "Email, password and name are required fields";
    public static final String LOGIN_WITH_WRONG_CRED = "email or password are incorrect";
    public static final String EDIT_USER_WITH_OUT_AUTH = "You should be authorised";
    public static final String CREATE_ORDER_WITH_WRONG_ID = "One or more ids provided are incorrect";
    public static final String CREATE_ORDER_WITH_WITH_OUT_ID = "Ingredient ids must be provided";
}
