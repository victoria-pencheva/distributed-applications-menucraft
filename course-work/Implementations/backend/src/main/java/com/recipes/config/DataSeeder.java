package com.recipes.config;

import com.recipes.entity.*;
import com.recipes.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final IngredientRepository ingredientRepo;
    private final RecipeRepository recipeRepo;
    private final RecipeIngredientRepository riRepo;
    private final RecipeStepRepository stepRepo;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepo, CategoryRepository categoryRepo,
                      IngredientRepository ingredientRepo, RecipeRepository recipeRepo,
                      RecipeIngredientRepository riRepo, RecipeStepRepository stepRepo,
                      PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.ingredientRepo = ingredientRepo;
        this.recipeRepo = recipeRepo;
        this.riRepo = riRepo;
        this.stepRepo = stepRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (categoryRepo.count() == 0) {
            seedAll();
        } else {
            migrateIfNeeded();
        }
    }

    private void seedAll() {
        User admin = new User();
        admin.setUsername("admin"); admin.setEmail("admin@recipes.com");
        admin.setPassword(passwordEncoder.encode("Admin123!")); admin.setRole(Role.ADMIN);
        userRepo.save(admin);

        User chef = new User();
        chef.setUsername("chef"); chef.setEmail("chef@recipes.com");
        chef.setPassword(passwordEncoder.encode("Chef123!")); chef.setRole(Role.USER);
        userRepo.save(chef);

        Category italian   = cat("Italian",       "Pasta, pizza, risotto and more");
        Category asian     = cat("Asian",         "Chinese, Japanese, Thai cuisine");
        Category desserts  = cat("Desserts",      "Sweet treats and baked goods");
        Category breakfast = cat("Breakfast",     "Morning meals and brunch");
        Category med       = cat("Mediterranean", "Greek, Turkish, Lebanese cuisine");
        Category mexican   = cat("Mexican",       "Tacos, burritos, guacamole and more");
        Category soups     = cat("Soups & Stews", "Warming bowls and hearty stews");
        Category salads    = cat("Salads",        "Fresh and vibrant salads");
        categoryRepo.saveAll(List.of(italian, asian, desserts, breakfast, med, mexican, soups, salads));

        Ingredient flour       = ing("All-purpose Flour", "Wheat flour for baking",           "g",   364.0, 10.0,  76.0,  1.0,  3.0,  IngredientCategory.GRAINS,   true);
        Ingredient eggs        = ing("Eggs",              "Large hen eggs",                   "pcs",  78.0,  6.0,   0.6,  5.0,  0.0,  IngredientCategory.EGGS,     false);
        Ingredient milk        = ing("Milk",              "Whole cow milk",                   "g",    61.0,  3.4,   4.8,  3.3,  0.0,  IngredientCategory.DAIRY,    false);
        Ingredient butter      = ing("Butter",            "Unsalted butter",                  "g",   717.0,  0.6,   0.1, 80.0,  0.0,  IngredientCategory.DAIRY,    false);
        Ingredient sugar       = ing("Sugar",             "White granulated sugar",           "g",   387.0,  0.0, 100.0,  0.0,  0.0,  IngredientCategory.OTHER,    false);
        Ingredient salt        = ing("Salt",              "Table salt",                       "g",     0.0,  0.0,   0.0,  0.0,  0.0,  IngredientCategory.SPICES,   false);
        Ingredient pasta       = ing("Spaghetti",         "Dried spaghetti pasta",            "g",   371.0, 13.0,  70.0,  1.5,  3.0,  IngredientCategory.GRAINS,   true);
        Ingredient tomatoSauce = ing("Tomato Sauce",      "Canned crushed tomatoes",          "g",    29.0,  1.5,   5.5,  0.2,  1.2,  IngredientCategory.PRODUCE,  false);
        Ingredient garlic      = ing("Garlic",            "Fresh garlic cloves",              "pcs",   5.0,  0.2,   1.0,  0.0,  0.1,  IngredientCategory.PRODUCE,  false);
        Ingredient oliveOil    = ing("Olive Oil",         "Extra virgin olive oil",           "g",   884.0,  0.0,   0.0,100.0,  0.0,  IngredientCategory.OILS,     false);
        Ingredient chicken     = ing("Chicken Breast",    "Boneless skinless chicken breast", "g",   165.0, 31.0,   0.0,  3.6,  0.0,  IngredientCategory.POULTRY,  false);
        Ingredient rice        = ing("Jasmine Rice",      "Long grain jasmine rice",          "g",   130.0,  2.7,  28.0,  0.3,  0.4,  IngredientCategory.GRAINS,   false);
        Ingredient soySauce    = ing("Soy Sauce",         "Dark soy sauce",                   "g",    53.0,  5.5,   7.0,  0.0,  0.5,  IngredientCategory.OTHER,    true);
        Ingredient cocoa       = ing("Cocoa Powder",      "Unsweetened cocoa powder",         "g",   228.0, 20.0,  58.0, 14.0, 37.0,  IngredientCategory.OTHER,    false);
        Ingredient onion       = ing("Onion",             "Yellow onion",                     "g",    40.0,  1.1,   9.3,  0.1,  1.7,  IngredientCategory.PRODUCE,  false);
        Ingredient bellPepper  = ing("Bell Pepper",       "Red or green bell pepper",         "g",    31.0,  1.0,   6.0,  0.3,  2.0,  IngredientCategory.PRODUCE,  false);
        Ingredient groundBeef  = ing("Ground Beef",       "80/20 minced beef",                "g",   250.0, 17.0,   0.0, 20.0,  0.0,  IngredientCategory.MEAT,     false);
        Ingredient lemon       = ing("Lemon",             "Fresh lemon",                      "pcs",  17.0,  0.6,   5.0,  0.2,  1.5,  IngredientCategory.PRODUCE,  false);
        Ingredient parmesan    = ing("Parmesan",          "Hard aged Italian cheese",         "g",   431.0, 38.0,   4.0, 29.0,  0.0,  IngredientCategory.DAIRY,    false);
        Ingredient heavyCream  = ing("Heavy Cream",       "Whipping cream 35% fat",           "g",   340.0,  2.5,   3.0, 36.0,  0.0,  IngredientCategory.DAIRY,    false);
        Ingredient tomatoes    = ing("Tomatoes",          "Fresh ripe tomatoes",              "g",    18.0,  0.9,   3.9,  0.2,  1.2,  IngredientCategory.PRODUCE,  false);
        Ingredient spinach     = ing("Spinach",           "Fresh baby spinach",               "g",    23.0,  2.9,   3.6,  0.4,  2.2,  IngredientCategory.PRODUCE,  false);
        Ingredient mozzarella  = ing("Mozzarella",        "Fresh mozzarella cheese",          "g",   280.0, 22.0,   2.2, 17.0,  0.0,  IngredientCategory.DAIRY,    false);
        Ingredient cheddar     = ing("Cheddar Cheese",    "Mature cheddar",                   "g",   403.0, 25.0,   1.3, 33.0,  0.0,  IngredientCategory.DAIRY,    false);
        Ingredient avocado     = ing("Avocado",           "Ripe Hass avocado",                "pcs", 234.0,  3.0,  12.0, 21.0,  9.0,  IngredientCategory.PRODUCE,  false);
        Ingredient cumin       = ing("Cumin",             "Ground cumin spice",               "g",   375.0, 18.0,  44.0, 22.0, 11.0,  IngredientCategory.SPICES,   false);
        Ingredient paprika     = ing("Paprika",           "Sweet smoked paprika",             "g",   282.0, 14.0,  54.0, 13.0, 34.0,  IngredientCategory.SPICES,   false);
        Ingredient yeast       = ing("Dry Yeast",         "Active dry yeast",                 "g",   325.0, 40.0,  47.0,  7.0, 26.0,  IngredientCategory.OTHER,    false);
        Ingredient honey       = ing("Honey",             "Pure natural honey",               "g",   304.0,  0.3,  82.0,  0.0,  0.2,  IngredientCategory.OTHER,    false);
        Ingredient greekYogurt = ing("Greek Yogurt",      "Full-fat strained yogurt",         "g",    97.0, 10.0,   4.0,  5.0,  0.0,  IngredientCategory.DAIRY,    false);
        Ingredient salmon      = ing("Salmon Fillet",     "Atlantic salmon fillet",           "g",   208.0, 25.0,   0.0, 13.0,  0.0,  IngredientCategory.SEAFOOD,  false);
        Ingredient feta        = ing("Feta Cheese",       "Crumbled Greek feta",              "g",   264.0, 14.0,   4.0, 21.0,  0.0,  IngredientCategory.DAIRY,    false);
        Ingredient cucumber    = ing("Cucumber",          "English cucumber",                 "g",    16.0,  0.7,   3.6,  0.1,  0.5,  IngredientCategory.PRODUCE,  false);
        Ingredient olives      = ing("Kalamata Olives",   "Pitted Kalamata olives",           "g",   115.0,  0.8,   6.0, 11.0,  1.5,  IngredientCategory.PRODUCE,  false);
        Ingredient lentils     = ing("Red Lentils",       "Dried red lentils",                "g",   116.0,  9.0,  20.0,  0.4,  8.0,  IngredientCategory.LEGUMES,  false);
        Ingredient chickpeas   = ing("Chickpeas",         "Cooked or canned chickpeas",       "g",   164.0,  9.0,  27.0,  2.6,  8.0,  IngredientCategory.LEGUMES,  false);
        Ingredient beefStock   = ing("Beef Stock",        "Rich beef broth",                  "g",    17.0,  1.0,   2.5,  0.2,  0.0,  IngredientCategory.OTHER,    false);
        Ingredient bread       = ing("Bread",             "Sourdough or crusty white bread",  "g",   265.0,  9.0,  50.0,  3.0,  2.0,  IngredientCategory.GRAINS,   true);
        Ingredient potatoes    = ing("Potatoes",          "Floury potatoes",                  "g",    77.0,  2.0,  17.0,  0.1,  2.0,  IngredientCategory.PRODUCE,  false);
        Ingredient carrots     = ing("Carrots",           "Fresh carrots",                    "g",    41.0,  0.9,  10.0,  0.2,  2.8,  IngredientCategory.PRODUCE,  false);
        Ingredient pancetta    = ing("Pancetta",          "Cured Italian pork belly",         "g",   458.0, 12.0,   0.0, 44.0,  0.0,  IngredientCategory.MEAT,     false);
        Ingredient banana      = ing("Banana",            "Ripe banana",                      "pcs",  89.0,  1.1,  23.0,  0.3,  2.6,  IngredientCategory.PRODUCE,  false);
        Ingredient ginger      = ing("Fresh Ginger",      "Fresh ginger root",                "g",    80.0,  1.8,  18.0,  0.75, 2.0,  IngredientCategory.SPICES,   false);
        Ingredient coconutMilk = ing("Coconut Milk",      "Full-fat coconut milk",            "g",   230.0,  2.3,   6.0, 24.0,  0.0,  IngredientCategory.OTHER,    false);
        Ingredient curryPowder = ing("Curry Powder",      "Mild curry spice blend",           "g",   325.0, 12.0,  58.0, 15.0, 33.0,  IngredientCategory.SPICES,   false);
        Ingredient romaine     = ing("Romaine Lettuce",   "Crisp romaine lettuce",            "g",    17.0,  1.2,   3.3,  0.3,  2.1,  IngredientCategory.PRODUCE,  false);
        Ingredient dijonMust   = ing("Dijon Mustard",     "French Dijon mustard",             "g",    66.0,  4.0,   5.0,  4.0,  2.0,  IngredientCategory.SPICES,   false);

        ingredientRepo.saveAll(List.of(
            flour, eggs, milk, butter, sugar, salt, pasta, tomatoSauce, garlic, oliveOil,
            chicken, rice, soySauce, cocoa, onion, bellPepper, groundBeef, lemon, parmesan,
            heavyCream, tomatoes, spinach, mozzarella, cheddar, avocado, cumin, paprika, yeast,
            honey, greekYogurt, salmon, feta, cucumber, olives, lentils, chickpeas, beefStock,
            bread, potatoes, carrots, pancetta, banana, ginger, coconutMilk, curryPowder,
            romaine, dijonMust));

        Recipe spaghetti = recipe("Spaghetti Marinara",
            "Classic Italian pasta with rich tomato sauce",
            10, 20, 4, Difficulty.EASY, italian, chef,
            "Classic Sunday pasta, done in 30 minutes",
            12.0, 68.0, 8.0, 4.0);
        recipeRepo.save(spaghetti);
        ri(spaghetti, pasta, 400.0, "g"); ri(spaghetti, tomatoSauce, 400.0, "g");
        ri(spaghetti, garlic, 4.0, "pcs"); ri(spaghetti, oliveOil, 30.0, "g");
        ri(spaghetti, salt, 5.0, "g");
        step(spaghetti, 1, "Boil well-salted water. Cook spaghetti until al dente per package directions.");
        step(spaghetti, 2, "Heat olive oil in a wide pan over medium heat. Add minced garlic and sauté 1 minute until fragrant.");
        step(spaghetti, 3, "Add tomato sauce, season with salt, and simmer uncovered for 10 minutes.");
        step(spaghetti, 4, "Drain pasta, reserving 1 cup pasta water. Toss with sauce, adding pasta water to loosen.");
        step(spaghetti, 5, "Serve immediately with a drizzle of olive oil.");

        Recipe chickenRice = recipe("Garlic Chicken Fried Rice",
            "Quick Asian-style fried rice with tender chicken",
            15, 20, 2, Difficulty.MEDIUM, asian, chef,
            "One-wok weeknight wonder",
            32.0, 45.0, 10.0, 2.0);
        recipeRepo.save(chickenRice);
        ri(chickenRice, chicken, 300.0, "g"); ri(chickenRice, rice, 200.0, "g");
        ri(chickenRice, garlic, 3.0, "pcs"); ri(chickenRice, soySauce, 45.0, "g");
        ri(chickenRice, oliveOil, 20.0, "g"); ri(chickenRice, eggs, 2.0, "pcs");
        step(chickenRice, 1, "Cook jasmine rice and spread on a tray to cool — day-old rice is ideal.");
        step(chickenRice, 2, "Heat oil in a wok over high heat. Add garlic and stir-fry 30 seconds.");
        step(chickenRice, 3, "Add diced chicken and cook until golden, about 4 minutes.");
        step(chickenRice, 4, "Push to the side, crack in eggs and scramble until just set.");
        step(chickenRice, 5, "Add rice and soy sauce, toss everything over high heat for 2 minutes.");

        Recipe pancakes = recipe("Fluffy Pancakes",
            "Light and airy breakfast pancakes",
            5, 15, 4, Difficulty.EASY, breakfast, admin,
            "Weekend brunch perfection",
            8.0, 42.0, 9.0, 1.0);
        recipeRepo.save(pancakes);
        ri(pancakes, flour, 200.0, "g"); ri(pancakes, eggs, 2.0, "pcs");
        ri(pancakes, milk, 250.0, "g"); ri(pancakes, butter, 30.0, "g");
        ri(pancakes, sugar, 20.0, "g"); ri(pancakes, salt, 2.0, "g");
        step(pancakes, 1, "Whisk flour, sugar, and salt together in a large bowl.");
        step(pancakes, 2, "Beat eggs with milk and melted butter in a separate bowl.");
        step(pancakes, 3, "Fold wet into dry until just combined — lumps are fine, don't over-mix.");
        step(pancakes, 4, "Heat a non-stick pan over medium. Pour ¼ cup batter per pancake.");
        step(pancakes, 5, "Cook until bubbles form and edges look dry, flip once, cook 1 more minute.");

        Recipe chocolateCake = recipe("Chocolate Fudge Cake",
            "Rich dark chocolate cake for special occasions",
            30, 45, 8, Difficulty.HARD, desserts, admin,
            "Celebration-worthy dark chocolate",
            6.0, 58.0, 22.0, 3.0);
        recipeRepo.save(chocolateCake);
        ri(chocolateCake, flour, 250.0, "g"); ri(chocolateCake, cocoa, 75.0, "g");
        ri(chocolateCake, sugar, 300.0, "g"); ri(chocolateCake, eggs, 3.0, "pcs");
        ri(chocolateCake, butter, 200.0, "g"); ri(chocolateCake, milk, 200.0, "g");
        step(chocolateCake, 1, "Preheat oven to 175°C. Grease and flour two 9-inch pans.");
        step(chocolateCake, 2, "Melt butter. Whisk with sugar until smooth. Beat in eggs one at a time.");
        step(chocolateCake, 3, "Sift flour and cocoa together. Fold into the wet mixture alternating with milk.");
        step(chocolateCake, 4, "Divide batter evenly. Bake 35–40 minutes — a toothpick should come out clean.");
        step(chocolateCake, 5, "Cool 10 minutes in pan, then fully on a wire rack before frosting.");

        Recipe tacos = recipe("Beef Tacos",
            "Juicy spiced ground beef tacos with fresh toppings",
            15, 20, 4, Difficulty.MEDIUM, mexican, chef,
            "Tuesday night or any night, these disappear fast",
            28.0, 32.0, 18.0, 4.0);
        recipeRepo.save(tacos);
        ri(tacos, groundBeef, 500.0, "g"); ri(tacos, onion, 120.0, "g");
        ri(tacos, bellPepper, 100.0, "g"); ri(tacos, cumin, 5.0, "g");
        ri(tacos, paprika, 5.0, "g"); ri(tacos, salt, 4.0, "g");
        ri(tacos, cheddar, 80.0, "g"); ri(tacos, avocado, 1.0, "pcs");
        step(tacos, 1, "Cook diced onion and bell pepper in a hot pan with a splash of oil until softened.");
        step(tacos, 2, "Add ground beef, break up with a spoon, cook until no pink remains.");
        step(tacos, 3, "Add cumin, paprika, and salt. Stir well and cook 2 more minutes.");
        step(tacos, 4, "Warm tortillas in a dry pan. Mash avocado with a pinch of salt.");
        step(tacos, 5, "Fill tortillas with beef, top with guacamole and cheddar. Serve immediately.");

        Recipe greekSalad = recipe("Greek Salad",
            "Crisp vegetables with salty feta and olives",
            15, 0, 4, Difficulty.EASY, salads, chef,
            "No-cook, all flavour — ready in 15 minutes",
            6.0, 14.0, 14.0, 3.0);
        recipeRepo.save(greekSalad);
        ri(greekSalad, tomatoes, 300.0, "g"); ri(greekSalad, cucumber, 200.0, "g");
        ri(greekSalad, bellPepper, 100.0, "g"); ri(greekSalad, olives, 80.0, "g");
        ri(greekSalad, feta, 150.0, "g"); ri(greekSalad, oliveOil, 30.0, "g");
        ri(greekSalad, lemon, 1.0, "pcs"); ri(greekSalad, salt, 3.0, "g");
        step(greekSalad, 1, "Cut tomatoes into wedges. Slice cucumber into half-moons. Dice bell pepper.");
        step(greekSalad, 2, "Combine all vegetables in a large bowl with olives.");
        step(greekSalad, 3, "Drizzle with olive oil and squeeze over lemon juice. Season with salt.");
        step(greekSalad, 4, "Top with crumbled feta. Toss gently and serve immediately.");

        Recipe tomatoSoup = recipe("Creamy Tomato Soup",
            "Silky blended tomato soup with a touch of cream",
            10, 25, 4, Difficulty.EASY, soups, admin,
            "Blissfully simple — a bowlful of comfort",
            4.0, 18.0, 9.0, 3.0);
        recipeRepo.save(tomatoSoup);
        ri(tomatoSoup, tomatoes, 800.0, "g"); ri(tomatoSoup, onion, 150.0, "g");
        ri(tomatoSoup, garlic, 3.0, "pcs"); ri(tomatoSoup, butter, 30.0, "g");
        ri(tomatoSoup, heavyCream, 100.0, "g"); ri(tomatoSoup, salt, 5.0, "g");
        step(tomatoSoup, 1, "Melt butter in a large pot over medium heat. Sauté onion until translucent, about 8 minutes.");
        step(tomatoSoup, 2, "Add garlic and cook 1 minute. Add chopped tomatoes and season with salt.");
        step(tomatoSoup, 3, "Simmer 20 minutes until tomatoes collapse and soften completely.");
        step(tomatoSoup, 4, "Blend until smooth using an immersion blender or stand blender.");
        step(tomatoSoup, 5, "Stir in heavy cream, adjust seasoning, and serve with crusty bread.");

        Recipe pizza = recipe("Margherita Pizza",
            "Classic Neapolitan pizza with fresh mozzarella",
            30, 15, 4, Difficulty.MEDIUM, italian, chef,
            "The one that started it all — tomato, basil, mozzarella",
            16.0, 48.0, 14.0, 2.0);
        recipeRepo.save(pizza);
        ri(pizza, flour, 300.0, "g"); ri(pizza, yeast, 5.0, "g");
        ri(pizza, salt, 6.0, "g"); ri(pizza, oliveOil, 20.0, "g");
        ri(pizza, tomatoSauce, 150.0, "g"); ri(pizza, mozzarella, 200.0, "g");
        step(pizza, 1, "Mix flour, yeast, salt and olive oil. Add 180ml warm water and knead 10 minutes until smooth.");
        step(pizza, 2, "Cover and rest 1 hour until doubled. Meanwhile preheat oven to 250°C with a baking stone.");
        step(pizza, 3, "Stretch dough into a thin round on a floured surface.");
        step(pizza, 4, "Spread tomato sauce, tear over mozzarella.");
        step(pizza, 5, "Bake 10–12 minutes until crust is charred and cheese is bubbling.");

        Recipe salmonRecipe = recipe("Lemon Garlic Salmon",
            "Pan-seared salmon with bright lemon and garlic butter",
            10, 15, 2, Difficulty.MEDIUM, med, chef,
            "Restaurant-quality in under 25 minutes",
            42.0, 4.0, 18.0, 0.0);
        recipeRepo.save(salmonRecipe);
        ri(salmonRecipe, salmon, 400.0, "g"); ri(salmonRecipe, lemon, 1.0, "pcs");
        ri(salmonRecipe, garlic, 3.0, "pcs"); ri(salmonRecipe, butter, 30.0, "g");
        ri(salmonRecipe, oliveOil, 15.0, "g"); ri(salmonRecipe, salt, 3.0, "g");
        step(salmonRecipe, 1, "Pat salmon dry and season generously with salt.");
        step(salmonRecipe, 2, "Heat olive oil in an oven-safe skillet over high heat until shimmering.");
        step(salmonRecipe, 3, "Add salmon skin-side up. Sear 3 minutes undisturbed until a golden crust forms.");
        step(salmonRecipe, 4, "Flip. Add butter and garlic to the pan. Baste salmon with the foaming butter.");
        step(salmonRecipe, 5, "Squeeze over lemon juice. Cook 3 more minutes. Rest 2 minutes before serving.");

        Recipe lentilSoup = recipe("Red Lentil Soup",
            "Hearty Middle-Eastern lentil soup with warm spices",
            10, 30, 6, Difficulty.EASY, soups, admin,
            "Deeply nourishing — spiced, golden and ready in 40 minutes",
            18.0, 42.0, 3.0, 8.0);
        recipeRepo.save(lentilSoup);
        ri(lentilSoup, lentils, 300.0, "g"); ri(lentilSoup, onion, 200.0, "g");
        ri(lentilSoup, garlic, 4.0, "pcs"); ri(lentilSoup, cumin, 6.0, "g");
        ri(lentilSoup, paprika, 4.0, "g"); ri(lentilSoup, oliveOil, 30.0, "g");
        ri(lentilSoup, lemon, 1.0, "pcs"); ri(lentilSoup, salt, 6.0, "g");
        step(lentilSoup, 1, "Heat olive oil in a large pot. Sauté onion until deeply golden, about 12 minutes.");
        step(lentilSoup, 2, "Add garlic, cumin and paprika. Cook 1 minute until fragrant.");
        step(lentilSoup, 3, "Add rinsed lentils and 1.2L water. Bring to boil, skim any foam.");
        step(lentilSoup, 4, "Simmer 25 minutes until lentils are completely soft and falling apart.");
        step(lentilSoup, 5, "Blend half the soup for a creamy-chunky texture. Finish with lemon juice and salt.");

        Recipe shakshuka = recipe("Shakshuka",
            "Eggs poached in a spiced tomato and pepper sauce",
            10, 20, 3, Difficulty.EASY, breakfast, chef,
            "One pan, big flavour — brunch done right",
            12.0, 16.0, 10.0, 4.0);
        recipeRepo.save(shakshuka);
        ri(shakshuka, eggs, 5.0, "pcs"); ri(shakshuka, tomatoes, 500.0, "g");
        ri(shakshuka, bellPepper, 150.0, "g"); ri(shakshuka, onion, 120.0, "g");
        ri(shakshuka, garlic, 3.0, "pcs"); ri(shakshuka, cumin, 4.0, "g");
        ri(shakshuka, paprika, 4.0, "g"); ri(shakshuka, oliveOil, 20.0, "g");
        step(shakshuka, 1, "Heat olive oil in a wide pan. Sauté onion and pepper until soft, about 8 minutes.");
        step(shakshuka, 2, "Add garlic, cumin and paprika. Cook 1 minute.");
        step(shakshuka, 3, "Add chopped tomatoes and simmer 10 minutes until thickened. Season with salt.");
        step(shakshuka, 4, "Make wells in the sauce. Crack one egg into each well.");
        step(shakshuka, 5, "Cover and cook on low heat 5–7 minutes until whites are set but yolks still runny.");

        Recipe bolognese = recipe("Beef Bolognese",
            "Slow-cooked Italian meat sauce on fresh pasta",
            15, 60, 6, Difficulty.MEDIUM, italian, admin,
            "Worth every minute of the simmer",
            35.0, 52.0, 16.0, 3.0);
        recipeRepo.save(bolognese);
        ri(bolognese, groundBeef, 600.0, "g"); ri(bolognese, pasta, 500.0, "g");
        ri(bolognese, onion, 150.0, "g"); ri(bolognese, garlic, 4.0, "pcs");
        ri(bolognese, tomatoSauce, 400.0, "g"); ri(bolognese, oliveOil, 25.0, "g");
        ri(bolognese, salt, 6.0, "g"); ri(bolognese, parmesan, 60.0, "g");
        step(bolognese, 1, "Heat oil in a heavy pot. Brown onion and garlic over medium heat until golden.");
        step(bolognese, 2, "Add ground beef. Cook breaking it up until browned and liquid has evaporated.");
        step(bolognese, 3, "Add tomato sauce, season well. Simmer on the lowest heat for 45 minutes, stirring occasionally.");
        step(bolognese, 4, "Cook spaghetti in well-salted boiling water until al dente.");
        step(bolognese, 5, "Toss pasta with sauce, serve topped with freshly grated parmesan.");

        Recipe yogurtBowl = recipe("Honey Greek Yogurt Bowl",
            "Thick creamy yogurt with honey and a pinch of warmth",
            5, 0, 1, Difficulty.EASY, breakfast, chef,
            "Breakfast in 5 minutes — protein-packed and satisfying",
            10.0, 24.0, 5.0, 0.0);
        recipeRepo.save(yogurtBowl);
        ri(yogurtBowl, greekYogurt, 200.0, "g"); ri(yogurtBowl, honey, 20.0, "g");
        ri(yogurtBowl, sugar, 5.0, "g");
        step(yogurtBowl, 1, "Spoon Greek yogurt into a bowl.");
        step(yogurtBowl, 2, "Drizzle honey over the top.");
        step(yogurtBowl, 3, "Dust with a pinch of sugar or cinnamon. Serve immediately.");

        Recipe chickpeaCurry = recipe("Chickpea Curry",
            "Fragrant golden chickpeas in a rich spiced sauce",
            10, 25, 4, Difficulty.EASY, asian, chef,
            "Weeknight vegan — warming, hearty, packed with protein",
            14.0, 46.0, 8.0, 10.0);
        recipeRepo.save(chickpeaCurry);
        ri(chickpeaCurry, chickpeas, 400.0, "g"); ri(chickpeaCurry, tomatoSauce, 400.0, "g");
        ri(chickpeaCurry, onion, 150.0, "g"); ri(chickpeaCurry, garlic, 4.0, "pcs");
        ri(chickpeaCurry, cumin, 5.0, "g"); ri(chickpeaCurry, paprika, 5.0, "g");
        ri(chickpeaCurry, oliveOil, 20.0, "g"); ri(chickpeaCurry, salt, 5.0, "g");
        step(chickpeaCurry, 1, "Heat oil in a pan. Sauté onion until soft and golden, about 10 minutes.");
        step(chickpeaCurry, 2, "Add garlic, cumin, and paprika. Stir 1 minute until spices bloom.");
        step(chickpeaCurry, 3, "Add chickpeas and tomato sauce. Season well.");
        step(chickpeaCurry, 4, "Simmer 20 minutes until sauce thickens and chickpeas are coated.");
        step(chickpeaCurry, 5, "Serve with rice or flatbread. Squeeze over lemon juice to finish.");

        Recipe carbonara = recipe("Pasta Carbonara",
            "Roman pasta with eggs, pancetta and parmesan",
            10, 15, 2, Difficulty.MEDIUM, italian, chef,
            "Rome's finest — silky, rich, no cream needed",
            28.0, 58.0, 22.0, 2.0);
        recipeRepo.save(carbonara);
        ri(carbonara, pasta, 200.0, "g"); ri(carbonara, eggs, 3.0, "pcs");
        ri(carbonara, pancetta, 100.0, "g"); ri(carbonara, parmesan, 60.0, "g");
        ri(carbonara, garlic, 2.0, "pcs"); ri(carbonara, salt, 3.0, "g");
        step(carbonara, 1, "Cook spaghetti in well-salted boiling water until al dente. Reserve 1 cup pasta water.");
        step(carbonara, 2, "Fry pancetta in a cold pan, render fat until crispy. Add garlic, cook 1 minute.");
        step(carbonara, 3, "Beat eggs with grated parmesan and a pinch of black pepper.");
        step(carbonara, 4, "Remove pan from heat. Add drained pasta and toss with pancetta fat.");
        step(carbonara, 5, "Pour egg mixture over, tossing rapidly. Add pasta water splash by splash until silky.");

        Recipe tikka = recipe("Chicken Tikka Masala",
            "Tender chicken in a rich, spiced tomato-coconut sauce",
            20, 30, 4, Difficulty.MEDIUM, asian, chef,
            "The nation's favourite curry — bold, creamy and aromatic",
            38.0, 24.0, 16.0, 4.0);
        recipeRepo.save(tikka);
        ri(tikka, chicken, 600.0, "g"); ri(tikka, tomatoSauce, 400.0, "g");
        ri(tikka, coconutMilk, 200.0, "g"); ri(tikka, onion, 150.0, "g");
        ri(tikka, garlic, 4.0, "pcs"); ri(tikka, ginger, 20.0, "g");
        ri(tikka, curryPowder, 15.0, "g"); ri(tikka, salt, 6.0, "g");
        step(tikka, 1, "Marinate diced chicken in yogurt, curry powder, and salt for 20 minutes.");
        step(tikka, 2, "Grill or pan-fry chicken until charred at edges. Set aside.");
        step(tikka, 3, "Sauté onion, garlic, and ginger in oil until golden, about 10 minutes.");
        step(tikka, 4, "Add remaining curry powder. Cook 1 minute. Add tomato sauce and simmer 10 minutes.");
        step(tikka, 5, "Add coconut milk and chicken. Simmer 10 minutes. Adjust seasoning and serve with rice.");

        Recipe avocadoToast = recipe("Avocado Toast",
            "Smashed avocado on toasted sourdough with lemon and salt",
            5, 5, 2, Difficulty.EASY, breakfast, admin,
            "Millennial classic — still undefeated at brunch",
            6.0, 28.0, 16.0, 8.0);
        recipeRepo.save(avocadoToast);
        ri(avocadoToast, bread, 150.0, "g"); ri(avocadoToast, avocado, 2.0, "pcs");
        ri(avocadoToast, lemon, 1.0, "pcs"); ri(avocadoToast, salt, 2.0, "g");
        ri(avocadoToast, oliveOil, 10.0, "g");
        step(avocadoToast, 1, "Toast bread slices until golden and crisp.");
        step(avocadoToast, 2, "Halve avocados, scoop flesh into a bowl.");
        step(avocadoToast, 3, "Mash with lemon juice, salt, and a drizzle of olive oil. Keep it chunky.");
        step(avocadoToast, 4, "Spread generously on toast. Add toppings of your choice and serve immediately.");

        Recipe beefStew = recipe("Beef Stew",
            "Slow-cooked beef with tender vegetables in rich gravy",
            20, 90, 6, Difficulty.MEDIUM, soups, admin,
            "Sunday comfort — worth every hour of the simmer",
            42.0, 28.0, 18.0, 5.0);
        recipeRepo.save(beefStew);
        ri(beefStew, groundBeef, 800.0, "g"); ri(beefStew, potatoes, 400.0, "g");
        ri(beefStew, carrots, 200.0, "g"); ri(beefStew, onion, 200.0, "g");
        ri(beefStew, garlic, 4.0, "pcs"); ri(beefStew, beefStock, 600.0, "g");
        ri(beefStew, oliveOil, 30.0, "g"); ri(beefStew, salt, 8.0, "g");
        step(beefStew, 1, "Cut beef into 4cm chunks. Season with salt. Brown in batches in hot oil. Remove and set aside.");
        step(beefStew, 2, "In same pot, sauté onion and garlic until softened.");
        step(beefStew, 3, "Return beef. Add stock, bring to boil, skim foam.");
        step(beefStew, 4, "Add potatoes and carrots. Simmer covered on low heat 75 minutes until beef is tender.");
        step(beefStew, 5, "Adjust seasoning. Serve with crusty bread.");

        Recipe bananaBread = recipe("Banana Bread",
            "Moist and fragrant loaf with ripe bananas",
            10, 55, 8, Difficulty.EASY, desserts, admin,
            "The perfect use for overripe bananas — nobody will know",
            5.0, 42.0, 10.0, 2.0);
        recipeRepo.save(bananaBread);
        ri(bananaBread, banana, 3.0, "pcs"); ri(bananaBread, flour, 220.0, "g");
        ri(bananaBread, sugar, 100.0, "g"); ri(bananaBread, eggs, 2.0, "pcs");
        ri(bananaBread, butter, 80.0, "g"); ri(bananaBread, salt, 2.0, "g");
        step(bananaBread, 1, "Preheat oven to 175°C. Grease a loaf tin.");
        step(bananaBread, 2, "Mash bananas thoroughly with a fork.");
        step(bananaBread, 3, "Beat in melted butter, sugar and eggs.");
        step(bananaBread, 4, "Fold in flour and salt until just combined.");
        step(bananaBread, 5, "Pour into tin and bake 50–55 minutes. A skewer should come out clean. Cool before slicing.");

        Recipe caesar = recipe("Caesar Salad",
            "Crisp romaine with parmesan, croutons and Caesar dressing",
            15, 5, 4, Difficulty.EASY, salads, chef,
            "A steakhouse classic — simple ingredients, big flavour",
            10.0, 18.0, 16.0, 3.0);
        recipeRepo.save(caesar);
        ri(caesar, romaine, 400.0, "g"); ri(caesar, parmesan, 80.0, "g");
        ri(caesar, bread, 100.0, "g"); ri(caesar, garlic, 2.0, "pcs");
        ri(caesar, oliveOil, 40.0, "g"); ri(caesar, lemon, 1.0, "pcs");
        ri(caesar, dijonMust, 10.0, "g"); ri(caesar, salt, 3.0, "g");
        step(caesar, 1, "Cube bread, toss with olive oil and salt, bake at 200°C for 10 minutes until golden.");
        step(caesar, 2, "Whisk together olive oil, lemon juice, minced garlic, Dijon mustard and salt for dressing.");
        step(caesar, 3, "Tear romaine into a large bowl. Add croutons.");
        step(caesar, 4, "Drizzle dressing and toss well. Top with shaved parmesan and serve immediately.");

        Recipe stuffedPeppers = recipe("Stuffed Bell Peppers",
            "Colourful peppers filled with spiced beef and rice",
            20, 40, 4, Difficulty.MEDIUM, mexican, chef,
            "A crowd-pleaser — filling, colourful and satisfying",
            32.0, 38.0, 14.0, 4.0);
        recipeRepo.save(stuffedPeppers);
        ri(stuffedPeppers, bellPepper, 4.0, "pcs"); ri(stuffedPeppers, groundBeef, 400.0, "g");
        ri(stuffedPeppers, rice, 150.0, "g"); ri(stuffedPeppers, tomatoSauce, 200.0, "g");
        ri(stuffedPeppers, cheddar, 100.0, "g"); ri(stuffedPeppers, onion, 100.0, "g");
        ri(stuffedPeppers, cumin, 4.0, "g"); ri(stuffedPeppers, salt, 5.0, "g");
        step(stuffedPeppers, 1, "Preheat oven to 190°C. Cook rice until just done.");
        step(stuffedPeppers, 2, "Slice tops off peppers and remove seeds. Place in a baking dish.");
        step(stuffedPeppers, 3, "Brown onion and beef in a pan. Add cumin, salt and tomato sauce. Cook 5 minutes.");
        step(stuffedPeppers, 4, "Stir in cooked rice. Fill each pepper generously with the mixture.");
        step(stuffedPeppers, 5, "Top with cheddar. Cover with foil and bake 30 minutes. Uncover last 10 minutes.");

        Recipe teriyaki = recipe("Teriyaki Salmon",
            "Glazed salmon in a sweet soy and ginger teriyaki sauce",
            10, 15, 2, Difficulty.EASY, asian, chef,
            "Five ingredients, ten minutes — Japanese soul food",
            40.0, 18.0, 14.0, 0.0);
        recipeRepo.save(teriyaki);
        ri(teriyaki, salmon, 400.0, "g"); ri(teriyaki, soySauce, 60.0, "g");
        ri(teriyaki, honey, 30.0, "g"); ri(teriyaki, garlic, 2.0, "pcs");
        ri(teriyaki, ginger, 15.0, "g"); ri(teriyaki, rice, 200.0, "g");
        step(teriyaki, 1, "Mix soy sauce, honey, minced garlic, and grated ginger in a bowl.");
        step(teriyaki, 2, "Marinate salmon in half the sauce for 10 minutes.");
        step(teriyaki, 3, "Cook rice according to package instructions.");
        step(teriyaki, 4, "Heat a pan over medium-high. Sear salmon 3 minutes per side.");
        step(teriyaki, 5, "Pour remaining sauce into pan. Bubble 1 minute until glazed. Serve over rice.");

        Recipe aglioOlio = recipe("Spaghetti Aglio e Olio",
            "Simple pasta with golden garlic, olive oil and parmesan",
            5, 15, 2, Difficulty.EASY, italian, chef,
            "Six ingredients, pure magic — Roman fast food",
            14.0, 70.0, 20.0, 3.0);
        recipeRepo.save(aglioOlio);
        ri(aglioOlio, pasta, 200.0, "g"); ri(aglioOlio, garlic, 6.0, "pcs");
        ri(aglioOlio, oliveOil, 60.0, "g"); ri(aglioOlio, parmesan, 40.0, "g");
        ri(aglioOlio, salt, 4.0, "g");
        step(aglioOlio, 1, "Cook spaghetti in well-salted boiling water until al dente. Reserve 1 cup pasta water.");
        step(aglioOlio, 2, "Slice garlic thinly. Heat olive oil in a wide pan over medium-low heat.");
        step(aglioOlio, 3, "Add garlic to cold oil, then bring to medium. Cook until golden — do not burn.");
        step(aglioOlio, 4, "Add drained pasta and splash of pasta water. Toss vigorously over heat 1 minute.");
        step(aglioOlio, 5, "Remove from heat. Finish with parmesan and a drizzle of olive oil. Serve immediately.");

        Recipe caprese = recipe("Caprese Salad",
            "Layered fresh mozzarella and tomatoes drizzled with olive oil",
            10, 0, 4, Difficulty.EASY, salads, admin,
            "Summer on a plate — three ingredients, one masterpiece",
            12.0, 4.0, 16.0, 1.0);
        recipeRepo.save(caprese);
        ri(caprese, tomatoes, 400.0, "g"); ri(caprese, mozzarella, 300.0, "g");
        ri(caprese, oliveOil, 30.0, "g"); ri(caprese, salt, 3.0, "g");
        ri(caprese, lemon, 1.0, "pcs");
        step(caprese, 1, "Slice tomatoes and mozzarella into 1cm rounds.");
        step(caprese, 2, "Arrange alternating slices on a serving plate.");
        step(caprese, 3, "Drizzle generously with olive oil and a squeeze of lemon.");
        step(caprese, 4, "Season with flaky salt and serve at room temperature.");

        Recipe omelette = recipe("Veggie Omelette",
            "Fluffy eggs filled with sautéed spinach, pepper and onion",
            5, 10, 2, Difficulty.EASY, breakfast, chef,
            "Protein-packed and on the table in 15 minutes",
            16.0, 6.0, 14.0, 2.0);
        recipeRepo.save(omelette);
        ri(omelette, eggs, 4.0, "pcs"); ri(omelette, spinach, 80.0, "g");
        ri(omelette, bellPepper, 80.0, "g"); ri(omelette, onion, 60.0, "g");
        ri(omelette, butter, 15.0, "g"); ri(omelette, salt, 2.0, "g");
        step(omelette, 1, "Sauté diced onion and bell pepper in half the butter over medium heat until soft, 4 minutes.");
        step(omelette, 2, "Add spinach and cook 1 minute until wilted. Season with salt. Remove and set aside.");
        step(omelette, 3, "Beat eggs with a pinch of salt. Melt remaining butter in the pan over medium heat.");
        step(omelette, 4, "Pour in eggs. As edges set, gently draw them toward the centre with a spatula.");
        step(omelette, 5, "When just set, add filling to one half. Fold over and slide onto a plate. Serve immediately.");

        System.out.println("Database seeded: 2 users, 8 categories, 47 ingredients, 25 recipes");
    }

    private void migrateIfNeeded() {
        userRepo.findByUsername("admin").ifPresentOrElse(
            u -> {
                if (u.getRole() != Role.ADMIN) {
                    u.setRole(Role.ADMIN);
                    userRepo.save(u);
                    System.out.println("Migration: promoted admin to ADMIN role");
                }
            },
            () -> {
                User admin = new User();
                admin.setUsername("admin"); admin.setEmail("admin@recipes.com");
                admin.setPassword(passwordEncoder.encode("admin123")); admin.setRole(Role.ADMIN);
                userRepo.save(admin);
                System.out.println("Migration: created admin user");
            }
        );
        if (userRepo.findByUsername("chef").isEmpty()) {
            User chef = new User();
            chef.setUsername("chef"); chef.setEmail("chef@recipes.com");
            chef.setPassword(passwordEncoder.encode("chef1234")); chef.setRole(Role.USER);
            userRepo.save(chef);
        }

        ingredientRepo.findAll().forEach(i -> {
            boolean changed = false;
            if (i.getCategory() == null) {
                IngredientCategory cat = switch (i.getName()) {
                    case "All-purpose Flour" -> IngredientCategory.GRAINS;
                    case "Eggs"              -> IngredientCategory.EGGS;
                    case "Milk", "Butter"    -> IngredientCategory.DAIRY;
                    case "Sugar"             -> IngredientCategory.OTHER;
                    case "Salt"              -> IngredientCategory.SPICES;
                    case "Spaghetti"         -> IngredientCategory.GRAINS;
                    case "Tomato Sauce", "Garlic" -> IngredientCategory.PRODUCE;
                    case "Olive Oil"         -> IngredientCategory.OILS;
                    case "Chicken Breast"    -> IngredientCategory.POULTRY;
                    case "Jasmine Rice"      -> IngredientCategory.GRAINS;
                    case "Soy Sauce", "Cocoa Powder" -> IngredientCategory.OTHER;
                    default -> null;
                };
                if (cat != null) { i.setCategory(cat); changed = true; }
            }
            if (!i.isContainsGluten()) {
                boolean gluten = switch (i.getName()) {
                    case "All-purpose Flour", "Spaghetti", "Soy Sauce" -> true;
                    default -> false;
                };
                if (gluten) { i.setContainsGluten(true); changed = true; }
            }
            String unit = i.getDefaultUnit();
            if (unit != null && !unit.equals("g") && !unit.equals("kg") && !unit.equals("pcs")) {
                String fixed = switch (unit) {
                    case "ml", "mL"    -> "g";
                    case "cloves"      -> "pcs";
                    default            -> "g";
                };
                i.setDefaultUnit(fixed); changed = true;
            }
            if ("Eggs".equals(i.getName()) && i.getCalories() != null && i.getCalories() > 100) {
                i.setCalories(78.0); changed = true;
            }
            if (i.getProtein() == null) {
                double[] m = switch (i.getName()) {
                    case "All-purpose Flour" -> new double[]{10,  76,    1,    3};
                    case "Eggs"              -> new double[]{ 6,   0.6,  5,    0};
                    case "Milk"              -> new double[]{ 3.4, 4.8,  3.3,  0};
                    case "Butter"            -> new double[]{ 0.6, 0.1, 80,    0};
                    case "Sugar"             -> new double[]{ 0,  100,   0,    0};
                    case "Salt"              -> new double[]{ 0,    0,   0,    0};
                    case "Spaghetti"         -> new double[]{13,  70,    1.5,  3};
                    case "Tomato Sauce"      -> new double[]{ 1.5, 5.5,  0.2,  1.2};
                    case "Garlic"            -> new double[]{ 0.2, 1,    0,    0.1};
                    case "Olive Oil"         -> new double[]{ 0,   0,  100,    0};
                    case "Chicken Breast"    -> new double[]{31,   0,    3.6,  0};
                    case "Jasmine Rice"      -> new double[]{ 2.7,28,    0.3,  0.4};
                    case "Soy Sauce"         -> new double[]{ 5.5, 7,    0,    0.5};
                    case "Cocoa Powder"      -> new double[]{20,  58,   14,   37};
                    case "Onion"             -> new double[]{ 1.1, 9.3,  0.1,  1.7};
                    case "Bell Pepper"       -> new double[]{ 1,   6,    0.3,  2};
                    case "Ground Beef"       -> new double[]{17,   0,   20,    0};
                    case "Lemon"             -> new double[]{ 0.6, 5,    0.2,  1.5};
                    case "Parmesan"          -> new double[]{38,   4,   29,    0};
                    case "Heavy Cream"       -> new double[]{ 2.5, 3,   36,    0};
                    case "Tomatoes"          -> new double[]{ 0.9, 3.9,  0.2,  1.2};
                    case "Spinach"           -> new double[]{ 2.9, 3.6,  0.4,  2.2};
                    case "Mozzarella"        -> new double[]{22,   2.2, 17,    0};
                    case "Cheddar Cheese"    -> new double[]{25,   1.3, 33,    0};
                    case "Avocado"           -> new double[]{ 3,  12,   21,    9};
                    case "Cumin"             -> new double[]{18,  44,   22,   11};
                    case "Paprika"           -> new double[]{14,  54,   13,   34};
                    case "Dry Yeast"         -> new double[]{40,  47,    7,   26};
                    case "Honey"             -> new double[]{ 0.3,82,    0,    0.2};
                    case "Greek Yogurt"      -> new double[]{10,   4,    5,    0};
                    case "Salmon Fillet"     -> new double[]{25,   0,   13,    0};
                    case "Feta Cheese"       -> new double[]{14,   4,   21,    0};
                    case "Cucumber"          -> new double[]{ 0.7, 3.6,  0.1,  0.5};
                    case "Kalamata Olives"   -> new double[]{ 0.8, 6,   11,    1.5};
                    case "Red Lentils"       -> new double[]{ 9,  20,    0.4,  8};
                    case "Chickpeas"         -> new double[]{ 9,  27,    2.6,  8};
                    case "Beef Stock"        -> new double[]{ 1,   2.5,  0.2,  0};
                    case "Bread"             -> new double[]{ 9,  50,    3,    2};
                    case "Potatoes"          -> new double[]{ 2,  17,    0.1,  2};
                    case "Carrots"           -> new double[]{ 0.9,10,    0.2,  2.8};
                    case "Pancetta"          -> new double[]{12,   0,   44,    0};
                    case "Banana"            -> new double[]{ 1.1,23,    0.3,  2.6};
                    case "Fresh Ginger"      -> new double[]{ 1.8,18,    0.75, 2};
                    case "Coconut Milk"      -> new double[]{ 2.3, 6,   24,    0};
                    case "Curry Powder"      -> new double[]{12,  58,   15,   33};
                    case "Romaine Lettuce"   -> new double[]{ 1.2, 3.3,  0.3,  2.1};
                    case "Dijon Mustard"     -> new double[]{ 4,   5,    4,    2};
                    default -> null;
                };
                if (m != null) {
                    i.setProtein(m[0]); i.setCarbs(m[1]); i.setFat(m[2]); i.setFiber(m[3]);
                    changed = true;
                }
            }
            if (changed) ingredientRepo.save(i);
        });

        riRepo.findAll().forEach(ri -> {
            String u = ri.getUnit();
            if (u != null && !u.equals("g") && !u.equals("kg") && !u.equals("pcs")) {
                String fixed = switch (u) {
                    case "ml", "mL", "L", "l", "cup", "cups" -> "g";
                    case "cloves", "clove", "piece", "pieces" -> "pcs";
                    default -> "g";
                };
                ri.setUnit(fixed);
                riRepo.save(ri);
                System.out.println("Migration: fixed recipe_ingredient unit '" + u + "' → '" + fixed + "'");
            }
        });

        recipeRepo.findAll().forEach(r -> {
            boolean changed = false;
            if (r.getCuisine() == null) {
                String cuisine = switch (r.getName()) {
                    case "Spaghetti Marinara"        -> "Italian";
                    case "Garlic Chicken Fried Rice" -> "Asian";
                    case "Fluffy Pancakes"           -> "American";
                    case "Chocolate Fudge Cake"      -> "American";
                    default -> null;
                };
                if (cuisine != null) { r.setCuisine(cuisine); changed = true; }
            }
            if (r.getBlurb() == null) {
                String blurb = switch (r.getName()) {
                    case "Spaghetti Marinara"        -> "Classic Sunday pasta, done in 30 minutes";
                    case "Garlic Chicken Fried Rice" -> "One-wok weeknight wonder";
                    case "Fluffy Pancakes"           -> "Weekend brunch perfection";
                    case "Chocolate Fudge Cake"      -> "Celebration-worthy dark chocolate";
                    default -> null;
                };
                if (blurb != null) { r.setBlurb(blurb); changed = true; }
            }
            if (r.getProtein() == null) {
                Double[] macros = switch (r.getName()) {
                    case "Spaghetti Marinara"        -> new Double[]{12.0, 68.0, 8.0, 4.0};
                    case "Garlic Chicken Fried Rice" -> new Double[]{32.0, 45.0, 10.0, 2.0};
                    case "Fluffy Pancakes"           -> new Double[]{8.0, 42.0, 9.0, 1.0};
                    case "Chocolate Fudge Cake"      -> new Double[]{6.0, 58.0, 22.0, 3.0};
                    default -> null;
                };
                if (macros != null) {
                    r.setProtein(macros[0]); r.setCarbs(macros[1]);
                    r.setFat(macros[2]); r.setFiber(macros[3]); changed = true;
                }
            }
            if (changed) recipeRepo.save(r);
        });

        addCatIfMissing("Mediterranean", "Greek, Turkish, Lebanese cuisine");
        addCatIfMissing("Mexican",       "Tacos, burritos, guacamole and more");
        addCatIfMissing("Soups & Stews", "Warming bowls and hearty stews");
        addCatIfMissing("Salads",        "Fresh and vibrant salads");

        addIngIfMissing("Onion",          "Yellow onion",                     "g",   40.0,  1.1,  9.3,  0.1,  1.7,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Bell Pepper",    "Red or green bell pepper",         "g",   31.0,  1.0,  6.0,  0.3,  2.0,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Ground Beef",    "80/20 minced beef",                "g",   250.0, 17.0, 0.0,  20.0, 0.0,  IngredientCategory.MEAT,     false);
        addIngIfMissing("Lemon",          "Fresh lemon",                      "pcs", 17.0,  0.6,  5.0,  0.2,  1.5,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Parmesan",       "Hard aged Italian cheese",         "g",   431.0, 38.0, 4.0,  29.0, 0.0,  IngredientCategory.DAIRY,    false);
        addIngIfMissing("Heavy Cream",    "Whipping cream 35% fat",           "g",   340.0, 2.5,  3.0,  36.0, 0.0,  IngredientCategory.DAIRY,    false);
        addIngIfMissing("Tomatoes",       "Fresh ripe tomatoes",              "g",   18.0,  0.9,  3.9,  0.2,  1.2,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Spinach",        "Fresh baby spinach",               "g",   23.0,  2.9,  3.6,  0.4,  2.2,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Mozzarella",     "Fresh mozzarella cheese",          "g",   280.0, 22.0, 2.2,  17.0, 0.0,  IngredientCategory.DAIRY,    false);
        addIngIfMissing("Cheddar Cheese", "Mature cheddar",                   "g",   403.0, 25.0, 1.3,  33.0, 0.0,  IngredientCategory.DAIRY,    false);
        addIngIfMissing("Avocado",        "Ripe Hass avocado",                "pcs", 234.0, 3.0,  12.0, 21.0, 9.0,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Cumin",          "Ground cumin spice",               "g",   375.0, 18.0, 44.0, 22.0, 11.0, IngredientCategory.SPICES,   false);
        addIngIfMissing("Paprika",        "Sweet smoked paprika",             "g",   282.0, 14.0, 54.0, 13.0, 34.0, IngredientCategory.SPICES,   false);
        addIngIfMissing("Dry Yeast",      "Active dry yeast",                 "g",   325.0, 40.0, 47.0, 7.0,  26.0, IngredientCategory.OTHER,    false);
        addIngIfMissing("Honey",          "Pure natural honey",               "g",   304.0, 0.3,  82.0, 0.0,  0.2,  IngredientCategory.OTHER,    false);
        addIngIfMissing("Greek Yogurt",   "Full-fat strained yogurt",         "g",   97.0,  10.0, 4.0,  5.0,  0.0,  IngredientCategory.DAIRY,    false);
        addIngIfMissing("Salmon Fillet",  "Atlantic salmon fillet",           "g",   208.0, 25.0, 0.0,  13.0, 0.0,  IngredientCategory.SEAFOOD,  false);
        addIngIfMissing("Feta Cheese",    "Crumbled Greek feta",              "g",   264.0, 14.0, 4.0,  21.0, 0.0,  IngredientCategory.DAIRY,    false);
        addIngIfMissing("Cucumber",       "English cucumber",                 "g",   16.0,  0.7,  3.6,  0.1,  0.5,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Kalamata Olives","Pitted Kalamata olives",           "g",   115.0, 0.8,  6.0,  11.0, 1.5,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Red Lentils",    "Dried red lentils",                "g",   116.0, 9.0,  20.0, 0.4,  8.0,  IngredientCategory.LEGUMES,  false);
        addIngIfMissing("Chickpeas",      "Cooked or canned chickpeas",       "g",   164.0, 9.0,  27.0, 2.6,  8.0,  IngredientCategory.LEGUMES,  false);
        addIngIfMissing("Beef Stock",     "Rich beef broth",                  "g",   17.0,  1.0,  2.5,  0.2,  0.0,  IngredientCategory.OTHER,    false);
        addIngIfMissing("Bread",          "Sourdough or crusty white bread",  "g",   265.0, 9.0,  50.0, 3.0,  2.0,  IngredientCategory.GRAINS,   true);
        addIngIfMissing("Potatoes",       "Floury potatoes",                  "g",   77.0,  2.0,  17.0, 0.1,  2.0,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Carrots",        "Fresh carrots",                    "g",   41.0,  0.9,  10.0, 0.2,  2.8,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Pancetta",       "Cured Italian pork belly",         "g",   458.0, 12.0, 0.0,  44.0, 0.0,  IngredientCategory.MEAT,     false);
        addIngIfMissing("Banana",         "Ripe banana",                      "pcs", 89.0,  1.1,  23.0, 0.3,  2.6,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Fresh Ginger",   "Fresh ginger root",                "g",   80.0,  1.8,  18.0, 0.75, 2.0,  IngredientCategory.SPICES,   false);
        addIngIfMissing("Coconut Milk",   "Full-fat coconut milk",            "g",   230.0, 2.3,  6.0,  24.0, 0.0,  IngredientCategory.OTHER,    false);
        addIngIfMissing("Curry Powder",   "Mild curry spice blend",           "g",   325.0, 12.0, 58.0, 15.0, 33.0, IngredientCategory.SPICES,   false);
        addIngIfMissing("Romaine Lettuce","Crisp romaine lettuce",            "g",   17.0,  1.2,  3.3,  0.3,  2.1,  IngredientCategory.PRODUCE,  false);
        addIngIfMissing("Dijon Mustard",  "French Dijon mustard",             "g",   66.0,  4.0,  5.0,  4.0,  2.0,  IngredientCategory.SPICES,   false);

        User admin = userRepo.findByUsername("admin").orElse(null);
        User chef  = userRepo.findByUsername("chef").orElse(null);
        if (admin == null || chef == null) { System.out.println("Migration: users not found"); return; }

        addRecipesIfMissing(admin, chef);

        System.out.println("Migration complete");
    }

    private void addRecipesIfMissing(User admin, User chef) {
        Category med      = categoryRepo.findByName("Mediterranean").orElse(null);
        Category mexican  = categoryRepo.findByName("Mexican").orElse(null);
        Category soups    = categoryRepo.findByName("Soups & Stews").orElse(null);
        Category salads   = categoryRepo.findByName("Salads").orElse(null);
        Category italian  = categoryRepo.findByName("Italian").orElse(null);
        Category asian    = categoryRepo.findByName("Asian").orElse(null);
        Category breakfast = categoryRepo.findByName("Breakfast").orElse(null);
        Category desserts = categoryRepo.findByName("Desserts").orElse(null);

        if (!recipeRepo.existsByName("Beef Tacos") && mexican != null) {
            Ingredient gb = ing("Ground Beef"); Ingredient on = ing("Onion");
            Ingredient bp = ing("Bell Pepper"); Ingredient cu = ing("Cumin");
            Ingredient pa = ing("Paprika");     Ingredient sa = ing("Salt");
            Ingredient ch = ing("Cheddar Cheese"); Ingredient av = ing("Avocado");
            if (gb != null && on != null && bp != null && cu != null && av != null) {
                Recipe r = recipe("Beef Tacos", "Juicy spiced ground beef tacos with fresh toppings",
                    15, 20, 4, Difficulty.MEDIUM, mexican, chef,
                    "Tuesday night or any night, these disappear fast", 28.0, 32.0, 18.0, 4.0);
                recipeRepo.save(r);
                if (gb != null) ri(r, gb, 500.0, "g"); if (on != null) ri(r, on, 120.0, "g");
                if (bp != null) ri(r, bp, 100.0, "g"); if (cu != null) ri(r, cu, 5.0, "g");
                if (pa != null) ri(r, pa, 5.0, "g");   if (sa != null) ri(r, sa, 4.0, "g");
                if (ch != null) ri(r, ch, 80.0, "g");  if (av != null) ri(r, av, 1.0, "pcs");
                step(r, 1, "Cook diced onion and bell pepper in oil until softened.");
                step(r, 2, "Add ground beef, break up, cook until browned.");
                step(r, 3, "Add cumin, paprika, salt. Cook 2 more minutes.");
                step(r, 4, "Mash avocado with a pinch of salt.");
                step(r, 5, "Fill warm tortillas with beef, guacamole and cheddar.");
            }
        }

        if (!recipeRepo.existsByName("Greek Salad") && salads != null) {
            Ingredient to = ing("Tomatoes");       Ingredient cu = ing("Cucumber");
            Ingredient bp = ing("Bell Pepper");    Ingredient ol = ing("Kalamata Olives");
            Ingredient ft = ing("Feta Cheese");    Ingredient oo = ing("Olive Oil");
            Ingredient le = ing("Lemon");          Ingredient sa = ing("Salt");
            if (to != null && ft != null && ol != null) {
                Recipe r = recipe("Greek Salad", "Crisp vegetables with salty feta and olives",
                    15, 0, 4, Difficulty.EASY, salads, chef,
                    "No-cook, all flavour — ready in 15 minutes", 6.0, 14.0, 14.0, 3.0);
                recipeRepo.save(r);
                if (to != null) ri(r, to, 300.0, "g"); if (cu != null) ri(r, cu, 200.0, "g");
                if (bp != null) ri(r, bp, 100.0, "g"); if (ol != null) ri(r, ol, 80.0, "g");
                if (ft != null) ri(r, ft, 150.0, "g"); if (oo != null) ri(r, oo, 30.0, "g");
                if (le != null) ri(r, le, 1.0, "pcs");  if (sa != null) ri(r, sa, 3.0, "g");
                step(r, 1, "Cut tomatoes into wedges. Slice cucumber. Dice pepper.");
                step(r, 2, "Combine vegetables and olives in a large bowl.");
                step(r, 3, "Drizzle with olive oil and lemon juice. Season with salt.");
                step(r, 4, "Top with crumbled feta and serve immediately.");
            }
        }

        if (!recipeRepo.existsByName("Creamy Tomato Soup") && soups != null) {
            Ingredient to = ing("Tomatoes");   Ingredient on = ing("Onion");
            Ingredient ga = ing("Garlic");     Ingredient bt = ing("Butter");
            Ingredient hc = ing("Heavy Cream");Ingredient sa = ing("Salt");
            if (to != null && on != null) {
                Recipe r = recipe("Creamy Tomato Soup", "Silky blended tomato soup with a touch of cream",
                    10, 25, 4, Difficulty.EASY, soups, admin,
                    "Blissfully simple — a bowlful of comfort", 4.0, 18.0, 9.0, 3.0);
                recipeRepo.save(r);
                if (to != null) ri(r, to, 800.0, "g"); if (on != null) ri(r, on, 150.0, "g");
                if (ga != null) ri(r, ga, 3.0, "pcs");  if (bt != null) ri(r, bt, 30.0, "g");
                if (hc != null) ri(r, hc, 100.0, "g");  if (sa != null) ri(r, sa, 5.0, "g");
                step(r, 1, "Melt butter. Sauté onion 8 minutes until translucent.");
                step(r, 2, "Add garlic and chopped tomatoes. Season and simmer 20 minutes.");
                step(r, 3, "Blend until smooth. Stir in cream. Adjust seasoning.");
            }
        }

        if (!recipeRepo.existsByName("Margherita Pizza") && italian != null) {
            Ingredient fl = ing("All-purpose Flour"); Ingredient ye = ing("Dry Yeast");
            Ingredient sa = ing("Salt");              Ingredient oo = ing("Olive Oil");
            Ingredient ts = ing("Tomato Sauce");      Ingredient mz = ing("Mozzarella");
            if (fl != null && mz != null) {
                Recipe r = recipe("Margherita Pizza", "Classic Neapolitan pizza with fresh mozzarella",
                    30, 15, 4, Difficulty.MEDIUM, italian, chef,
                    "The one that started it all", 16.0, 48.0, 14.0, 2.0);
                recipeRepo.save(r);
                if (fl != null) ri(r, fl, 300.0, "g"); if (ye != null) ri(r, ye, 5.0, "g");
                if (sa != null) ri(r, sa, 6.0, "g");   if (oo != null) ri(r, oo, 20.0, "g");
                if (ts != null) ri(r, ts, 150.0, "g"); if (mz != null) ri(r, mz, 200.0, "g");
                step(r, 1, "Mix flour, yeast, salt, oil and 180ml warm water. Knead 10 minutes.");
                step(r, 2, "Rest 1 hour. Preheat oven to 250°C.");
                step(r, 3, "Stretch dough, spread sauce, add mozzarella.");
                step(r, 4, "Bake 10–12 minutes until crust is charred and cheese bubbling.");
            }
        }

        if (!recipeRepo.existsByName("Lemon Garlic Salmon") && med != null) {
            Ingredient sl = ing("Salmon Fillet"); Ingredient le = ing("Lemon");
            Ingredient ga = ing("Garlic");        Ingredient bt = ing("Butter");
            Ingredient oo = ing("Olive Oil");     Ingredient sa = ing("Salt");
            if (sl != null) {
                Recipe r = recipe("Lemon Garlic Salmon", "Pan-seared salmon with bright lemon and garlic butter",
                    10, 15, 2, Difficulty.MEDIUM, med, chef,
                    "Restaurant-quality in under 25 minutes", 42.0, 4.0, 18.0, 0.0);
                recipeRepo.save(r);
                if (sl != null) ri(r, sl, 400.0, "g"); if (le != null) ri(r, le, 1.0, "pcs");
                if (ga != null) ri(r, ga, 3.0, "pcs");  if (bt != null) ri(r, bt, 30.0, "g");
                if (oo != null) ri(r, oo, 15.0, "g");   if (sa != null) ri(r, sa, 3.0, "g");
                step(r, 1, "Pat salmon dry and season with salt.");
                step(r, 2, "Sear skin-side up in hot oil for 3 minutes.");
                step(r, 3, "Flip. Add butter and garlic. Baste salmon continuously.");
                step(r, 4, "Squeeze lemon over. Cook 3 more minutes then rest before serving.");
            }
        }

        if (!recipeRepo.existsByName("Red Lentil Soup") && soups != null) {
            Ingredient lt = ing("Red Lentils");   Ingredient on = ing("Onion");
            Ingredient ga = ing("Garlic");        Ingredient cu = ing("Cumin");
            Ingredient pa = ing("Paprika");       Ingredient oo = ing("Olive Oil");
            Ingredient le = ing("Lemon");         Ingredient sa = ing("Salt");
            if (lt != null) {
                Recipe r = recipe("Red Lentil Soup", "Hearty Middle-Eastern lentil soup with warm spices",
                    10, 30, 6, Difficulty.EASY, soups, admin,
                    "Deeply nourishing — spiced, golden and ready in 40 minutes", 18.0, 42.0, 3.0, 8.0);
                recipeRepo.save(r);
                if (lt != null) ri(r, lt, 300.0, "g"); if (on != null) ri(r, on, 200.0, "g");
                if (ga != null) ri(r, ga, 4.0, "pcs");  if (cu != null) ri(r, cu, 6.0, "g");
                if (pa != null) ri(r, pa, 4.0, "g");    if (oo != null) ri(r, oo, 30.0, "g");
                if (le != null) ri(r, le, 1.0, "pcs");  if (sa != null) ri(r, sa, 6.0, "g");
                step(r, 1, "Sauté onion in oil until deeply golden, about 12 minutes.");
                step(r, 2, "Add garlic, cumin, paprika. Cook 1 minute.");
                step(r, 3, "Add lentils and 1.2L water. Boil then simmer 25 minutes.");
                step(r, 4, "Blend half for creamy-chunky texture. Finish with lemon juice.");
            }
        }

        if (!recipeRepo.existsByName("Shakshuka") && breakfast != null) {
            Ingredient eg = ing("Eggs");         Ingredient to = ing("Tomatoes");
            Ingredient bp = ing("Bell Pepper");  Ingredient on = ing("Onion");
            Ingredient ga = ing("Garlic");       Ingredient cu = ing("Cumin");
            Ingredient pa = ing("Paprika");      Ingredient oo = ing("Olive Oil");
            if (eg != null && to != null) {
                Recipe r = recipe("Shakshuka", "Eggs poached in a spiced tomato and pepper sauce",
                    10, 20, 3, Difficulty.EASY, breakfast, chef,
                    "One pan, big flavour — brunch done right", 12.0, 16.0, 10.0, 4.0);
                recipeRepo.save(r);
                if (eg != null) ri(r, eg, 5.0, "pcs");  if (to != null) ri(r, to, 500.0, "g");
                if (bp != null) ri(r, bp, 150.0, "g");  if (on != null) ri(r, on, 120.0, "g");
                if (ga != null) ri(r, ga, 3.0, "pcs");  if (cu != null) ri(r, cu, 4.0, "g");
                if (pa != null) ri(r, pa, 4.0, "g");    if (oo != null) ri(r, oo, 20.0, "g");
                step(r, 1, "Sauté onion and pepper in oil until soft, 8 minutes.");
                step(r, 2, "Add garlic, spices. Cook 1 minute until fragrant.");
                step(r, 3, "Add tomatoes, simmer 10 minutes until thickened.");
                step(r, 4, "Make wells in sauce. Crack in eggs. Cover and cook 5–7 minutes until whites are just set.");
            }
        }

        if (!recipeRepo.existsByName("Pasta Carbonara") && italian != null) {
            Ingredient pa = ing("Spaghetti"); Ingredient eg = ing("Eggs");
            Ingredient pn = ing("Pancetta"); Ingredient pm = ing("Parmesan");
            Ingredient ga = ing("Garlic");   Ingredient sa = ing("Salt");
            if (pa != null && eg != null) {
                Recipe r = recipe("Pasta Carbonara", "Roman pasta with eggs, pancetta and parmesan",
                    10, 15, 2, Difficulty.MEDIUM, italian, chef,
                    "Rome's finest — silky, rich, no cream needed", 28.0, 58.0, 22.0, 2.0);
                recipeRepo.save(r);
                if (pa != null) ri(r, pa, 200.0, "g"); if (eg != null) ri(r, eg, 3.0, "pcs");
                if (pn != null) ri(r, pn, 100.0, "g"); if (pm != null) ri(r, pm, 60.0, "g");
                if (ga != null) ri(r, ga, 2.0, "pcs"); if (sa != null) ri(r, sa, 3.0, "g");
                step(r, 1, "Cook spaghetti al dente. Reserve 1 cup pasta water.");
                step(r, 2, "Render pancetta with garlic until crispy.");
                step(r, 3, "Beat eggs with parmesan. Off heat, toss pasta with pancetta.");
                step(r, 4, "Pour egg mixture over, tossing rapidly. Add pasta water to make it silky.");
            }
        }

        if (!recipeRepo.existsByName("Chicken Tikka Masala") && asian != null) {
            Ingredient ch = ing("Chicken Breast"); Ingredient ts = ing("Tomato Sauce");
            Ingredient cm = ing("Coconut Milk");   Ingredient on = ing("Onion");
            Ingredient ga = ing("Garlic");         Ingredient gi = ing("Fresh Ginger");
            Ingredient cp = ing("Curry Powder");   Ingredient sa = ing("Salt");
            if (ch != null) {
                Recipe r = recipe("Chicken Tikka Masala", "Tender chicken in a rich, spiced tomato-coconut sauce",
                    20, 30, 4, Difficulty.MEDIUM, asian, chef,
                    "The nation's favourite curry — bold, creamy and aromatic", 38.0, 24.0, 16.0, 4.0);
                recipeRepo.save(r);
                if (ch != null) ri(r, ch, 600.0, "g"); if (ts != null) ri(r, ts, 400.0, "g");
                if (cm != null) ri(r, cm, 200.0, "g"); if (on != null) ri(r, on, 150.0, "g");
                if (ga != null) ri(r, ga, 4.0, "pcs");  if (gi != null) ri(r, gi, 20.0, "g");
                if (cp != null) ri(r, cp, 15.0, "g");   if (sa != null) ri(r, sa, 6.0, "g");
                step(r, 1, "Marinate chicken in curry powder and salt for 20 minutes.");
                step(r, 2, "Pan-fry until charred at edges. Set aside.");
                step(r, 3, "Sauté onion, garlic, ginger in oil until golden.");
                step(r, 4, "Add remaining curry powder, tomato sauce, simmer 10 minutes.");
                step(r, 5, "Add coconut milk and chicken. Simmer 10 minutes. Serve with rice.");
            }
        }

        if (!recipeRepo.existsByName("Avocado Toast") && breakfast != null) {
            Ingredient br = ing("Bread");   Ingredient av = ing("Avocado");
            Ingredient le = ing("Lemon");   Ingredient sa = ing("Salt");
            Ingredient oo = ing("Olive Oil");
            if (av != null && br != null) {
                Recipe r = recipe("Avocado Toast", "Smashed avocado on toasted sourdough with lemon and salt",
                    5, 5, 2, Difficulty.EASY, breakfast, admin,
                    "Millennial classic — still undefeated at brunch", 6.0, 28.0, 16.0, 8.0);
                recipeRepo.save(r);
                if (br != null) ri(r, br, 150.0, "g"); if (av != null) ri(r, av, 2.0, "pcs");
                if (le != null) ri(r, le, 1.0, "pcs"); if (sa != null) ri(r, sa, 2.0, "g");
                if (oo != null) ri(r, oo, 10.0, "g");
                step(r, 1, "Toast bread until golden and crisp.");
                step(r, 2, "Mash avocado with lemon juice, salt, and olive oil.");
                step(r, 3, "Spread generously on toast and serve immediately.");
            }
        }

        if (!recipeRepo.existsByName("Beef Stew") && soups != null) {
            Ingredient gb = ing("Ground Beef"); Ingredient po = ing("Potatoes");
            Ingredient ca = ing("Carrots");     Ingredient on = ing("Onion");
            Ingredient ga = ing("Garlic");      Ingredient bs = ing("Beef Stock");
            Ingredient oo = ing("Olive Oil");   Ingredient sa = ing("Salt");
            if (gb != null && bs != null) {
                Recipe r = recipe("Beef Stew", "Slow-cooked beef with tender vegetables in rich gravy",
                    20, 90, 6, Difficulty.MEDIUM, soups, admin,
                    "Sunday comfort — worth every hour of the simmer", 42.0, 28.0, 18.0, 5.0);
                recipeRepo.save(r);
                if (gb != null) ri(r, gb, 800.0, "g"); if (po != null) ri(r, po, 400.0, "g");
                if (ca != null) ri(r, ca, 200.0, "g"); if (on != null) ri(r, on, 200.0, "g");
                if (ga != null) ri(r, ga, 4.0, "pcs");  if (bs != null) ri(r, bs, 600.0, "g");
                if (oo != null) ri(r, oo, 30.0, "g");   if (sa != null) ri(r, sa, 8.0, "g");
                step(r, 1, "Brown beef in batches in hot oil. Remove and set aside.");
                step(r, 2, "Sauté onion and garlic until softened.");
                step(r, 3, "Return beef, add stock. Bring to boil, skim foam.");
                step(r, 4, "Add potatoes and carrots. Simmer covered 75 minutes.");
                step(r, 5, "Adjust seasoning. Serve with crusty bread.");
            }
        }

        if (!recipeRepo.existsByName("Banana Bread") && desserts != null) {
            Ingredient bn = ing("Banana"); Ingredient fl = ing("All-purpose Flour");
            Ingredient sg = ing("Sugar");  Ingredient eg = ing("Eggs");
            Ingredient bt = ing("Butter"); Ingredient sa = ing("Salt");
            if (bn != null && fl != null) {
                Recipe r = recipe("Banana Bread", "Moist and fragrant loaf with ripe bananas",
                    10, 55, 8, Difficulty.EASY, desserts, admin,
                    "The perfect use for overripe bananas", 5.0, 42.0, 10.0, 2.0);
                recipeRepo.save(r);
                if (bn != null) ri(r, bn, 3.0, "pcs"); if (fl != null) ri(r, fl, 220.0, "g");
                if (sg != null) ri(r, sg, 100.0, "g"); if (eg != null) ri(r, eg, 2.0, "pcs");
                if (bt != null) ri(r, bt, 80.0, "g");  if (sa != null) ri(r, sa, 2.0, "g");
                step(r, 1, "Preheat oven to 175°C. Grease a loaf tin.");
                step(r, 2, "Mash bananas. Beat in butter, sugar and eggs.");
                step(r, 3, "Fold in flour and salt until just combined.");
                step(r, 4, "Pour into tin. Bake 50–55 minutes until a skewer comes out clean.");
            }
        }

        if (!recipeRepo.existsByName("Caesar Salad") && salads != null) {
            Ingredient ro = ing("Romaine Lettuce"); Ingredient pm = ing("Parmesan");
            Ingredient br = ing("Bread");           Ingredient ga = ing("Garlic");
            Ingredient oo = ing("Olive Oil");       Ingredient le = ing("Lemon");
            Ingredient dm = ing("Dijon Mustard");   Ingredient sa = ing("Salt");
            if (ro != null) {
                Recipe r = recipe("Caesar Salad", "Crisp romaine with parmesan, croutons and Caesar dressing",
                    15, 5, 4, Difficulty.EASY, salads, chef,
                    "A steakhouse classic — simple ingredients, big flavour", 10.0, 18.0, 16.0, 3.0);
                recipeRepo.save(r);
                if (ro != null) ri(r, ro, 400.0, "g"); if (pm != null) ri(r, pm, 80.0, "g");
                if (br != null) ri(r, br, 100.0, "g"); if (ga != null) ri(r, ga, 2.0, "pcs");
                if (oo != null) ri(r, oo, 40.0, "g");  if (le != null) ri(r, le, 1.0, "pcs");
                if (dm != null) ri(r, dm, 10.0, "g");  if (sa != null) ri(r, sa, 3.0, "g");
                step(r, 1, "Cube bread, toss with oil and salt. Bake at 200°C for 10 minutes.");
                step(r, 2, "Whisk olive oil, lemon juice, garlic, mustard and salt for dressing.");
                step(r, 3, "Tear romaine into bowl. Add croutons. Drizzle dressing and toss.");
                step(r, 4, "Top with shaved parmesan and serve immediately.");
            }
        }

        if (!recipeRepo.existsByName("Stuffed Bell Peppers") && mexican != null) {
            Ingredient bp = ing("Bell Pepper");  Ingredient gb = ing("Ground Beef");
            Ingredient ri2 = ing("Jasmine Rice"); Ingredient ts = ing("Tomato Sauce");
            Ingredient ch = ing("Cheddar Cheese"); Ingredient on = ing("Onion");
            Ingredient cu = ing("Cumin");          Ingredient sa = ing("Salt");
            if (bp != null && gb != null) {
                Recipe r = recipe("Stuffed Bell Peppers", "Colourful peppers filled with spiced beef and rice",
                    20, 40, 4, Difficulty.MEDIUM, mexican, chef,
                    "A crowd-pleaser — filling, colourful and satisfying", 32.0, 38.0, 14.0, 4.0);
                recipeRepo.save(r);
                if (bp != null)  ri(r, bp, 4.0, "pcs");   if (gb != null)  ri(r, gb, 400.0, "g");
                if (ri2 != null) ri(r, ri2, 150.0, "g");  if (ts != null)  ri(r, ts, 200.0, "g");
                if (ch != null)  ri(r, ch, 100.0, "g");   if (on != null)  ri(r, on, 100.0, "g");
                if (cu != null)  ri(r, cu, 4.0, "g");     if (sa != null)  ri(r, sa, 5.0, "g");
                step(r, 1, "Preheat oven to 190°C. Cook rice.");
                step(r, 2, "Slice tops off peppers, remove seeds, place in a baking dish.");
                step(r, 3, "Brown onion and beef. Add cumin, salt, tomato sauce. Cook 5 minutes.");
                step(r, 4, "Mix in rice. Fill peppers. Top with cheddar.");
                step(r, 5, "Cover with foil and bake 30 minutes. Uncover last 10 minutes.");
            }
        }

        if (!recipeRepo.existsByName("Teriyaki Salmon") && asian != null) {
            Ingredient sl = ing("Salmon Fillet"); Ingredient ss = ing("Soy Sauce");
            Ingredient hn = ing("Honey");         Ingredient ga = ing("Garlic");
            Ingredient gi = ing("Fresh Ginger");  Ingredient ri2 = ing("Jasmine Rice");
            if (sl != null) {
                Recipe r = recipe("Teriyaki Salmon", "Glazed salmon in a sweet soy and ginger teriyaki sauce",
                    10, 15, 2, Difficulty.EASY, asian, chef,
                    "Five ingredients, ten minutes — Japanese soul food", 40.0, 18.0, 14.0, 0.0);
                recipeRepo.save(r);
                if (sl != null)  ri(r, sl, 400.0, "g");  if (ss != null)  ri(r, ss, 60.0, "g");
                if (hn != null)  ri(r, hn, 30.0, "g");   if (ga != null)  ri(r, ga, 2.0, "pcs");
                if (gi != null)  ri(r, gi, 15.0, "g");   if (ri2 != null) ri(r, ri2, 200.0, "g");
                step(r, 1, "Mix soy sauce, honey, minced garlic and grated ginger.");
                step(r, 2, "Marinate salmon in half the sauce for 10 minutes.");
                step(r, 3, "Cook rice. Sear salmon 3 minutes per side in a hot pan.");
                step(r, 4, "Add remaining sauce, bubble 1 minute until glazed. Serve over rice.");
            }
        }

        if (!recipeRepo.existsByName("Spaghetti Aglio e Olio") && italian != null) {
            Ingredient pa = ing("Spaghetti"); Ingredient ga = ing("Garlic");
            Ingredient oo = ing("Olive Oil"); Ingredient pm = ing("Parmesan");
            Ingredient sa = ing("Salt");
            if (pa != null && ga != null) {
                Recipe r = recipe("Spaghetti Aglio e Olio", "Simple pasta with golden garlic, olive oil and parmesan",
                    5, 15, 2, Difficulty.EASY, italian, chef,
                    "Six ingredients, pure magic — Roman fast food", 14.0, 70.0, 20.0, 3.0);
                recipeRepo.save(r);
                if (pa != null) ri(r, pa, 200.0, "g"); if (ga != null) ri(r, ga, 6.0, "pcs");
                if (oo != null) ri(r, oo, 60.0, "g");  if (pm != null) ri(r, pm, 40.0, "g");
                if (sa != null) ri(r, sa, 4.0, "g");
                step(r, 1, "Cook spaghetti in salted water until al dente. Reserve 1 cup pasta water.");
                step(r, 2, "Slice garlic. Heat olive oil and garlic from cold over medium-low until golden.");
                step(r, 3, "Toss in drained pasta with pasta water. Finish with parmesan and olive oil.");
            }
        }

        if (!recipeRepo.existsByName("Caprese Salad") && salads != null) {
            Ingredient to = ing("Tomatoes");   Ingredient mz = ing("Mozzarella");
            Ingredient oo = ing("Olive Oil");  Ingredient sa = ing("Salt");
            Ingredient le = ing("Lemon");
            if (to != null && mz != null) {
                Recipe r = recipe("Caprese Salad", "Layered fresh mozzarella and tomatoes drizzled with olive oil",
                    10, 0, 4, Difficulty.EASY, salads, admin,
                    "Summer on a plate — three ingredients, one masterpiece", 12.0, 4.0, 16.0, 1.0);
                recipeRepo.save(r);
                if (to != null) ri(r, to, 400.0, "g"); if (mz != null) ri(r, mz, 300.0, "g");
                if (oo != null) ri(r, oo, 30.0, "g");  if (sa != null) ri(r, sa, 3.0, "g");
                if (le != null) ri(r, le, 1.0, "pcs");
                step(r, 1, "Slice tomatoes and mozzarella into 1cm rounds.");
                step(r, 2, "Arrange alternating slices on a plate. Drizzle with olive oil and lemon. Season and serve.");
            }
        }

        if (!recipeRepo.existsByName("Veggie Omelette") && breakfast != null) {
            Ingredient eg = ing("Eggs");       Ingredient sp = ing("Spinach");
            Ingredient bp = ing("Bell Pepper");Ingredient on = ing("Onion");
            Ingredient bt = ing("Butter");     Ingredient sa = ing("Salt");
            if (eg != null) {
                Recipe r = recipe("Veggie Omelette", "Fluffy eggs filled with sautéed spinach, pepper and onion",
                    5, 10, 2, Difficulty.EASY, breakfast, chef,
                    "Protein-packed and on the table in 15 minutes", 16.0, 6.0, 14.0, 2.0);
                recipeRepo.save(r);
                if (eg != null) ri(r, eg, 4.0, "pcs");  if (sp != null) ri(r, sp, 80.0, "g");
                if (bp != null) ri(r, bp, 80.0, "g");   if (on != null) ri(r, on, 60.0, "g");
                if (bt != null) ri(r, bt, 15.0, "g");   if (sa != null) ri(r, sa, 2.0, "g");
                step(r, 1, "Sauté onion and pepper in half the butter until soft, 4 minutes. Add spinach 1 minute.");
                step(r, 2, "Beat eggs with salt. Melt remaining butter. Pour in eggs, draw edges to centre as they set.");
                step(r, 3, "Add filling to one half when just set. Fold and serve immediately.");
            }
        }

        if (!recipeRepo.existsByName("Chickpea Curry") && asian != null) {
            Ingredient cp = ing("Chickpeas");     Ingredient ts = ing("Tomato Sauce");
            Ingredient on = ing("Onion");         Ingredient ga = ing("Garlic");
            Ingredient cu = ing("Cumin");         Ingredient pa = ing("Paprika");
            Ingredient oo = ing("Olive Oil");     Ingredient sa = ing("Salt");
            if (cp != null) {
                Recipe r = recipe("Chickpea Curry", "Fragrant golden chickpeas in a rich spiced sauce",
                    10, 25, 4, Difficulty.EASY, asian, chef,
                    "Weeknight vegan — warming, hearty, packed with protein", 14.0, 46.0, 8.0, 10.0);
                recipeRepo.save(r);
                if (cp != null) ri(r, cp, 400.0, "g"); if (ts != null) ri(r, ts, 400.0, "g");
                if (on != null) ri(r, on, 150.0, "g"); if (ga != null) ri(r, ga, 4.0, "pcs");
                if (cu != null) ri(r, cu, 5.0, "g");   if (pa != null) ri(r, pa, 5.0, "g");
                if (oo != null) ri(r, oo, 20.0, "g");  if (sa != null) ri(r, sa, 5.0, "g");
                step(r, 1, "Sauté onion in oil until golden, about 10 minutes.");
                step(r, 2, "Add garlic and spices. Stir 1 minute.");
                step(r, 3, "Add chickpeas and tomato sauce. Season well.");
                step(r, 4, "Simmer 20 minutes until sauce thickens. Serve with rice.");
            }
        }
    }

    private Category cat(String name, String desc) {
        Category c = new Category();
        c.setName(name); c.setDescription(desc);
        return c;
    }

    private Ingredient ing(String name, String desc, String unit, Double cal,
                           Double protein, Double carbs, Double fat, Double fiber,
                           IngredientCategory cat, boolean gluten) {
        Ingredient i = new Ingredient();
        i.setName(name); i.setDescription(desc); i.setDefaultUnit(unit);
        i.setCalories(cal); i.setProtein(protein); i.setCarbs(carbs);
        i.setFat(fat); i.setFiber(fiber);
        i.setCategory(cat); i.setContainsGluten(gluten);
        return i;
    }

    private Ingredient ing(String name) {
        return ingredientRepo.findByName(name).orElse(null);
    }

    private Recipe recipe(String name, String desc, int prep, int cook, int servings,
                          Difficulty diff, Category cat, User user, String blurb,
                          Double protein, Double carbs, Double fat, Double fiber) {
        Recipe r = new Recipe();
        r.setName(name); r.setDescription(desc);
        r.setPrepTime(prep); r.setCookTime(cook); r.setServings(servings);
        r.setDifficulty(diff); r.setCategory(cat); r.setUser(user);
        r.setBlurb(blurb); r.setProtein(protein); r.setCarbs(carbs);
        r.setFat(fat); r.setFiber(fiber);
        return r;
    }

    private void ri(Recipe recipe, Ingredient ingredient, Double qty, String unit) {
        if (ingredient == null) return;
        RecipeIngredient ri = new RecipeIngredient();
        ri.setRecipe(recipe); ri.setIngredient(ingredient);
        ri.setQuantity(qty); ri.setUnit(unit);
        riRepo.save(ri);
    }

    private void step(Recipe recipe, int num, String desc) {
        RecipeStep s = new RecipeStep();
        s.setRecipe(recipe); s.setStepNumber(num); s.setDescription(desc);
        stepRepo.save(s);
    }

    private void addCatIfMissing(String name, String desc) {
        if (categoryRepo.findByName(name).isEmpty()) {
            categoryRepo.save(cat(name, desc));
        }
    }

    private void addIngIfMissing(String name, String desc, String unit, Double cal,
                                  Double protein, Double carbs, Double fat, Double fiber,
                                  IngredientCategory cat, boolean gluten) {
        if (ingredientRepo.findByName(name).isEmpty()) {
            ingredientRepo.save(ing(name, desc, unit, cal, protein, carbs, fat, fiber, cat, gluten));
        }
    }
}
