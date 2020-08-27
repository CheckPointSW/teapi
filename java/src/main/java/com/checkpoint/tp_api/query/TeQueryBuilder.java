package com.checkpoint.tp_api.query;

import com.checkpoint.tp_api.cli.TeQueryCLI;
import com.checkpoint.tp_api.dir.DirScanner;
import com.checkpoint.tp_api.dir.FileToQuery;
import com.checkpoint.tp_api.dir.TeQueryBucket;
import com.checkpoint.tp_api.logger.TeLogger;
import com.checkpoint.tp_api.response.TeBucketHandler;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by edanha on 3/26/2017.
 */

public class TeQueryBuilder {
    private List<TeQueryBucket> teQueryBuckets;
    private Map<String, Object> argMap;
    private static Logger logger;
    private int numberOfFiles;

    private final static int MAX_FILES_PER_BUCKET = 500;
    private final static int TIME_TO_SLEEP = 30;

    public TeQueryBuilder parseArgs(String[] args) throws Exception{
        argMap = new TeQueryCLI().parse(args); //argument map from CLI
        return this;
    }

    public TeQueryBuilder build() throws Exception{
        logger = TeLogger.init(TeQueryBuilder.class, argMap);
        splitIntoBuckets(new DirScanner(argMap).getFilesToQuery()); //Splits into buckets
        return this;
    }

    public TeQueryBuilder execute() throws Exception {
        logger.debug(teQueryBuckets.size() + " buckets was created");
        //Iterates through all buckets, query&upload for each bucket on turns
        while(!teQueryBuckets.isEmpty()) {
            for (TeQueryBucket teQueryBucket : teQueryBuckets) {
                new TeBucketHandler(argMap).handleFilesBucket(teQueryBucket);
                if (teQueryBucket.isEmpty()) { //Filtering out finished buckets
                    teQueryBuckets = teQueryBuckets.stream().filter(teQueryBucketToCheck -> !teQueryBucketToCheck.isEmpty())
                            .collect(Collectors.toList());
                }
            }
            sleepBeforeNextTime();
        }
        logger.info("Done with all files");
        return this;
    }

    private void sleepBeforeNextTime() throws InterruptedException {
        if (!teQueryBuckets.isEmpty()) {
            //Sleeping + how many files left
            logger.info(teQueryBuckets.stream().mapToInt(TeQueryBucket::size).sum() + "/" + numberOfFiles + " pending...");
            logger.info("Sleeping for... " + TIME_TO_SLEEP + " seconds");
            TimeUnit.SECONDS.sleep(TIME_TO_SLEEP);
        }
    }

    private void splitIntoBuckets(List<FileToQuery> fileToQueryList) throws Exception{
        numberOfFiles  = fileToQueryList.size();
        int numberOfBuckets = numberOfFiles/MAX_FILES_PER_BUCKET + 1;

        teQueryBuckets = new ArrayList<>();
        for (int i = 0; i < numberOfBuckets; i++) { //Splitting into buckets
            teQueryBuckets.add(new TeQueryBucket(
                    fileToQueryList.subList(i*MAX_FILES_PER_BUCKET,
                            Math.min((i+1)*MAX_FILES_PER_BUCKET, numberOfFiles)), argMap));
        }
    }

    @SuppressWarnings("unused")
    public TeQueryBuilder setArgMap(String ScanDirectory, String ReportsDirectory, String APIKey, boolean debug,
                                    boolean pdfReports, boolean xmlReports, boolean summaryReports,
                                    boolean recursive, String host, int port) {
        //In case you don't want to use parser
        setArgMap(ScanDirectory, ReportsDirectory, APIKey, debug, pdfReports, xmlReports, summaryReports, recursive);
        argMap.put("withProxy", true);
        argMap.put("host", host);
        argMap.put("port", port);
        return this;
    }

    public TeQueryBuilder setArgMap(String ScanDirectory, String ReportsDirectory, String APIKey, boolean debug,
                                    boolean pdfReports, boolean xmlReports, boolean summaryReports,
                                    boolean recursive) {
        //In case you don't want to use parser
        argMap = new HashMap<>();
        argMap.put("D", ScanDirectory);
        argMap.put("R", ReportsDirectory);
        argMap.put("K", APIKey);
        argMap.put("d", debug);
        argMap.put("p", pdfReports);
        argMap.put("x", xmlReports);
        argMap.put("s", summaryReports);
        argMap.put("r", recursive);
        return this;
    }
}
