package site.nomoreparties.stellarburgers.model.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @SerializedName("_id")
    private String id;
    private String name;
    private IngredientType type;
    private int proteins;
    private int fat;
    private int carbohydrates;
    private int calories;
    private int price;
    private String image;
    @SerializedName("image_mobile")
    private String imageMobile;
    @SerializedName("image_large")
    private String imageLarge;
    @SerializedName("__v")
    private int v;
}
