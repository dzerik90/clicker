package ClickerBot;

import ClickerBot.Bots.ClickerBot;
import ClickerBot.Bots.SunflowerLandBot;
import ClickerBot.Bots.WombatBot;
import ClickerBot.Config.SunflowerLandConfig;
import ClickerBot.Config.WombatConfig;
import ClickerBot.DTO.FarmData;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CropsCronSunflowerLandMain {

    public static void main(String[] args) {
        System.out.println("----- Sunflower Land Clicker ------");
        clickerBot = new ClickerBot();
        System.out.println("Program uruchomi się za 2 sekundy.");
        clickerBot.sleepM(500);

        SunflowerLandConfig config = new SunflowerLandConfig();
        SunflowerLandBot bot = new SunflowerLandBot(config, clickerBot);

        WombatConfig wombatConfig = new WombatConfig();
        WombatBot wombatBot = new WombatBot(wombatConfig, clickerBot);

        Map<String, Integer> mealsCount = new HashMap<>();
        mealsCount.put("Mashed Potato", 0);
        mealsCount.put("Pumpkin Soup", 0);
        mealsCount.put("Bumpkin Broth", 0);

        String[] crops = {
//            "Cauliflower",
//            "Parsnip",
//            "Beetroot",
//            "Cabbage",
//            "Carrot",
//            "Pumpkin",
            "Potato",
//            "Sunflower"
        };

//        for (String crop : crops) {
//            bot.inventory(crop);
//            clickerBot.sleep(3);
//        }
//        clickerBot.sleep(300);

        String[] meals = {
                "Mashed Potato",
                "Pumpkin Soup",
                "Bumpkin Broth"
        };

        Map<String, Integer> mealsTarget = new HashMap<>();
        mealsTarget.put("Mashed Potato", 0);
        mealsTarget.put("Pumpkin Soup", 0);
        mealsTarget.put("Bumpkin Broth", 20);

        Map<Integer, String> cropsQueue = new HashMap<>();
        Map<Integer, String> mealsQueue = new HashMap<>();

        for (int i = 0; i < crops.length; i++) {
            cropsQueue.put(i, crops[i]);
        }

        for (int i = 0; i < meals.length; i++) {
            mealsQueue.put(i, meals[i]);
        }

        boolean farmCrops = true;
        boolean cookMeal = false;
        boolean collectResources = false;
        boolean wombat = false;

        int currentCrop = 0;
        int currentMeal = 0;

        int waitTree = 10 * 60 + 15;
        int waitStone = 10 * 60 + 15;
        int waitIron = 10 * 60 + 15;

//        Date nextCrop = getTimePlusSecond(0);
        Date nextCrop = getTimePlusSecond(0);
        Date nextTree = new Date();
        Date nextStone = new Date();
        Date nextIron = new Date();

        Date nextWombatRun = new Date();
        int waitWombat = 5 * 60;

        Date nextMeal = new Date();
        boolean firstMeal = true;

        while (true) {
            Date currentDate = new Date();

            if  (cookMeal &&
                    mealsQueue.containsKey(currentMeal) &&
                    currentDate.compareTo(nextMeal) >= 0) {

                if (mealsQueue.containsKey(currentMeal) &&
                        mealsCount.get(mealsQueue.get(currentMeal)) >= mealsTarget.get(mealsQueue.get(currentMeal))) {
                    currentMeal++;
                    nextMeal = new Date();
                } else {
                    bot.clickInTab();
                    bot.collectMeal(mealsQueue.get(currentMeal), firstMeal);
                    mealsCount.put(mealsQueue.get(currentMeal), mealsCount.get(mealsQueue.get(currentMeal)) + 1);
                    firstMeal = false;
                    nextMeal = getTimePlusSecond(config.mealsTimes.get(mealsQueue.get(currentMeal)) + 2);
                    System.out.println("Next meal: " + nextMeal.toString());
                }
            }

            if (farmCrops && cropsQueue.containsKey(currentCrop) && currentDate.compareTo(nextCrop) >= 0) {
                bot.clickInTab();
                FarmData farmData = bot.checkFarm();
                bot.inventory(cropsQueue.get(currentCrop));
                nextCrop = getTimePlusSecond(config.cropsTimes.get(cropsQueue.get(currentCrop)) + 5);
                bot.crops(farmData, true);

                if (farmData.seeds.get(cropsQueue.get(currentCrop)) - 43 <= 0) {
                    System.out.println("Chang seed: " + cropsQueue.get(currentCrop));
                    currentCrop++;
                    nextCrop = new Date();
                }
                System.out.println("Next crops: " + nextCrop.toString());
            }

            if (collectResources && currentDate.compareTo(nextTree) >= 0) {
                bot.clickInTab();
                bot.inventory("Axe");
                bot.trees();
                nextTree = getTimePlusSecond(waitTree);
                System.out.println("Next tree: " + nextTree.toString());
            }

            if (collectResources && currentDate.compareTo(nextIron) >= 0) {
                bot.clickInTab();
                bot.inventory("Stone Pickaxe");
                bot.iron();
                nextIron = getTimePlusSecond(waitIron);
                System.out.println("Next iron: " + nextIron.toString());
            }

            if (collectResources && currentDate.compareTo(nextStone) >= 0) {
                bot.clickInTab();
                bot.inventory("Pickaxe");
                bot.stones();
                nextStone = getTimePlusSecond(waitStone);
                System.out.println("Next stone: " + nextStone.toString());
            }

            if (wombat) {
                if (currentDate.compareTo(nextWombatRun) >= 0) {
                    wombatBot.run(60 * 60 * 24);
                    nextWombatRun = getTimePlusSecond(waitWombat);
                    System.out.println("Next wombat: " + nextWombatRun.toString());
                }
            }

            clickerBot.sleep(1);
        }
    }

    private static Date getTimePlusSecond(int seconds) {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    static ClickerBot clickerBot;
}