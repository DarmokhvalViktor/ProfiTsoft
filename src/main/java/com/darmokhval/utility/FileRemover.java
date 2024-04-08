package com.darmokhval.utility;

import java.io.File;

public class FileRemover {


    /**
     * method that deletes .json files in passed directory
     * @param path directory to delete files
     */
    public void deleteJsonFiles(String path) {
        File[] files = new File(path).listFiles();
        if(files != null) {
            for(File file: files) {
                if(file.isFile() && file.getName().endsWith(".json")) {
                    boolean deleted = file.delete();
                    if(deleted) {
                        System.out.println("Deleted file: " + file.getName());
                    } else {
                        System.out.println("Failed to delete file: " + file.getName());
                    }
                }
            }
        }
    }
}
