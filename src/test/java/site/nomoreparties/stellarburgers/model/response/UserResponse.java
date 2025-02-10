package site.nomoreparties.stellarburgers.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.nomoreparties.stellarburgers.model.UserAuthorization;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private boolean success;
    private UserAuthorization user;
    private String accessToken;
    private String refreshToken;
}
