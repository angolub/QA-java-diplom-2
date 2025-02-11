package site.nomoreparties.stellarburgers;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.model.UserAuthorization;
import site.nomoreparties.stellarburgers.model.response.Ingredient;
import site.nomoreparties.stellarburgers.model.Burger;
import site.nomoreparties.stellarburgers.model.response.IngredientResponse;
import site.nomoreparties.stellarburgers.model.response.MessageResponse;
import site.nomoreparties.stellarburgers.model.response.OrderCollectionResponse;
import site.nomoreparties.stellarburgers.model.response.OrderResponse;
import site.nomoreparties.stellarburgers.model.response.orderdetail.OrderDetail;
import site.nomoreparties.stellarburgers.model.response.orderdetail.OrderOwner;
import site.nomoreparties.stellarburgers.service.OrderService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;


public class OrderAPITest extends TestBase {
    private final int INGREDIENTS_COUNT = 1;
    private final String INVALID_INGREDIENT_HASH = "00a0";
    private final String ASSERT_MESSAGE_NO_INGREDIENTS = "Нет ингредиентов для создания бургера";
    private final String NO_INGREDIENTS_IN_BURGER_MESSAGE = "Ingredient ids must be provided";

    private  Burger makeBurger() {
        Response response = OrderService.getIngredients();
        checkStatusCodeResponse(response, HttpStatus.SC_OK);
        IngredientResponse ingredientResponse = response.body()
                .as(IngredientResponse.class);

        List<Ingredient> ingredients = ingredientResponse.getData();
        Assert.assertNotNull(ASSERT_MESSAGE_NO_INGREDIENTS, ingredients);
        assertThat(ASSERT_MESSAGE_NO_INGREDIENTS, ingredients.size(), greaterThan(0));

        String firstIngredient = ingredients.get(0).getId();
        return new Burger(List.of(firstIngredient));
    }

