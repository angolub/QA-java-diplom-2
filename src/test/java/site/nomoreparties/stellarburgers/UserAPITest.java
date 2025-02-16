package site.nomoreparties.stellarburgers;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import site.nomoreparties.stellarburgers.model.UserAuthorization;
import site.nomoreparties.stellarburgers.model.response.MessageResponse;
import site.nomoreparties.stellarburgers.model.response.UserResponse;
import site.nomoreparties.stellarburgers.service.UserService;
import site.nomoreparties.stellarburgers.service.Utils;

public classUserAPITest extends TestBase {

    private final String DUBLICATE_USER_MESSAGE = "User already exists";
    private final String NO_REQUIRED_USER_FIELDS_MESSAGE = "Email, password and name are required fields";
    private final String EMAIL_PASSWORD_INCORRECT_MESSAGE = "email or password are incorrect";
    private final String NOT_AUTHORISED_USER_MESSAGE = "You should be authorised";

    @Test
    @DisplayName("Test user registration (positive testcase)")
    public void registerUniqueUserTest() {
        UserResponse userResponse = initUniqueUser();
        checkUserResponse(user, userResponse);
    }

    @Test
    @DisplayName("Test existing user's registration (duplicate registration)")
    public void registerDublicateUserTest() {
        initUniqueUser();

        Response response = UserService.registerUser(user);
        checkStatusCodeResponse(response, HttpStatus.SC_FORBIDDEN);
        MessageResponse messageResponse = response.body()
                .as(MessageResponse.class);
        checkErrorMessageResponse(DUBLICATE_USER_MESSAGE, messageResponse);
    }

    @Test
    @DisplayName("Test user registration without email field")
    public void registerUserWithoutEmailTest() {
        UserAuthorization userWithoutEmail = UserAuthorization.builder()
                .password(user.getPassword())
                .name(user.getName())
                .build();
        Response response = UserService.registerUser(userWithoutEmail);
        checkStatusCodeResponse(response, HttpStatus.SC_FORBIDDEN);
        MessageResponse messageResponse = response.body()
                .as(MessageResponse.class);
        checkErrorMessageResponse(NO_REQUIRED_USER_FIELDS_MESSAGE, messageResponse);
    }

    @Test
    @DisplayName("Test user registration without password field")
    public void registerUserWithoutPasswordTest() {
        UserAuthorization userWithoutPassword = UserAuthorization.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
        Response response = UserService.registerUser(userWithoutPassword);
        checkStatusCodeResponse(response, HttpStatus.SC_FORBIDDEN);
        MessageResponse messageResponse = response.body()
                .as(MessageResponse.class);
        checkErrorMessageResponse(NO_REQUIRED_USER_FIELDS_MESSAGE, messageResponse);
    }

    @Test
    @DisplayName("Test user registration without name field")
    public void registerUserWithoutNameTest() {
        UserAuthorization userWithoutName = UserAuthorization.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        Response response = UserService.registerUser(userWithoutName);
        checkStatusCodeResponse(response, HttpStatus.SC_FORBIDDEN);
        MessageResponse messageResponse = response.body()
                .as(MessageResponse.class);
        checkErrorMessageResponse(NO_REQUIRED_USER_FIELDS_MESSAGE, messageResponse);
    }

    @Test
    @DisplayName("Test login as an existing user (positive testcase)")
    public void loginUserPositiveTest() {
        initUniqueUser();
        UserResponse userResponse = loginUser();
        checkUserResponse(user, userResponse);
    }

    @Test
    @DisplayName("Test login with invalid password")
    public void loginUserWithInvalidPasswordTest() {
        initUniqueUser();
        user.setPassword(String.format("%swrong", user.getPassword()));
        Response response = UserService.loginUser(user);
        checkStatusCodeResponse(response, HttpStatus.SC_UNAUTHORIZED);
        MessageResponse messageResponse = response.body()
                .as(MessageResponse.class);
        checkErrorMessageResponse(EMAIL_PASSWORD_INCORRECT_MESSAGE, messageResponse);
    }

    @Test
    @DisplayName("Test login with invalid email")
    public void loginUserWithInvalidEmailTest() {
        user = Utils.generateUserAuthorization();
        Response response = UserService.loginUser(user);
        checkStatusCodeResponse(response, HttpStatus.SC_UNAUTHORIZED);
        MessageResponse messageResponse = response.body()
                .as(MessageResponse.class);
        checkErrorMessageResponse(EMAIL_PASSWORD_INCORRECT_MESSAGE, messageResponse);
    }

    @Test
    @DisplayName("Test update user (positive testcase)")
    public void updateUserWithAuthorizationTest() {
        initUniqueUser();

        user.setEmail(String.format("update_%s", user.getEmail()));
        user.setPassword(String.format("update_%s", user.getPassword()));
        user.setName(String.format("Update %s", user.getName()));

        Response response = UserService.updateUser(accessToken, user);
        checkStatusCodeResponse(response, HttpStatus.SC_OK);

        UserResponse userResponse = response.body()
                .as(UserResponse.class);
        checkUpdateUserResponse(user, userResponse);

        loginUser();
    }

    @Test
    @DisplayName("Test update user without authorization")
    public void updateUserWithoutAuthorizationTest() {
        initUniqueUser();

        user.setEmail(String.format("update_%s", user.getEmail()));
        user.setPassword(String.format("update_%s", user.getPassword()));
        user.setName(String.format("Update %s", user.getName()));

        Response response = UserService.updateUser(user);
        checkStatusCodeResponse(response, HttpStatus.SC_UNAUTHORIZED);

        MessageResponse messageResponse = response.body()
                .as(MessageResponse.class);
        checkErrorMessageResponse(NOT_AUTHORISED_USER_MESSAGE, messageResponse);
    }

    @Step("Check response user")
    public void checkUserResponse(UserAuthorization expectedUser, UserResponse userResponse){
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(userResponse.isSuccess())
                    .as("Проверяем значение поля success").isTrue();
            softAssertions.assertThat(userResponse.getUser())
                    .as("Проверяем значение поля user").isNotNull();
            softAssertions.assertThat(userResponse.getAccessToken())
                    .as("Проверяем значение поля access token")
                    .isNotBlank()
                    .startsWith(Utils.ACCESS_TOKEN_PREFIX);
            softAssertions.assertThat(userResponse.getRefreshToken())
                    .as("Проверяем значение поля refresh token")
                    .isNotBlank();
        });

        UserAuthorization userAuthorization = userResponse.getUser();

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(userAuthorization.getEmail())
                    .as("Проверяем значение поля email").isEqualTo(expectedUser.getEmail());
            softAssertions.assertThat(userAuthorization.getName())
                    .as("Проверяем значение поля name").isEqualTo(expectedUser.getName());

        });
    }

    @Step("Check response update user")
    public void checkUpdateUserResponse(UserAuthorization expectedUser, UserResponse userResponse){
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(userResponse.isSuccess())
                    .as("Проверяем значение поля success").isTrue();
            softAssertions.assertThat(userResponse.getUser())
                    .as("Проверяем значение поля user").isNotNull();
        });

        UserAuthorization userAuthorization = userResponse.getUser();

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(userAuthorization.getEmail())
                    .as("Проверяем значение поля email").isEqualTo(expectedUser.getEmail());
            softAssertions.assertThat(userAuthorization.getName())
                    .as("Проверяем значение поля name").isEqualTo(expectedUser.getName());

        });
    }

    private UserResponse loginUser() {
        Response response = UserService.loginUser(user);
        checkStatusCodeResponse(response, HttpStatus.SC_OK);
        UserResponse userResponse = response.body()
                .as(UserResponse.class);
        accessToken = Utils.getAuthToken(userResponse);

        return userResponse;
    }
}
