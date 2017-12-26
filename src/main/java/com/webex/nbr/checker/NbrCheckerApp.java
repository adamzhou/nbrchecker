package com.webex.nbr.checker;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * NbrChecker is a tool to check if NBR recording data is correct.
 *
 * The version v1 supports camera duration check.
 *
 * Created by linzhou on 15/12/2017.
 */
public class NbrCheckerApp {

    private static void help() {
        String usage = "Usage: java -jar webex-nbr-checker.jar [Raw Data List File] [Result File]\n";
        System.out.println(usage);
    }

    private static void version() {
        System.out.println("WebEx NBR Checker Tool Version 1.0.0.20171215\n");
    }

    public static void main(String[] args) {

        version();

        if (args.length < 2 || StringUtils.isBlank(args[0]) || StringUtils.isBlank(args[1])) {
            help();
            return;
        }

        String rawDataListFile = args[0];
        String resultFile = args[1];

        List<String> rawDataPathList = getRawDataPathList(rawDataListFile);
        if (rawDataPathList == null || rawDataPathList.isEmpty()) {
            System.out.println("Could not get any data from " + rawDataListFile);
            return;
        }

        /*if (!checkResultFile(resultFile)) {
            System.out.println(resultFile + "could not be created!");
            return;
        }*/

        NbrChecker checker = new NbrChecker(rawDataPathList);
        NbrCheckResult result = checker.check();
        SaveResult(result, resultFile);
    }

    private static void SaveResult(NbrCheckResult result, String resultFile) {
        File file = new File(resultFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(result.toString());
        } catch (IOException e) {
            System.out.println("Failed to write check result to file! e=" + e.getMessage());
        }
    }

    private static boolean checkResultFile(String resultFile) {
        File file = new File(resultFile);
        return file.canWrite();
    }

    private static List<String> getRawDataPathList(String rawDataListFile) {

        List<String> rawDataPathList = new ArrayList<>();
        try {
            File f = new File(rawDataListFile);
            BufferedReader br = new BufferedReader(new FileReader(f));
            String readLine = "";
            while ((readLine = br.readLine()) != null) {
                String path = filterComment(readLine);
                if (StringUtils.isNotBlank(path)) {
                    rawDataPathList.add(readLine);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to getRawDataPathList! e=" + e.getMessage());
        }

        return rawDataPathList;
    }

    /**
     * Filter the line with "#" head
     *
     * @param line
     * @return
     */
    private static String filterComment(String line) {
        if (StringUtils.isNotBlank(line) && line.startsWith("#")) {
            return null;
        } else {
            return line;
        }
    }
}
