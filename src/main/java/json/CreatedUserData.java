package json;

public class CreatedUserData {
    private String success;
    private ShortUserData shortUserData;
    private String accessToken;
    private String refreshToken;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public ShortUserData getShortUserData() {
        return shortUserData;
    }

    public void setShortUserData(ShortUserData shortUserData) {
        this.shortUserData = shortUserData;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
