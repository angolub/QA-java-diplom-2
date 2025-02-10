package site.nomoreparties.stellarburgers.service;

import site.nomoreparties.stellarburgers.model.UserAuthorization;
import site.nomoreparties.stellarburgers.model.response.UserResponse;

public class Utils {

    private static final String EMAIL_POSTFIX = "yandex.ru";
    private static final String USER_PASSWORD = "yandexRu";
    private static final String USER_NAME = "Иван Иванов";

    public static final String ACCESS_TOKEN_PREFIX = "Bearer ";

    private static String generateString(int size)
    {
        // choose a Character random from this String
        String AlphaNumericString = "0123456789abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            int index = (int)((AlphaNumericString.length() - 1) * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static String generateEmail() {
        return String.format("%s@%s", generateString(7), EMAIL_POSTFIX);
    }

    public static UserAuthorization generateUserAuthorization() {
        return new UserAuthorization(generateEmail(), USER_PASSWORD, USER_NAME);
    }

    public static String getAuthToken(UserResponse user){
        String accessToken  = user.getAccessToken();
        return accessToken.substring(ACCESS_TOKEN_PREFIX.length());
    }
}
