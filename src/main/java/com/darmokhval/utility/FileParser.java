package com.darmokhval.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 284 seconds for 7 thread to parse 7 files each 3.79 gb
 * 5.3 seconds for 1 thread to parse 500 files each 1mb.
 * 3.5 seconds for 2 threads to parse 500 files each 1mb.
 * 2.4 seconds for 4 threads to parse 500 files each 1mb.
 * 2 seconds for 12 threads to parse 500 files each 1mb.
 */
public class FileParser {
    private static final Set<String> VALID_FIELDS = new HashSet<>();
    private static final Logger logger = Logger.getLogger("File parser logger");
    static {
        VALID_FIELDS.add("brand");
        VALID_FIELDS.add("model");
        VALID_FIELDS.add("year_of_release");
        VALID_FIELDS.add("owner");
        VALID_FIELDS.add("mileage");
        VALID_FIELDS.add("accessories");
        VALID_FIELDS.add("was_in_accident");
    }

    /**
     * iterates over files passed by path, creates new thread for each file present.
     * method should use executor.awaitTermination to
     * @param args expects 1st param as directory path and 2nd param as searched field.
     * @return map by searched field.
     */
    public Map<String, Integer> parseFiles(String ...args) {
//        System.out.println("parsing all files");
        long start = System.currentTimeMillis();
        Map<String, Integer> resultingMapWithFields = new ConcurrentHashMap<>();
        File[] files = null;
        if(checkForCorrectArgs(args)) {
            files = new File(args[0]).listFiles();
        }

        if(files != null) {
            System.out.println(Runtime.getRuntime().availableProcessors() + "available processors?");
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//            ExecutorService executor = Executors.newFixedThreadPool(4);
            for(File file: files){
                executor.submit(() -> parseFile(file, args[1], resultingMapWithFields));
            }
            executor.shutdown();
            try {
                if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                    logger.log(Level.WARNING, "Executor service did not terminate within the specified timeout");
                }
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Thread interrupted while awaiting termination of executor service", e);
                Thread.currentThread().interrupt();
            } finally {
                System.out.println("Files were successfully parsed!");
                System.out.println(System.currentTimeMillis() - start + " : time in millis to parse all files");
            }
        }
        return resultingMapWithFields;
    }

    /**
     * iterates over one file in chunks to not overuse RAM and writes to map as passed args searched values.
     * @param file iterated file
     * @param searchedField searched field
     * @param countFieldMap map where result will be stored
     */
    private void parseFile(File file, String searchedField, Map<String, Integer> countFieldMap) {
//        System.out.println("parsing file... "+ file.getName());
        if (checkFileIfJson(file)) {
//            System.out.println("Parsing file that is 100% json %" + file.getName());
            ObjectMapper mapper = new ObjectMapper();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while((line = reader.readLine()) != null) {
                    JsonNode rootNode = mapper.readTree(line);
                    String fieldValue = rootNode.path(searchedField).asText();
                    String[] fieldValues = fieldValue.split(",\\s*");
                    for (String value: fieldValues) {
                        if (!searchedField.equals(value.trim())) {
                            countFieldMap.merge(value.trim(), 1, Integer::sum);
                        }
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An error occurred while reading from file", e);
            }
        }
    }

    /**
     * method to check if the arguments passed to method are valid, if not - throws error.
     * @param args -> two arguments that should be passed inside, path to directory and field to search in file.
     */
    private boolean checkForCorrectArgs(String ...args){
        if(args == null || args.length != 2) {
            throw new IllegalArgumentException("Number of arguments are not supported");
        }

        String directoryPath = args[0];
        String fieldAttribute = args[1];

        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path.");
        }
        File[] files = directory.listFiles();
        if(files == null || files.length == 0) {
            throw new IllegalArgumentException("There are no files inside of a specified directory");
        }
        if(fieldAttribute.isBlank()) {
            throw new IllegalArgumentException("Field attribute can't be blank");
        }
        if(!isValidField(fieldAttribute)) {
            throw new IllegalArgumentException("Field attribute not supported");
        }
        return true;
    }

    private boolean isValidField(String attribute) {
        return VALID_FIELDS.contains(attribute);
    }

    /**
     * checks if file exactly in .json format
     * @param file file to check
     * @return false if file is not json
     */
    private boolean checkFileIfJson(File file) {
        return file != null && file.getName().endsWith(".json");
    }
}
