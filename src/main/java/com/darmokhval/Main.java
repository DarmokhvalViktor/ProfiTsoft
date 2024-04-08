package com.darmokhval;

import com.darmokhval.controller.FileController;
import com.darmokhval.utility.FileParser;
import com.darmokhval.utility.FileRemover;
import com.darmokhval.utility.FileWriter;
import com.darmokhval.utility.RandomFilesWithCarsGenerator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    public static void main(String[] args) {
        FileController fileController = new FileController(new FileParser(), new FileWriter());


        AtomicLong counter = new AtomicLong(0);
        Runnable runnable = () -> System.out.println("tik" + counter.getAndIncrement());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        try {
            executor.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
//            FileRemover remover = new FileRemover();
//            remover.deleteJsonFiles(args[0]); //deletes all .json files in specified directory. test use only.
            RandomFilesWithCarsGenerator carGenerator = new RandomFilesWithCarsGenerator();
//            carGenerator.createFiles(500); //creates a number of .json files in predefined directory. test use.
            fileController.parseFiles(args);
        } catch (IllegalArgumentException e) {
            System.err.println("An error has occurred: " + e.getMessage());
        } finally {
            System.out.println("Shutting down the executor");
            executor.shutdown();
        }

        System.out.println("Program has completed its execution");
    }
}