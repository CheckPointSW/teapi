package com.checkpoint.tp_api.cli;

import org.apache.commons.cli.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by edanha on 4/9/2017.
 */
public class TeQueryCLI {

    private CommandLine cmd;
    private Options options;
    private Option helpOption;
    private Map<String, Object> argMap;

    public final String ILLEGAL_REQUEST_SUMMARY_AND_PDF_ERROR_MESSAGE = "Illegal request. Pdf reports are not available in the new Threat Emulation reports format. Requesting for pdf and summary reports simultaneously is not supported.";

    public TeQueryCLI() {
        options = new Options();
        argMap = new HashMap<>();

        OptionBuilder.withDescription("Show help message");
        OptionBuilder.withLongOpt("help");
        options.addOption(helpOption = OptionBuilder.create("h"));

        OptionBuilder.withArgName("scanning directory");
        OptionBuilder.withLongOpt("directory");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The scanning directory");
        OptionBuilder.isRequired();
        options.addOption(OptionBuilder.create("D"));

        OptionBuilder.withArgName("reports directory");
        OptionBuilder.withLongOpt("reports");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("A folder to download the reports to");
        OptionBuilder.isRequired();
        options.addOption(OptionBuilder.create("R"));

        OptionBuilder.withArgName("API Key");
        OptionBuilder.withLongOpt("key");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("API Key");
        OptionBuilder.isRequired();
        options.addOption(OptionBuilder.create("K"));

        OptionBuilder.withDescription("Add debugging");
        OptionBuilder.withLongOpt("debug");
        OptionBuilder.hasOptionalArg();
        OptionBuilder.withArgName("Path to .log file");
        options.addOption(OptionBuilder.create("d"));

        OptionBuilder.withDescription("Download PDF reports");
        OptionBuilder.withLongOpt("pdf");
        options.addOption(OptionBuilder.create("p"));

        OptionBuilder.withDescription("Download XML reports");
        OptionBuilder.withLongOpt("xml");
        options.addOption(OptionBuilder.create("x"));

        OptionBuilder.withDescription("Download summary reports");
        OptionBuilder.withLongOpt("summary");
        options.addOption(OptionBuilder.create("s"));

        OptionBuilder.withDescription("Emulate the files in the directory recursively");
        OptionBuilder.withLongOpt("recursive");
        options.addOption(OptionBuilder.create("r"));

        OptionBuilder.withDescription("Proxy Settings");
        OptionBuilder.withArgName("host, port");
        OptionBuilder.withLongOpt("proxy");
        OptionBuilder.hasArgs(2);
        options.addOption(OptionBuilder.create("pr"));

        OptionBuilder.withDescription("Activate Threat Extraction (supported only with cloud);" +
                " Scrubbing method. Convert to PDF / CleanContent");
        OptionBuilder.withArgName("method('convert'/'clean'), clean files path");
        OptionBuilder.withLongOpt("extraction");
        OptionBuilder.hasArgs(2);
        options.addOption(OptionBuilder.create("tex"));
    }

    private boolean validateOptions(String[] args) throws Exception {
        CommandLineParser parser = new BasicParser();
        try {
            cmd = parser.parse(new Options().addOption(helpOption), args);
            if (cmd.hasOption("h") || args.length == 0) {
                printHelp(); //just help
                System.exit(0);
            }
        } catch (ParseException e) {
            cmd = parser.parse(options, args);
        }

        File file = new File(cmd.getOptionValue("D")); //Checking for valid directory
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("No such directory as " + cmd.getOptionValue("D"));
        }
        file = new File(cmd.getOptionValue("R"));
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("No such directory as " + cmd.getOptionValue("R"));
        }

        if (cmd.hasOption("s") && cmd.hasOption("p")) {
            throw new IllegalArgumentException(ILLEGAL_REQUEST_SUMMARY_AND_PDF_ERROR_MESSAGE);
        }

        return true;
    }

    private void printHelp() {
        new HelpFormatter().printHelp(200,"java " + this.getClass().getName(), "", options, "\n");
    }

    //for boolean flags
    private void putFlag(String option) {
        if (cmd.hasOption(option)) {
            argMap.put(option, true);
        } else {
            argMap.put(option, false);
        }
    }

    public Map<String, Object> parse(String[] args) throws Exception {
        try {
            if (validateOptions(args)) {
                argMap.put("D", cmd.getOptionValue("D"));
                argMap.put("R", cmd.getOptionValue("R"));
                argMap.put("K", cmd.getOptionValue("K"));
                if (cmd.hasOption("d")) {
                    argMap.put("debugFileName", cmd.getOptionValue("d"));
                }
                putFlag("d");
                putFlag("p");
                putFlag("x");
                putFlag("s");
                putFlag("r");
                if (cmd.hasOption("pr")) {
                    argMap.put("withProxy", true);
                    argMap.put("host", cmd.getOptionValues("pr")[0]);
                    argMap.put("port", cmd.getOptionValues("pr")[1]);
                } else {
                    argMap.put("withProxy", false);
                }
                if (cmd.hasOption("tex")) {
                    argMap.put("withTex", true);
                    argMap.put("method", cmd.getOptionValues("tex")[0]);
                    argMap.put("tex_folder", cmd.getOptionValues("tex")[1]);
                }
                else {
                    argMap.put("withTex", false);
                }
            }
        } catch (ParseException e) {
            printHelp();
            throw e;
        }
        return argMap;
    }
}
