package com.finalproject.adephagia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject.adephagia.dao.FoodItem;
import com.finalproject.adephagia.dao.Recipe;
import com.finalproject.adephagia.dao.RecipeItem;
import com.finalproject.adephagia.dto.IngestionItem;
import com.finalproject.adephagia.dto.InitialRequestItem;
import com.finalproject.adephagia.dto.Measurements;
import com.finalproject.adephagia.repository.FoodItemRepository;
import com.finalproject.adephagia.repository.RecipeItemRepository;
import com.finalproject.adephagia.repository.RecipeRepository;
import com.finalproject.adephagia.util.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Service
public class IngestionService {
    private RecipeItemRepository recipeItemRepository;
    private RecipeRepository recipeRepository;
    private FoodItemRepository foodItemRepository;
    public static final List<String> alphabet = List.of(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    );

    private final String SEARCH_CONSTANT = "Picture of raw ";
    public static final String consumptionUrl = "https://www.themealdb.com/api/json/v1/1/search.php?f=";

    public IngestionService(RecipeRepository recipeRepository, RecipeItemRepository recipeItemRepository,
                            FoodItemRepository foodItemRepository){
        this.recipeRepository = recipeRepository;
        this.recipeItemRepository = recipeItemRepository;
        this.foodItemRepository = foodItemRepository;
    }

    public void ingest (){
        // Need to loop for each letter
        Optional<InitialRequestItem> data = getAndSerializeData(alphabet.get(1));
        data.ifPresent(initialRequestItem -> {
            // get the meals returned
            List<IngestionItem> recipes = initialRequestItem.getMeals();
            recipes.forEach(recipe -> {
                Recipe recipeToBeSaved = new RecipeBuilder()
                        .name(recipe.getStrMeal().trim())
                        .description(recipe.getStrInstructions().trim())
                        .isPublic(true)
                        .picId(recipe.getStrMealThumb())
                        .build();

                // save recipe
                recipeRepository.save(recipeToBeSaved);
                // Create FoodItems
                createAndSaveFoodItems(recipe, recipeToBeSaved);
            });
        });
    }

    private void findPicAndDescription(String ingredientName, FoodItem foodItem) {
        System.setProperty("webdriver.chrome.driver", "src\\main\\java\\com\\finalproject\\adephagia\\drivers\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        // Open Google
        driver.get("https://www.google.com");
        // Search the item
        driver.findElement(By.className("gLFyf")).sendKeys(SEARCH_CONSTANT + ingredientName);
        driver.findElement(By.className("gLFyf")).sendKeys(Keys.RETURN);
        // Click Images
        driver.findElement(By.xpath("//*[text()='Images']")).click();
        driver.findElement(By.cssSelector("div.islrc img")).click();

        List<WebElement> elements = driver
                .findElements(By.xpath("//img[contains(@data-src,\"http\")]"));

        String src = elements.get(0).getAttribute("data-src");

        // Close the driver
        driver.quit();
        //Set the url
        foodItem.setPicUrl(src);
    }

    public void createAndSaveFoodItems(IngestionItem ingestionItem, Recipe recipe){
        for (int i = 1; i <= 20; i++){
            try {
                String ingredientName = (String) ingestionItem.getClass()
                        .getDeclaredField("strIngredient" + i).get(ingestionItem);
                if (ingredientName != null && !ingredientName.equals("")) {
                    ingredientName = ingredientName.toLowerCase();
                    Optional<FoodItem> item = foodItemRepository.findFoodItemsByName(ingredientName);
                    FoodItem savedItem;
                    // If the item does not exist create a new food item
                    if (item.isEmpty()){
                        // Create Food Item
                        FoodItem foodItem = new FoodItemBuilder()
                                .name(ingredientName)
                                .reusable(true)
                                .build();
                        // Find a pic and description
                        findPicAndDescription(ingredientName, foodItem);
                        //save the item
                        savedItem = foodItemRepository.save(foodItem);
                    } else {
                        // set the item
                        savedItem = item.get();
                    }
                    // Get the Measure
                    String ingredientMeasure = (String) ingestionItem.getClass()
                            .getDeclaredField("strMeasure" + i).get(ingestionItem);
                    RecipeItem recipeItem;
                    try {
                        Measurements measurements = MeasureParseUtils.parseMeasurement(ingredientMeasure);
                        //create the recipe item and associate it with the recipe
                        Measurements convertMeasurements = ConversionUtils.convertToStandardUnit(
                                measurements.getUnit(), measurements.getQuantity()
                        );
                        recipeItem = new RecipeItemBuilder().foodItem(savedItem)
                                .recipe(recipe).measurementUnit(convertMeasurements.getUnit())
                                .quantity(convertMeasurements.getQuantity()).build();
                    } catch (Exception e){
                        // I believe this happens when no measurement for an item is given
                        // create a default item
                        recipeItem = new RecipeItemBuilder().foodItem(savedItem)
                                .recipe(recipe).measurementUnit("default")
                                .quantity(0F).build();
                    }
                    // save the item
                    recipeItemRepository.save(recipeItem);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private Optional<InitialRequestItem> getAndSerializeData(String letter) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(consumptionUrl + letter))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            // Successful Deserialization
            return Optional.of(new ObjectMapper().readValue(response.body(), InitialRequestItem.class));
        } catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
