package site.nomoreparties.stellarburgers.model.response.orderdetail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderOwner {
    private String email;
    private String name;
    private String createdAt;
    private String updatedAt;
}
