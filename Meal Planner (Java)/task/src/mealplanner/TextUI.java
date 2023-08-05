package mealplanner;

import mealplanner.model.*;

import java.sql.SQLException;
import java.util.*;

public class TextUI {
    private final Scanner scanner;
    private final MealManager mealManager;
    private final PlanManager planManager;

    public TextUI (Scanner scanner, MealManager mealManager, PlanManager planManager) {
        this.scanner = scanner;
        this.mealManager = mealManager;
        this.planManager = planManager;
    }

    public void start() throws SQLException {
        String option = "";
        while (!option.equals("exit")) {
            System.out.println();
            option = askForOption();
            switch (option) {
                case "add" -> addMealOption();
                case "plan" -> planOption();
                case "show" -> showMealsOption();
                case "save" -> saveShoppingList();
                case "exit" -> System.out.println("Bye!");
            }
        }
    }


    private void addMealOption() throws SQLException {
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String mealCategory = askForMealCategory();
        System.out.println("Input the meal's name:");
        String mealName = askForStringWithLettersOnly();
        System.out.println("Input the ingredients:");
        String mealIngredients = askForStringWithLettersOnly();
        Meal meal = new Meal(mealCategory, mealName, mealIngredients);
        mealManager.addMeal(meal);
        System.out.println("The meal has been added!");
    }

    private void planOption() throws SQLException {
        List<String> days = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        List<String> categories = List.of("breakfast", "lunch", "dinner");
        Plan plan = new Plan();
        for (String day : days) {
            List<Meal> mealsForTheDay = new ArrayList<>();
            System.out.println(day);
            for (String category : categories) {
                printMeals(category);
                System.out.printf("Choose the %s for %s from the list above: ", category, day);
                Meal meal = getOneMeal();
                mealsForTheDay.add(meal);
            }
            plan.addDayToPlan(day, mealsForTheDay);
            System.out.printf("\nYeah! We planned the meals for %s.\n\n", day);
        }
        planManager.setPlan(plan);
        planManager.sendPlanToDb();
        System.out.println(plan);
    }

    private void printMeals(String category) {
        List<String> mealNames = new ArrayList<>();
        for (Meal meal : mealManager.getMealsFromCategory(category).orElse(List.of())) {
            mealNames.add(meal.getName());
        }
        Collections.sort(mealNames);
        mealNames.forEach(System.out::println);
    }

    private Meal getOneMeal() {
        Meal output = null;
        while (output == null) {
            String mealName = scanner.nextLine();
            Optional<Meal> meal = mealManager.getMeal(mealName);
            if (meal.isPresent()) {
                output = meal.get();
            } else {
                System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
            }
        }
        return output;
    }


    private void showMealsOption() {
        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
        String category = askForMealCategory();
        Optional<List<Meal>> meals = mealManager.getMealsFromCategory(category);
        if (meals.isEmpty()) {
            System.out.println("No meals found.");
            return;
        }
        System.out.printf("Category: %s\n", category);
        for (Meal meal : meals.get()) {
            System.out.printf("Name: %s", meal.getName());
            System.out.println("\nIngredients:");
            for (String ingredient : meal.getIngredients()) {
                System.out.println(ingredient);
            }
        }
    }

    private void saveShoppingList() {
        Optional<Plan> plan = planManager.getPlan();
        if (plan.get().isEmpty()) {
            System.out.println("Unable to save. Plan your meals first.");
            return;
        }
        System.out.println("Input a filename:");
        String fileName = scanner.nextLine();
        ShoppingList shoppingList = new ShoppingList(plan.get());
        if (shoppingList.saveToFile(fileName)) {
            System.out.println("Saved!");
        }
    }



    private String askForOption() {
        List<String> possibleOptions = List.of("add", "plan", "show", "save", "exit");
        String option;
        do {
            System.out.println("What would you like to do (add, show, plan, save, exit)?");
            option = scanner.nextLine().toLowerCase().trim();
            if (!possibleOptions.contains(option)) {
                option = "";
            }
        } while (option.isEmpty());
        return option;
    }

    private String askForMealCategory() {
        List<String> possibleCategories = List.of("breakfast", "lunch", "dinner");
        String category;
        do {
            category = scanner.nextLine().toLowerCase().trim();
            if (!possibleCategories.contains(category)) {
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                category = "";
            }
        } while (category.isEmpty());
        return category;
    }

    private String askForStringWithLettersOnly() {
        String output;
        boolean isValid;
        do {
            output = scanner.nextLine().toLowerCase().trim();
            isValid = true;
            if (!output.matches("^[a-zA-Z]+(?: ?[a-zA-Z ]+)*(?:, ?[a-zA-Z]+(?: ?[a-zA-Z ]+)*)*$")) {
                System.out.println("Wrong format. Use letters only!");
                isValid = false;
            }
        } while (!isValid);
        return output;
    }
}














