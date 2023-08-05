package mealplanner.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingList {
    private Map<String, Integer> shoppingList;

    public ShoppingList(Plan plan) {
        shoppingList = new HashMap<>();
        Map<String, List<Meal>> mealPlan = plan.getPlan().orElse(new HashMap<>());
        for (Map.Entry<String, List<Meal>> entry : mealPlan.entrySet()) {
            for (Meal meal : entry.getValue()) {
                for (String ingredient : meal.getIngredients()) {
                    if (shoppingList.containsKey(ingredient)) {
                        shoppingList.put(ingredient, shoppingList.get(ingredient) + 1);
                    } else {
                        shoppingList.put(ingredient, 1);
                    }
                }
            }
        }
    }

    public boolean saveToFile(String fileName) {
        if (!shoppingList.isEmpty()) {
            File file = new File(fileName);
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Map.Entry<String, Integer> entry : shoppingList.entrySet()) {
                    if (entry.getValue() > 1) {
                        writer.println(String.format("%s x%d", entry.getKey(), entry.getValue()));
                    } else {
                        writer.println(entry.getKey());
                    }
                }
                return true;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

}
