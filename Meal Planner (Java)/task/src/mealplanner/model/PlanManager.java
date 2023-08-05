package mealplanner.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlanManager {
    private Plan plan;
    private final Connection connection;

    public PlanManager(Connection connection, MealManager mealManager) throws SQLException {
        this.connection = connection;
        loadPlanFromDb(mealManager);
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Optional<Plan> getPlan() {
        return Optional.ofNullable(plan).filter(p -> p.getPlan().isPresent());
    }

    private void loadPlanFromDb(MealManager mealManager) throws SQLException {
        this.plan = new Plan();
        List<Integer> planMealIds = new ArrayList<>();
        String getPlanQuery = "SELECT * FROM plan";
        try (PreparedStatement statement = connection.prepareStatement(getPlanQuery);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                planMealIds.add(rs.getInt("meal_id"));
            }
        }
        List<Meal> mealsForDay = new ArrayList<>();
        for (int i = 0; i < planMealIds.size(); i++) {
            String day = findDay(i);
            Optional<Meal> meal = mealManager.getMeal(planMealIds.get(i));
            meal.ifPresent(mealsForDay::add);
            if ((i + 1) % 3 == 0) {
                plan.addDayToPlan(day, mealsForDay);
                mealsForDay = new ArrayList<>();
            }
        }
    }

    private String findDay(int i) {
        if (i > 17) {
            return "Sunday";
        }
        if (i > 14) {
            return "Saturday";
        }
        if (i > 11) {
            return "Friday";
        }
        if (i > 8) {
            return "Thursday";
        }
        if (i > 5) {
            return "Wednesday";
        }
        if (i > 2) {
            return "Tuesday";
        }
        return "Monday";
    }

    public void sendPlanToDb() throws SQLException {
        String deleteOldPlanQuery = "DELETE FROM plan";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteOldPlanQuery)) {
            deleteStatement.executeUpdate();
        }

        String sendPlanQuery = "INSERT INTO plan (meal, category, meal_id) VALUES (?, ?, ?)";
        try (PreparedStatement sendStatement = connection.prepareStatement(sendPlanQuery)) {
            for (Map.Entry<String, List<Meal>> entry : plan.getPlan().get().entrySet()) {
                List<Meal> mealsForDay = entry.getValue();
                for (Meal meal : mealsForDay) {
                    sendStatement.setString(1, meal.getName());
                    sendStatement.setString(2, meal.getCategory());
                    sendStatement.setInt(3, MealManager.getMealId(meal));
                    sendStatement.executeUpdate();
                }
            }
        }
    }
}
