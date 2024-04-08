package com.darmokhval.utility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * it takes 145 milliseconds to write info into file
 */
public class FileWriter {
    private final String pathToSaveFile = "src/main/resources/statistics_by_";
    private static final Logger logger = Logger.getLogger("File writer logger");

    /**
     * create write to file statistics from map. if file with that name already exists -> deletes file and creates new
     * @param countFieldMap from where use data to write
     * @param searchedField field that was searched, used to name file
     */
    public void generateXML(Map<String, Integer> countFieldMap, String searchedField) {
        long startWritingTime = System.currentTimeMillis();
//        System.out.println("Start to write into file");
        try {
            if(checkForValues(countFieldMap)) {
                File outputFile = new File(pathToSaveFile + searchedField + ".xml");
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                Element rootElement = doc.createElement("statistic");
                doc.appendChild(rootElement);
                if(outputFile.exists()) {
                    if(outputFile.delete()) {
                        System.out.println("previous statistic file deleted, " + outputFile.getName());
                    }
                }

                countFieldMap.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .forEachOrdered(entry -> {
                            Element item = doc.createElement("item");
                            rootElement.appendChild(item);

                            Element value = doc.createElement("value");
                            value.appendChild(doc.createTextNode(entry.getKey()));
                            item.appendChild(value);

                            Element count = doc.createElement("count");
                            count.appendChild(doc.createTextNode(String.valueOf(entry.getValue())));
                            item.appendChild(count);
                        });

                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(pathToSaveFile + searchedField + ".xml"));
                transformer.transform(source, result);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while reading from file", e);
        } finally {
//            System.out.println("File was successfully created, time in millis: " + (System.currentTimeMillis() - startWritingTime));
        }
    }

    /**
     * check if passed map isn't empty or null
     * @param map to check
     * @return true if map not null or empty
     */
    private boolean checkForValues(Map<String, Integer> map) {
        if(map == null || map.isEmpty()) {
            throw new IllegalArgumentException("Cannot find specified field");
        }
        return true;
    }
}