    @Before
    public void startUp() {
        super.startUp();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    @DisplayName("Test create order (positive testcase)")
    public void createOrderPositiveTest() {
        initUniqueUser();
        Burger burger = makeBurger();

        Response response = OrderService.createOrder(accessToken, burger);
        checkStatusCodeResponse(response, HttpStatus.SC_OK);
        OrderResponse orderResponse = response.body()
                .as(OrderResponse.class);
        checkOrderResponseWithAutorization(burger, orderResponse);
    }

    @Test
    @DisplayName("Test create order without authorization (positive testcase)")
    public void createOrderWithoutAuthorizationTest() {
        Burger burger = makeBurger();

        Response response = OrderService.createOrder(burger);
        checkStatusCodeResponse(response, HttpStatus.SC_OK);
        OrderResponse orderResponse = response.body()
                .as(OrderResponse.class);

        checkOrderResponseWithoutAuthorization(orderResponse);
    }

    @Test
    @DisplayName("Test create order without ingredients")
    public void createOrderWithoutIngredientsTest() {
        initUniqueUser();
        Burger burger = new Burger(Collections.<String>emptyList());

        Response response = OrderService.createOrder(accessToken, burger);
        checkStatusCodeResponse(response, HttpStatus.SC_BAD_REQUEST);
        MessageResponse messageResponse = response.body()
                .as(MessageResponse.class);
        checkErrorMessageResponse(NO_INGREDIENTS_IN_BURGER_MESSAGE, messageResponse);
    }

    @Test
    @DisplayName("Test create order with invalid ingredients")
    public void createOrderWithInvalidIngredientsTest() {
        initUniqueUser();
        Burger burger = new Burger(List.of(INVALID_INGREDIENT_HASH));

        Response response = OrderService.createOrder(accessToken, burger);
        checkStatusCodeResponse(response, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Test get user orders")
    public void getUserOrdersTest() {
        initUniqueUser();
        Burger burger = makeBurger();

        Response response = OrderService.createOrder(accessToken, burger);
        checkStatusCodeResponse(response, HttpStatus.SC_OK);
        OrderResponse orderResponse = response.body()
                .as(OrderResponse.class);

        response = OrderService.getOrders(accessToken);

        OrderCollectionResponse orderCollectionResponseResponse = response.body()
                .as(OrderCollectionResponse.class);
        checkOrderCollectionResponse(orderResponse, orderCollectionResponseResponse);
    }

    @Test
    @DisplayName("Test get all orders")
    public void getAllOrdersTest() {
        Response response = OrderService.getAllOrders();

        OrderCollectionResponse orderCollectionResponseResponse = response.body()
                .as(OrderCollectionResponse.class);
        checkAllOrderCollectionResponse(orderCollectionResponseResponse);
    }

    @Step("Check response order with authorization")
    private void checkOrderResponseWithAutorization(Burger burger, OrderResponse orderResponse){
        checkOrderResponse(orderResponse);

        OrderDetail<Ingredient> orderDetail = orderResponse.getOrder();

        checkOrderDetail(burger, orderDetail);

        OrderOwner orderOwner = orderDetail.getOwner();

        checkOrderOwner(user, orderOwner);
    }

    @Step("Check response order")
    private void checkOrderResponse(OrderResponse orderResponse){
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(orderResponse.isSuccess())
                    .as("Проверяем значение поля success").isTrue();
            softAssertions.assertThat(orderResponse.getName())
                    .as("Проверяем значение поля name").isNotBlank();
            softAssertions.assertThat(orderResponse.getOrder())
                    .as("Проверяем значение поля order").isNotNull();
        });
    }

    @Step("Check order detail object")
    private void checkOrderDetail(Burger burger, OrderDetail<Ingredient> orderDetail) {
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(orderDetail.getId())
                    .as("Проверяем значение поля id").isNotBlank();
            softAssertions.assertThat(orderDetail.getOwner())
                    .as("Проверяем значение поля owner").isNotNull();
            softAssertions.assertThat(orderDetail.getName())
                    .as("Проверяем значение поля name").isNotBlank();
            softAssertions.assertThat(orderDetail.getCreatedAt())
                    .as("Проверяем значение поля createdAt").isNotBlank();
            softAssertions.assertThat(orderDetail.getUpdatedAt())
                    .as("Проверяем значение поля updatedAt").isNotBlank();
            softAssertions.assertThat(orderDetail.getNumber())
                    .as("Проверяем значение поля number").isGreaterThan(0);
            softAssertions.assertThat(orderDetail.getPrice())
                    .as("Проверяем значение поля price").isGreaterThan(0.0f);
            softAssertions.assertThat(orderDetail.getIngredients())
                    .as("Проверяем значение поля ingredients")
                    .size()
                    .isEqualTo(INGREDIENTS_COUNT)
                    .returnToIterable()
                    .extracting(Ingredient::getId)
                    .contains(burger.getIngredients().get(0));
        });
    }

    @Step("Check order owner object")
    private void checkOrderOwner(UserAuthorization expectedUser, OrderOwner orderOwner) {
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(orderOwner.getEmail())
                    .as("Проверяем значение поля email").isEqualTo(expectedUser.getEmail());
            softAssertions.assertThat(orderOwner.getName())
                    .as("Проверяем значение поля name").isEqualTo(expectedUser.getName());
            softAssertions.assertThat(orderOwner.getCreatedAt())
                    .as("Проверяем значение поля createdAt").isNotBlank();
            softAssertions.assertThat(orderOwner.getUpdatedAt())
                    .as("Проверяем значение поля updatedAt").isNotBlank();
        });
    }

    @Step("Check response order without authorization")
    public void checkOrderResponseWithoutAuthorization(OrderResponse orderResponse){
        checkOrderResponse(orderResponse);

        OrderDetail<Ingredient> orderDetail = orderResponse.getOrder();

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(orderDetail.getId())
                    .as("Проверяем значение поля id").isNull();
            softAssertions.assertThat(orderDetail.getOwner())
                    .as("Проверяем значение поля owner").isNull();
            softAssertions.assertThat(orderDetail.getName())
                    .as("Проверяем значение поля name").isNull();
            softAssertions.assertThat(orderDetail.getCreatedAt())
                    .as("Проверяем значение поля createdAt").isNull();
            softAssertions.assertThat(orderDetail.getUpdatedAt())
                    .as("Проверяем значение поля updatedAt").isNull();
            softAssertions.assertThat(orderDetail.getNumber())
                    .as("Проверяем значение поля number").isGreaterThan(0);
            softAssertions.assertThat(orderDetail.getPrice())
                    .as("Проверяем значение поля price").isEqualTo(0.0f);
            softAssertions.assertThat(orderDetail.getIngredients())
                    .as("Проверяем значение поля ingredients")
                    .isNull();
        });
    }

    @Step("Check response order collection with authorization")
    public void checkOrderCollectionResponse(OrderResponse orderResponse,OrderCollectionResponse orderCollectionResponse) {
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(orderCollectionResponse.isSuccess())
                    .as("Проверяем значение поля success").isTrue();
            softAssertions.assertThat(orderCollectionResponse.getOrders())
                    .as("Проверяем значение поля order")
                    .size()
                    .isEqualTo(1);
        });

        OrderDetail<String> actualOrderDetail = orderCollectionResponse.getOrders().get(0);
        OrderDetail<Ingredient> expectedOrderDetail = orderResponse.getOrder();

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actualOrderDetail.getId())
                    .as("Проверяем значение поля id")
                    .isEqualTo(expectedOrderDetail.getId());
            softAssertions.assertThat(actualOrderDetail.getName())
                    .as("Проверяем значение поля name")
                    .isEqualTo(expectedOrderDetail.getName());
            softAssertions.assertThat(actualOrderDetail.getNumber())
                    .as("Проверяем значение поля number")
                    .isEqualTo(expectedOrderDetail.getNumber());
            softAssertions.assertThat(actualOrderDetail.getCreatedAt())
                    .as("Проверяем значение поля createdAt")
                    .isEqualTo(expectedOrderDetail.getCreatedAt());
            softAssertions.assertThat(actualOrderDetail.getIngredients())
                    .as("Проверяем значение поля ingredients")
                    .size()
                    .isEqualTo(actualOrderDetail.getIngredients().size())
                    .returnToIterable()
                    .contains(expectedOrderDetail.getIngredients().get(0).getId());
        });
    }

    @Step("Check response order collection without authorization")
    public void checkAllOrderCollectionResponse(OrderCollectionResponse orderCollectionResponse){
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(orderCollectionResponse.isSuccess())
                    .as("Проверяем значение поля success").isTrue();
            softAssertions.assertThat(orderCollectionResponse.getOrders())
                    .as("Проверяем значение размер коллекции orders")
                    .size()
                    .isGreaterThan(0);

            softAssertions.assertThat(orderCollectionResponse.getOrders())
                    .as("Проверяем значение поля id в коллекции orders")
                    .size()
                    .returnToIterable()
                    .allSatisfy(order -> assertThat(order.getId(), Matchers.notNullValue()));


            softAssertions.assertThat(orderCollectionResponse.getOrders())
                    .as("Проверяем значение поля number в коллекции orders")
                    .size()
                    .returnToIterable()
                    .allSatisfy(order -> assertThat(order.getNumber(), Matchers.greaterThan(0)));
        });
    }
}
