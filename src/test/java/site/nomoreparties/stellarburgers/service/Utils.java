package site.nomoreparties.stellarburgers.service;

import com.github.javafaker.Faker;
import site.nomoreparties.stellarburgers.model.UserAuthorization;
import site.nomoreparties.stellarburgers.model.response.UserResponse;

import java.util.Locale;

public class Utils {

    public static final String ACCESS_TOKEN_PREFIX = "Bearer ";

    public static UserAuthorization generateUserAuthorization() {
        Faker faker = new Faker(new Locale("ru-RU"));
        String email = faker.bothify("??????##@yandex.ru");
        String password = faker.regexify("[a-z1-9]{8}");
        String name = faker.name().fullName();

        return new UserAuthorization(email, password, name);
    }

    public static String getAuthToken(UserResponse user){
        String accessToken  = user.getAccessToken();
        return accessToken.substring(ACCESS_TOKEN_PREFIX.length());
    }
}
