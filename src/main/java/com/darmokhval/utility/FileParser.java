package com.darmokhval.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class FileParser {
    /**
     * method iterates over one file in chunks to reduce RAM consumption, and writes result to a map that passed as argument.
     * @param file iterated file
     * @param searchedField searched field from where we need to take value
     * @param countFieldMap map where result will be stored
     */
    public void parseFile(File file, String searchedField, Map<String, Integer> countFieldMap) {
        if (checkFileIfJson(file)) {
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
                log.error("An error occurred while reading from file", e);
            }
        }
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
