package mealplanner;

import org.postgresql.util.PSQLException;

import java.sql.*;

public class DatabaseUtil {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:postgresql://localhost:5432/meals_db";
            String username = "postgres";
            String password = "1111";
            connection = DriverManager.getConnection(url, username, password);
            createTables(connection);
        }
        return connection;
    }

    private static void createTables(Connection connection) throws SQLException {
        String createMealsTableQuery = "CREATE TABLE meals (category VARCHAR, meal VARCHAR, meal_id INTEGER)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createMealsTableQuery);
        } catch (PSQLException e) {
            if (!isTableAlreadyExistsError(e)) {
                throw e;
            }
        }

        String createIngredientsTableQuery = "CREATE TABLE ingredients (ingredient VARCHAR, ingredient_id INTEGER, meal_id INTEGER)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createIngredientsTableQuery);
        } catch (PSQLException e) {
            if (!isTableAlreadyExistsError(e)) {
                throw e;
            }
        }

        String createPlanTableQuery = "CREATE TABLE plan (meal VARCHAR, category VARCHAR, meal_id INTEGER)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createPlanTableQuery);
        } catch (PSQLException e) {
            if (!isTableAlreadyExistsError(e)) {
                throw e;
            }
        }
    }

    private static boolean isTableAlreadyExistsError(PSQLException e) {
        // PostgreSQL's error code for "relation already exists" is 42P07
        return "42P07".equals(e.getSQLState()) || e.getMessage().contains("already exists");
    }
}
