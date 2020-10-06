package databaseClasses;

public class tokenLoginDetails {
    private String TokenId;
    private String tokenSchoolId;
    private String tokenTeacherId;
    private String tokenPupilId;
    private String tokenUserName;
    private String tokenPassword;
    private boolean autoLogin;

    public tokenLoginDetails() {
    }

    public String getTokenSchoolId() {
        return tokenSchoolId;
    }

    public void setTokenSchoolId(String tokenSchoolId) {
        this.tokenSchoolId = tokenSchoolId;
    }

    public String getTokenTeacherId() {
        return tokenTeacherId;
    }

    public void setTokenTeacherId(String tokenTeacherId) {
        this.tokenTeacherId = tokenTeacherId;
    }

    public String getTokenPupilId() {
        return tokenPupilId;
    }

    public void setTokenPupilId(String tokenPupilId) {
        this.tokenPupilId = tokenPupilId;
    }

    public String getTokenUserName() {
        return tokenUserName;
    }

    public void setTokenUserName(String tokenUserName) {
        this.tokenUserName = tokenUserName;
    }

    public String getTokenPassword() {
        return tokenPassword;
    }

    public void setTokenPassword(String tokenPassword) {
        this.tokenPassword = tokenPassword;
    }

    public String getTokenId() {
        return TokenId;
    }

    public void setTokenId(String tokenId) {
        TokenId = tokenId;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }
}
