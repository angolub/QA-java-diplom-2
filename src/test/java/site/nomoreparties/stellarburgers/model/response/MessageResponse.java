package site.nomoreparties.stellarburgers.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageResponse {
    private boolean success;
    private String message;
}
