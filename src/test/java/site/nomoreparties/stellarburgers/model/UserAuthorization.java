package site.nomoreparties.stellarburgers.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthorization {
    private String email;
    private String password;
    private String name;
}
