package site.nomoreparties.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import site.nomoreparties.stellarburgers.model.UserAuthorization;
import site.nomoreparties.stellarburgers.model.response.MessageResponse;
import site.nomoreparties.stellarburgers.model.response.UserResponse;
import site.nomoreparties.stellarburgers.service.UserService;
import site.nomoreparties.stellarburgers.service.Utils;

public abstract class TestBase {
    protected UserAuthorization user;
    protected String accessToken;

    @Before
    public void startUp() {
        RestAssured.baseURI= "https://stellarburgers.nomoreparties.site/";
        user = Utils.generateUserAuthorization();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            Response response = UserService.deleteUser(accessToken, user);
            checkStatusCodeResponse(response, HttpStatus.SC_ACCEPTED);
        }
    }

    @Step("Check response satus code")
    public void checkStatusCodeResponse(Response response, int code){
        response.then()
                .statusCode(code);
    }

    @Step("Check response with error message")
    public void checkErrorMessageResponse(String expectedMessage, MessageResponse messageResponse){
        Assert.assertFalse(messageResponse.isSuccess());
        Assert.assertEquals(expectedMessage, messageResponse.getMessage());
    }

    protected UserResponse initUniqueUser() {
        Response response = UserService.registerUser(user);
        checkStatusCodeResponse(response, HttpStatus.SC_OK);
        UserResponse userResponse = response.body()
                .as(UserResponse.class);
        accessToken = Utils.getAuthToken(userResponse);

        return userResponse;
    }
}
