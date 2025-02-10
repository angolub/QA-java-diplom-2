package site.nomoreparties.stellarburgers.service;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.model.Burger;

import static io.restassured.RestAssured.given;

public class OrderService {
    private final static String getIngredientsURL = "/api/ingredients";
    private final static String orderURL = "/api/orders";

    @Step("Send GET request to get ingredients")
    public static Response getIngredients() {
        return given()
                .get(getIngredientsURL);
    }

    @Step("Send POST request to create order")
    public static Response createOrder(String token, Burger order) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(token)
                .and()
                .body(order)
                .when()
                .post(orderURL);
    }

    @Step("Send POST request to create order without authorization")
    public static Response createOrder(Burger order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(orderURL);
    }

    @Step("Send GET request to get user's orders")
    public static Response getOrders(String token) {
        return given()
                .auth().oauth2(token)
                .get(orderURL);
    }

    @Step("Send GET request to get all orders")
    public static Response getAllOrders() {
        return given()
                .get(String.format("%s/all", orderURL));
    }
}
