package json;

import java.util.List;

public class IngredientsList {
    private String success;
    private List<IngredientsData> data;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<IngredientsData> getData() {
        return data;
    }

    public void setData(List<IngredientsData> data) {
        this.data = data;
    }
}
