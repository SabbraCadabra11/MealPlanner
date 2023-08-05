package mealplanner.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Meal {
    private String category;
    private String name;
    private List<String> ingredients;
    private int id;

    public Meal(String category, String name, List<String> ingredients, int mealId) {
        this.category = category;
        this.name = name;
        this.ingredients = ingredients;
        this.id = mealId;
    }

    public Meal(String category, String name, String ingredients) {
        this.category = category;
        this.name = name;
        String regex = "\\s*,\\s*";
        this.ingredients = Arrays.stream(ingredients.split(regex)).toList();
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        output.append(String.format("Category: %s\n" +
                "Name: %s\n" +
                "Ingredients:", category, name));
        ingredients.forEach((ingredient) -> output.append("\n").append(ingredient));
        return output.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meal meal)) return false;
        return Objects.equals(category, meal.category) &&
                Objects.equals(name, meal.name) &&
                Objects.equals(ingredients, meal.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, name, ingredients);
    }

}













