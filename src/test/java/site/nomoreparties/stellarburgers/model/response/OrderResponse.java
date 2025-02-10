package site.nomoreparties.stellarburgers.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.nomoreparties.stellarburgers.model.Ingredient;
import site.nomoreparties.stellarburgers.model.response.orderdetail.OrderDetail;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private boolean success;
    private String name;
    private OrderDetail<Ingredient> order;
}
