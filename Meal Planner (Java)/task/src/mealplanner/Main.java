package mealplanner;

import mealplanner.model.MealManager;
import mealplanner.model.PlanManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection connection = DatabaseUtil.getConnection();
        Scanner scanner = new Scanner(System.in);
        MealManager mealManager = new MealManager(connection);
        PlanManager planManager = new PlanManager(connection, mealManager);
        TextUI ui = new TextUI(scanner, mealManager, planManager);

        ui.start();
    }
}