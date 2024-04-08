package com.darmokhval.controller;

import com.darmokhval.utility.FileParser;
import com.darmokhval.utility.FileRemover;
import com.darmokhval.utility.FileWriter;
import lombok.RequiredArgsConstructor;

import java.util.Map;


/**
 * class controls parse and write to file
 */
@RequiredArgsConstructor
public class FileController {
    private final FileParser fileParser;
    private final FileWriter fileWriter;

    public void parseFiles(String ...args) {
        Map<String, Integer> countFieldMap = fileParser.parseFiles(args);
        fileWriter.generateXML(countFieldMap, args[1]);
    }
}
