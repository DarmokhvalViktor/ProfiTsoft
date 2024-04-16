package com.darmokhval;

import com.darmokhval.controller.FileController;
import com.darmokhval.utility.*;

public class Main {

    public static void main(String[] args) {
        FileController fileController = new FileController(
                new FileParser(),
                new FileWriter(),
                new ArgsValidator()
        );

//        FileRemover remover = new FileRemover();
//        remover.deleteJsonFiles(args[0]); //deletes all .json files in specified directory.
//        RandomFilesWithCarsGenerator carGenerator = new RandomFilesWithCarsGenerator();
//        carGenerator.createFiles(1, 7); //creates a number of .json files in predefined directory.

        try {
            fileController.parse(args);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e);
        }
    }
}