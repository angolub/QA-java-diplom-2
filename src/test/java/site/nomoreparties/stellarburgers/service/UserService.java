package site.nomoreparties.stellarburgers.service;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.model.UserAuthorization;

import static io.restassured.RestAssured.given;

public class UserService {
    private final static String registerURL = "/api/auth/register";
    private final static String loginURL = "/api/auth/login";
    private final static String userURL = "/api/auth/user";

    @Step("Send POST request to register user")
    public static Response registerUser(UserAuthorization user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(registerURL);
    }

    @Step("Send POST request to login user")
    public static Response loginUser(UserAuthorization user) {
        UserAuthorization authUser = UserAuthorization.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        return given()
                .header("Content-type", "application/json")
                .body(authUser)
                .when()
                .post(loginURL);
    }

    @Step("Send DELETE request to delete user")
    public static Response deleteUser(String token, UserAuthorization user) {
        UserAuthorization deletedUser = UserAuthorization.builder()
                .email(user.getEmail())
                .build();

        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(token)
                .and()
                .body(deletedUser)
                .when()
                .delete(userURL);
    }

    @Step("Send PATCH request to update user with authorization")
    public static Response updateUser(String token, UserAuthorization user) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(token)
                .and()
                .body(user)
                .when()
                .patch(userURL);
    }

    @Step("Send PATCH request to update user without authorization")
    public static Response updateUser(UserAuthorization user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .patch(userURL);
    }

}
