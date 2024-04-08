package com.darmokhval.utility;

import com.darmokhval.model.CarAccessories;
import com.darmokhval.model.Owner;
import com.darmokhval.model.CarBrand;
import com.darmokhval.model.CarModel;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to generate files to test if the program works.
 * methods starts with generateRandom... -> utility methods to generates random values to fill in file.
 * main method -> createFile, final values can be changed to test different conditions.
 * it takes about 54 seconds to create 5 files with 2_000_000 objects(at least on my PC) (all files weigh 373+mb)
 * it took about 42-38 seconds to create 7 files with 1_000_000 objects. files weigh -> 186mb.
 * it took 118 seconds to create 1 file with 20_000_000 objects (file weight 3.73gb)
 * 5 seconds to create single file with 1_000_000 objects (weight 198mb).
 * all of that above with 1 single thread.
 * with 7 threads code took 28-24 seconds to create 7 files. seems like disc I/O operations limits my code to be executed at some speed.
 * 294 seconds for 7 threads to create 7 files, each 3.79gb weigh.
 * method .createFiles() writes line-by-line, so it shouldn't consume too much RAM.
 */
public class RandomFilesWithCarsGenerator {
    private static final Random random = new Random();
    private static final Logger logger = Logger.getLogger("RandomGenerationLogger");
    private final static int numberOfObjectsToCreate = 5_000;
    private static final int minYear = 1990;
    private static final int maxYear = 2024;
    private static final String pathToSaveFile = "src/main/resources/large_car_data_";
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String[] famousPeople = {
            "Albert Einstein", "Marie Curie", "Isaac Newton", "Galileo Galilei",
            "Nikola Tesla", "Stephen Hawking", "Ada Lovelace", "Alan Turing", "Bill Gates", "Steve Jobs",
            "Elon Musk", "Mark Zuckerberg", "Oprah Winfrey", "Ellen DeGeneres", "David Beckham", "Cristiano Ronaldo",
            "Serena Williams", "Michael Jordan", "Kobe Bryant", "Usain Bolt", "Roger Federer", "Lionel Messi",
            "Tom Brady", "Muhammad Ali", "Bruce Lee", "Pablo Picasso", "Frida Kahlo",
            "Vincent Price", "Marilyn Monroe", "Audrey Hepburn", "James Dean", "Elvis Presley", "John Lennon",
            "Bob Marley", "Freddie Mercury", "Michael Jackson", "Whitney Houston",
            "Taylor Swift", "Kanye West", "Angelina Jolie", "Brad Pitt", "Jennifer Aniston",
            "Leonardo DiCaprio", "Vincent van Gogh"
    };

    /**
     * one can choose how many files should be created.
     * @param numberOfFiles specify how many files should be created.
     */
    public void createFiles(int numberOfFiles) {
        ExecutorService executorService = Executors.newFixedThreadPool(7);
        System.out.println("Creating files.... ");
        for(int j = 1; j <= numberOfFiles; j++) {
            int finalJ = j;
            executorService.submit(() -> writeFile(finalJ));
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                logger.log(Level.WARNING, "Executor service did not terminate within the specified timeout");
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Thread interrupted while awaiting termination of executor service", e);
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("Files were successfully created!!!");
        }
    }

    /**
     * creates file in defined directory. By default - "src/main/resources". Name "large_car_data_{fileNumber}.json".
     * Writes line by line, so it shouldn't use too much RAM.
     * @param fileNumber int number of file that needs to be written.
     */
    public void writeFile(int fileNumber) {
        System.out.println("Create file %" + fileNumber);
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(pathToSaveFile + fileNumber + ".json")))) {
            for (int i = 0; i < numberOfObjectsToCreate; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("brand", generateRandomBrand());
                jsonObject.put("model", generateRandomModel());
                jsonObject.put("year_of_release", generateRandomYear());
                jsonObject.put("owner", generateRandomOwner());
                jsonObject.put("mileage", generateRandomMileage());
                jsonObject.put("accessories", generateRandomAccessoriesString());
                jsonObject.put("was_in_accident", generateRandomAccident());
                writer.println(jsonObject);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while writing to file", e);
        }
    }

    /**
     * this method returns "true" in 25% of time. Using to decide if car was in accident.
     * @return random boolean with 75% change it to be "false".
     */
    private boolean generateRandomAccident() {
        int randomNumber = random.nextInt(100);
        return randomNumber >= 75;
    }

    /**
     * generate random number of accessories, minimum at 2, maximum -> 6
     * @return random number of accessories (in range from 2 to 6), grouped in one string divided by comma ","
     */
    private String generateRandomAccessoriesString() {
        int numberOfAccessories = random.nextInt(5) + 2;
        StringBuilder accessoriesBuilder = new StringBuilder();
        for (int i = 0; i < numberOfAccessories; i++) {
            CarAccessories accessory = CarAccessories.values()[random.nextInt(CarAccessories.values().length)];
            accessoriesBuilder.append(accessory.getAccessory());
            if(i < numberOfAccessories - 1) {
                accessoriesBuilder.append(", ");
            }
        }
        return accessoriesBuilder.toString();
    }

    private int generateRandomMileage() {
        return (int) (Math.random() * (200_000 - 50 + 1));
    }

    /**
     * get random string from an array defined in memory, and creates an owner object.
     * if in that array one occurrence not exactly 2 words, method will create some random name + surname for owner object.
     * @return string with owner name and lastname from predefined array
     */
    private String generateRandomOwner() {
        String[] ownerFromArray = famousPeople[random.nextInt(famousPeople.length)].split(" ");
        Owner owner;
        if(ownerFromArray.length == 2) {
            owner = new Owner(ownerFromArray[0], ownerFromArray[1]);
        } else {
            owner = new Owner("randomName" + characters.charAt(random.nextInt(characters.length())),
                    "randomLastname" + characters.charAt(random.nextInt(characters.length())));
        }
        return owner.getName() + " " + owner.getLastname();
    }

    private CarBrand generateRandomBrand() {
        return CarBrand.values()[random.nextInt(CarBrand.values().length)];
    }
    private CarModel generateRandomModel() {
        return CarModel.values()[random.nextInt(CarModel.values().length)];
    }

    /**
     * return random year between minYear and maxYear. 50% chance to return between maxYear and 2019. Using it to assign to carYear
     */
    private int generateRandomYear() {
        int randomNumber = random.nextInt(100);
        if(randomNumber < 50) {
            return 2019 + (int) (Math.random() * (maxYear - 2019 + 1));
        } else {
            return minYear + (int) (Math.random() * (maxYear - minYear + 1));
        }
    }

}
