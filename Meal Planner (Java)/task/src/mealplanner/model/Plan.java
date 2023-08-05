package mealplanner.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Plan {
    private Map<String, List<Meal>> plan;

    public Plan() {
        plan = new LinkedHashMap<>();
    }

    public void addDayToPlan(String day, List<Meal> meals) {
        plan.put(day, meals);
    }

    public List<Meal> getMealsForDay(String day) {
        return plan.get(day);
    }

    public Optional<Map<String, List<Meal>>> getPlan() {
        return Optional.ofNullable(plan);
    }

    public boolean isEmpty() {
        if (getPlan().isPresent()) {
            return getPlan().get().isEmpty();
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Meal>> entry : plan.entrySet()) {
            sb.append(entry.getKey()).append("\n");
            int i = 0;
            for (Meal meal : entry.getValue()) {
                switch (i) {
                    case 0 -> sb.append("Breakfast: ");
                    case 1 -> sb.append("Lunch: ");
                    case 2 -> sb.append("Dinner: ");
                }
                sb.append(meal.getName()).append("\n");
                i++;
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
