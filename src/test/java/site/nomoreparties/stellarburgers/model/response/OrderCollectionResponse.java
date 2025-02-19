package site.nomoreparties.stellarburgers.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.nomoreparties.stellarburgers.model.response.orderdetail.OrderDetail;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderCollectionResponse {
    private boolean success;
    private List<OrderDetail<String>> orders;
}
