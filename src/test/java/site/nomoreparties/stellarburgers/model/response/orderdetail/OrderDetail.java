package site.nomoreparties.stellarburgers.model.response.orderdetail;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetail<T> {

    @SerializedName("_id")
    private String id;
    private OrderOwner owner;
    private String status;
    private String name;
    private String createdAt;
    private String updatedAt;
    private int number;
    private float price;
    private List<T> ingredients;
}
