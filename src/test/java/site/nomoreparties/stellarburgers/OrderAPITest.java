package site.nomoreparties.stellarburgers;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.model.Ingredient;
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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;

public class OrderAPITest extends TestBase {
    private final int INGREDIENTS_COUNT = 1;
    private final String INVALID_INGREDIENT_HASH = "00a0";
    private final String ASSERT_MESSAGE_NO_INGREDIENTS = "Нет ингредиентов для создания бургера";
    private final String NO_INGREDIENTS_IN_BURGER_MESSAGE = "Ingredient ids must be provided";

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
        checkOrderResponse(burger, orderResponse);
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
    public void checkOrderResponse(Burger burger, OrderResponse orderResponse){
        Assert.assertTrue(orderResponse.isSuccess());
        Assert.assertNotNull(orderResponse.getName());
        Assert.assertNotNull(orderResponse.getOrder());

        OrderDetail<Ingredient> orderDetail = orderResponse.getOrder();
        Assert.assertNotNull(orderDetail.getId());
        Assert.assertNotNull(orderDetail.getOwner());
        Assert.assertNotNull(orderDetail.getStatus());
        Assert.assertNotNull(orderDetail.getName());
        Assert.assertNotNull(orderDetail.getCreatedAt());
        Assert.assertNotNull(orderDetail.getUpdatedAt());
        assertThat(orderDetail.getNumber(), greaterThan(0));
        assertThat(orderDetail.getPrice(), greaterThan(0.0f));
        Assert.assertNotNull(orderDetail.getIngredients());
        Assert.assertEquals(INGREDIENTS_COUNT, orderDetail.getIngredients().size());

        burger.getIngredients().forEach(ingredientId -> {
             Ingredient ingredient = orderDetail.getIngredients().stream()
                    .filter(ing -> ing.getId().equals(ingredientId))
                    .findFirst()
                    .orElseGet(null);
            Assert.assertNotNull(ingredient);
        });

        OrderOwner orderOwner = orderDetail.getOwner();
        Assert.assertEquals(user.getEmail(), orderOwner.getEmail());
        Assert.assertEquals(user.getName(), orderOwner.getName());
        Assert.assertNotNull(orderOwner.getCreatedAt());
        Assert.assertNotNull(orderOwner.getUpdatedAt());
    }

    @Step("Check response order without authorization")
    public void checkOrderResponseWithoutAuthorization(OrderResponse orderResponse){
        Assert.assertTrue(orderResponse.isSuccess());
        Assert.assertNotNull(orderResponse.getName());
        Assert.assertNotNull(orderResponse.getOrder());

        OrderDetail orderDetail = orderResponse.getOrder();
        assertThat(orderDetail.getNumber(), greaterThan(0));
        Assert.assertNull(orderDetail.getId());
        Assert.assertNull(orderDetail.getOwner());
        Assert.assertNull(orderDetail.getStatus());
        Assert.assertNull(orderDetail.getName());
        Assert.assertNull(orderDetail.getCreatedAt());
        Assert.assertNull(orderDetail.getUpdatedAt());
        Assert.assertEquals(0, orderDetail.getPrice(), 0.0f);
        Assert.assertNull(orderDetail.getIngredients());
    }

    @Step("Check response order collection with authorization")
    public void checkOrderCollectionResponse(OrderResponse orderResponse,OrderCollectionResponse orderCollectionResponse){
        Assert.assertTrue(orderCollectionResponse.isSuccess());
        Assert.assertNotNull(orderCollectionResponse.getOrders());
        Assert.assertEquals(1, orderCollectionResponse.getOrders().size());

        OrderDetail<String> actualOrderDetail = orderCollectionResponse.getOrders().get(0);
        OrderDetail<Ingredient> expectedOrderDetail = orderResponse.getOrder();

        Assert.assertEquals(expectedOrderDetail.getId(), actualOrderDetail.getId());
        Assert.assertEquals(expectedOrderDetail.getName(), actualOrderDetail.getName());
        Assert.assertEquals(expectedOrderDetail.getNumber(), actualOrderDetail.getNumber());
        Assert.assertEquals(expectedOrderDetail.getCreatedAt(), actualOrderDetail.getCreatedAt());
        Assert.assertNotNull(actualOrderDetail.getIngredients());
        Assert.assertEquals(expectedOrderDetail.getIngredients().size(), actualOrderDetail.getIngredients().size());
        Assert.assertEquals(expectedOrderDetail.getIngredients().get(0).getId(), actualOrderDetail.getIngredients().get(0));
    }

    @Step("Check response order collection without authorization")
    public void checkAllOrderCollectionResponse(OrderCollectionResponse orderCollectionResponse){
        Assert.assertTrue(orderCollectionResponse.isSuccess());
        Assert.assertNotNull(orderCollectionResponse.getOrders());
        assertThat(orderCollectionResponse.getOrders().size(), greaterThan(0));
        assertThat(orderCollectionResponse.getOrders(), everyItem(instanceOf(OrderDetail.class)));
        orderCollectionResponse.getOrders().forEach(orderDetail -> {
            Assert.assertNotNull(orderDetail.getId());
            Assert.assertNotNull(orderDetail.getName());
            assertThat(orderDetail.getNumber(), greaterThan(0));
            Assert.assertNotNull(orderDetail.getIngredients());
            assertThat(orderDetail.getIngredients().size(), greaterThan(0));
        });
    }

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
}
