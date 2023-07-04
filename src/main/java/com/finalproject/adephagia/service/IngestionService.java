package com.finalproject.adephagia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject.adephagia.dao.FoodItem;
import com.finalproject.adephagia.dao.LookupImge;
import com.finalproject.adephagia.dao.Recipe;
import com.finalproject.adephagia.dao.RecipeItem;
import com.finalproject.adephagia.dto.IngestionItem;
import com.finalproject.adephagia.dto.InitialRequestItem;
import com.finalproject.adephagia.dto.Measurements;
import com.finalproject.adephagia.dto.UnitAggRows;
import com.finalproject.adephagia.repository.FoodItemRepository;
import com.finalproject.adephagia.repository.RecipeItemRepository;
import com.finalproject.adephagia.repository.RecipeRepository;
import com.finalproject.adephagia.util.*;
import jakarta.transaction.Transactional;
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
import java.util.*;

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

    /**
    Used to populate a repository of a set of recipe data (as of now it only check as subset of URL)
    InitialItemRequest has a set of IngestionItems are looped over and built into Recipe from Recipe Builder
    */
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

        // Reconcile The recipe Items
        reconcileRecipeItems();
    }

    /**
     * Gets all food items within the repository and loops through all
     * Each food items unit type is set to the most frequent in recipes, and so are all of its recpie items
     */
    @Transactional
    private void reconcileRecipeItems() {
        //List<Recipe> recipesToRemove = new ArrayList<>();
        // get food_items list
        List<FoodItem> foodItems = foodItemRepository.findAll();
        // for each food item
        foodItems.forEach(foodItem -> {
            // get the recipe items for it
            List<RecipeItem> recipeItems = recipeItemRepository.findByFoodItemId(foodItem.getId());
            // find all the different units
            List<UnitAggRows> unitFrequencyList = recipeItemRepository.getUniqueUnitsAndCounts(foodItem.getId());
            // find the most frequently used unit
            String mostFreqUnit = findMostFrequentUnit(unitFrequencyList);
            // set the type to the most freq unit type
            foodItem.setUnitType(ConversionUtils.getUnitType(mostFreqUnit));
            // Save food_item
            foodItemRepository.save(foodItem);
            // for each non-frequent recipe_item
            for (RecipeItem recipeItem : recipeItems) {
                // if the recipe items unit is different from the most common one
                if (!recipeItem.getMeasurementUnit().equals(mostFreqUnit)) {
                    // convert non-freq units to the most frequent units
                    ConversionUtils.convertRecipeItem(recipeItem, mostFreqUnit);
                    recipeItemRepository.save(recipeItem);
                }
            }
        });
    }

    /**
     *
     * @param unitList a list of the units of the respective recipe items of a food
     * @return String representing the most common unit that represents the food
     */
    private String findMostFrequentUnit(List<UnitAggRows> unitList) {
        String unit = "";
        int count = 0;
        for (UnitAggRows row : unitList) {
            if (row.getCount() > count) {
                count = row.getCount();
                unit = row.getUnitType();
            }
        }
        return unit;
    }

    /**
     * Used to web-scrape for a specific image of ingredient
     * @param ingredientName the string name of an ingredients name that's image that is desired to be found
     * @return String of the url of the source of the image
     */
    private String longImageLookup(String ingredientName) {
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
        if (src.length() <= 2048) {
            return src;
        }
        return null;
    }

    /**
     * Used to lookup as a image, it then looks for the said image on a themealdb/images/ingredients
     * @param lookingString string that is, formated to be on a URL, then searched at the by appending to end of URL
     * @return LookupImge which contains the image's Url
     */
    public LookupImge findPic(String lookingString) {
        LookupImge li = new LookupImge();
        String[] lookingStringArr = lookingString.trim().replace(",", "").split(" ");
        List<String> lookingList = new ArrayList<>();
        String noSpaceString = "";
        //if the lookingString contains a whitespace?
        if (!lookingString.equals(lookingString.replace(" ", ""))) {
            lookingList.add(lookingString.replace(" ", ""));
            noSpaceString = lookingString.replace(" ", "");
        }
        //for each img string, it is added to lookingList and another with a pluralized form of 2
        for (String img : lookingStringArr){
            lookingList.add(img);
            lookingList.add(Pluralize.pluralize(img, 2));
        }

        for (String ing : lookingList){
            //looking for the string at this url as a png, for all strings in the lookingList
            String url = String.format("https://www.themealdb.com/images/ingredients/%s.png", ing);
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                        HttpResponse.BodyHandlers.ofString());
                //If the response is OK then assign respective values to return
                if (response.statusCode() == 200) {
                    li.setUrl(url);
                    if (ing.equals(noSpaceString)){
                        li.setNoSpaceName(noSpaceString);
                    }
                    return li;
                }
            } catch (Exception e) {
                li.setUrl("");
                return li;
            }
        }

        // Last resort to a scrape
        li.setUrl(longImageLookup(lookingString));
        return li;
    }

    /**
     * Will Index through all of the ingredients within the ingestion item that is passed in
     * Will try and retrieve food item that exists within the foodItemRepository, or create new one if not there
     * Makes a recipeItem from the food item, for the specific recipe, and will standardize the measurement of the recipe item
     * @param ingestionItem which is a specific item part of the initial ingest call
     * @param recipe build or representing the data of the ingestionItem
    */
    public void createAndSaveFoodItems(IngestionItem ingestionItem, Recipe recipe){
        //loops to 20, as it is max amount of ingredients that are available in an ingestionItem
        for (int i = 1; i <= 20; i++){
            try {
                //getting ingredient based on index in the IngestionItem
                String ingredientName = (String) ingestionItem.getClass()
                        .getDeclaredField("strIngredient" + i).get(ingestionItem);
                if (ingredientName != null && !ingredientName.equals("")) {
                    //String with amount, that is standardized
                    ingredientName = Pluralize.pluralize(ingredientName.toLowerCase(), 1);
                    Optional<FoodItem> item = foodItemRepository.findFoodItemsByName(ingredientName);
                    // First try to find the original item
                    if (item.isEmpty()) {
                        // if you can't find it, try with all the spaces removed
                        Optional<FoodItem> potentialItem =
                                foodItemRepository.findFoodItemsByName(ingredientName.replace(" ", ""));
                        // if you still can't find it, just use the original one
                        if (potentialItem.isPresent()){
                            item = potentialItem;
                        }
                    }
                    FoodItem savedItem;
                    // If the item does not exist create a new food item
                    if (item.isEmpty()){
                        // Create Food Item
                        FoodItem foodItem = new FoodItemBuilder()
                                .name(ingredientName)
                                .reusable(true)
                                .build();
                        //image obtained from mealdb or via webscrap
                        LookupImge imageUrl = findPic(ingredientName);
                        if (imageUrl.getNoSpaceName() != null) {
                            foodItem.setName(imageUrl.getNoSpaceName());
                        }
                        // Find a pic and description
                        foodItem.setPicUrl(imageUrl.getUrl());
                        //save the item
                        savedItem = foodItemRepository.save(foodItem);
                    } else {
                        // set the item
                        savedItem = item.get();
                    }
                    // Get the Measure for same indexed ingredient
                    String ingredientMeasure = (String) ingestionItem.getClass()
                            .getDeclaredField("strMeasure" + i).get(ingestionItem);
                    RecipeItem recipeItem;
                    try {
                        Measurements measurements = MeasureParseUtils.parseMeasurement(ingredientMeasure);
                        //create the recipe item and associate it with the recipe, using food item that was created/pulled
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

    /**
     * Looks for a recipe based of a URL and an appended letter for a subset of URL (api call)
     * @param letter that is passed in an appended to end of consumptionUrl which builds an HTTP request out of
     * @returns Optional of InitialRequestItem that is populated via response of HTTP request
     */
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
