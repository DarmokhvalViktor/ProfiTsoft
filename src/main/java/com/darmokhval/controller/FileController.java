package com.darmokhval.controller;

import com.darmokhval.utility.ArgsValidator;
import com.darmokhval.utility.FileParser;
import com.darmokhval.utility.FileWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * class that control parse and write to file
 */
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileParser fileParser;
    private final FileWriter fileWriter;
    private final ArgsValidator argsValidator;

    public void parse(String... args) {
        argsValidator.checkForCorrectArgs(args);
        Map<String, Integer> countFieldMap = parseFiles(args);
        fileWriter.generateXML(countFieldMap, args[1]);
    }

    private Map<String, Integer> parseFiles(String... args) {
        System.out.println("parsing all files");
        long start = System.currentTimeMillis();
        Map<String, Integer> resultingMapWithFields = new ConcurrentHashMap<>();
        File[] files = new File(args[0]).listFiles();

        if (files != null) {
            System.out.println(Runtime.getRuntime().availableProcessors() + "available processors");
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (File file : files) {
                executor.submit(() -> fileParser.parseFile(file, args[1], resultingMapWithFields));
            }
            executor.shutdown();
            try {
                if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                    log.info("Executor service did not terminate within the specified timeout");
                }
            } catch (InterruptedException e) {
                log.error("Thread interrupted while awaiting termination of executor service", e);
                Thread.currentThread().interrupt();
            } finally {
                System.out.println("Files were successfully parsed!");
                System.out.println(System.currentTimeMillis() - start + " : time in millis to parse all files");
            }
        }
        return resultingMapWithFields;
    }
}
