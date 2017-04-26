package com.checkpoint.tp_api.dir;

import com.checkpoint.tp_api.logger.TeLogger;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Created by edanha on 3/22/2017.
 */

public class DirScanner {
    private List<FileToQuery> filesToQuery;
    private static Logger logger;

    // scanDirPath = absolute path to file
    public DirScanner(Map<String, Object> argMap) throws Exception{
        logger = TeLogger.init(DirScanner.class, argMap);
        filesToQuery = new ArrayList<>();
        logger.info("Scanning files");
        Collection<File> listOfFiles;
        if ((boolean) argMap.get("r")) {
            //Searching for files in the folder recursively
            listOfFiles = FileUtils.listFiles(new File((String) argMap.get("D")),
                    TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        }
        else {
            //None-recursive
            listOfFiles = FileUtils.listFiles(new File((String) argMap.get("D")), TrueFileFilter.INSTANCE, null);
        }
        for (File file : listOfFiles) {
            try (FileInputStream fileInputStream = FileUtils.openInputStream(file)) {
                filesToQuery.add(new FileToQuery(DigestUtils.md5Hex(fileInputStream),
                        DigestUtils.sha1Hex(fileInputStream), file));
            }
        }
        HashSet<String> seen = new HashSet<>();
        filesToQuery.removeIf(file -> !seen.add(file.getMd5())); //Removing duplicates
        logger.info("Scanned " + filesToQuery.size() + " files");
    }

    public List<FileToQuery> getFilesToQuery() {
        return filesToQuery;
    }


}

