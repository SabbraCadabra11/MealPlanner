package mealplanner.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MealManager {
    private List<Meal> meals;
    private static Connection connection;

    public MealManager(Connection connection) throws SQLException {
        this.meals = new ArrayList<>();
        MealManager.connection = connection;
        loadMealsFromDatabase();
    }

    private void loadMealsFromDatabase() throws SQLException {
        String getMealsQuery = "SELECT * FROM meals";
        try (PreparedStatement statement = connection.prepareStatement(getMealsQuery);
            ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String category = rs.getString("category");
                String name = rs.getString("meal");
                int mealId = rs.getInt("meal_id");
                List<String> ingredients = loadIngredientsFromDatabase(mealId);
                Meal meal = new Meal(category, name, ingredients, mealId);
                if (!meals.contains(meal)) {
                    meals.add(meal);
                }
            }
        }
    }

    private List<String> loadIngredientsFromDatabase(int mealId) throws SQLException {
        List<String> ingredients = new ArrayList<>();
        String getIngredientsQuery = "SELECT * FROM ingredients WHERE meal_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(getIngredientsQuery)) {
            statement.setInt(1, mealId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ingredients.add(rs.getString("ingredient"));
            }
        }
        return ingredients;
    }

    public void addMeal(Meal meal) throws SQLException {
        meals.add(meal);
        addMealToDatabase(meal);
    }

    public Optional<Meal> getMeal(String name) {
        for (Meal meal : meals) {
            if (meal.getName().equalsIgnoreCase(name)){
                return Optional.of(meal);
            }
        }
        return Optional.empty();
    }

    public Optional<Meal> getMeal(int mealId) {
        for (Meal meal : meals) {
            if (meal.getId() == mealId){
                return Optional.of(meal);
            }
        }
        return Optional.empty();
    }


    public Optional<List<Meal>> getAllMeals() {
        if (meals.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(meals);
    }

    public Optional<List<Meal>> getMealsFromCategory(String category) {
        if (meals.isEmpty()) {
            return Optional.empty();
        }
        List<Meal> mealsFromCategory = new ArrayList<>();
        for (Meal meal : meals) {
            if (meal.getCategory().equalsIgnoreCase(category)) {
                mealsFromCategory.add(meal);
            }
        }
        return mealsFromCategory.isEmpty() ?
                Optional.empty() : Optional.of(mealsFromCategory);
    }

    private void addMealToDatabase(Meal meal) throws SQLException {
        int nextMealId = 0;
        String getLastMealIdQuery = "SELECT MAX(meal_id) AS max_meal_id FROM meals";
        try (PreparedStatement statement = connection.prepareStatement(getLastMealIdQuery);
             ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                nextMealId = rs.getInt("max_meal_id") + 1;
            }
        }
        String insertMeal = "INSERT INTO meals (category, meal, meal_id) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertMeal)) {
            statement.setString(1, meal.getCategory());
            statement.setString(2, meal.getName());
            statement.setInt(3, nextMealId);
            statement.executeUpdate();
        }
        addIngredientsToDatabase(meal, nextMealId);
    }

    private void addIngredientsToDatabase(Meal meal, int mealId) throws SQLException {
        int nextIngredientId = 0;
        String getLastIngredientIdQuery = "SELECT MAX(ingredient_id) AS max_ingredient_id FROM ingredients";
        try (PreparedStatement statement = connection.prepareStatement(getLastIngredientIdQuery);
            ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                nextIngredientId = rs.getInt("max_ingredient_id") + 1;
            }
        }
        String insertIngredient = "INSERT INTO ingredients (ingredient, ingredient_id, meal_id) VALUES (?, ?, ?)";
        for (String ingredient : meal.getIngredients()) {
            try (PreparedStatement statement = connection.prepareStatement(insertIngredient)) {
                statement.setString(1, ingredient);
                statement.setInt(2, nextIngredientId);
                statement.setInt(3, mealId);
                statement.executeUpdate();
            }
            nextIngredientId++;
        }
    }

    public static int getMealId(Meal meal) throws SQLException {
        String getMealIdQuery = "SELECT meal_id FROM meals WHERE meal = ? AND category = ?";
        try (PreparedStatement statement = connection.prepareStatement(getMealIdQuery)) {
            statement.setString(1, meal.getName());
            statement.setString(2, meal.getCategory());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("meal_id");
                }
            }
        }
        return -1;
    }
}













