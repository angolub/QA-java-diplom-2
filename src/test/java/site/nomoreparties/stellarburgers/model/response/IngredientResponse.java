package site.nomoreparties.stellarburgers.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.nomoreparties.stellarburgers.model.Ingredient;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IngredientResponse {
    private boolean success;
    private List<Ingredient> data;
}
