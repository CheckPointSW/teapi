package com.checkpoint.tp_api.dir;

import com.checkpoint.tp_api.upload.TeHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by edanha on 4/4/2017.
 */
public class TeQueryBucket {
    private List<FileToQuery> filesToQuery;
    private TeHttpClient teHttpClient;

    public TeQueryBucket(List<FileToQuery> filesToQuery, Map<String, Object> argMap) throws Exception{
        this.filesToQuery = filesToQuery;
        teHttpClient = new TeHttpClient(argMap);
    }

    public List<FileToQuery> getFilesToQueryList() {
        return filesToQuery;
    }

    public TeHttpClient getTeHttpClient() {
        return teHttpClient;
    }

    public boolean isEmpty() {
        return filesToQuery.isEmpty();
    }

    public int size() { return filesToQuery.size(); }

    public List<String> getMd5s() {
        List<String> md5s = new ArrayList<>();
        for (FileToQuery file : filesToQuery) {
            md5s.add(file.getMd5());
        }
        return md5s;
    }

    @SuppressWarnings("unused")
    public List<String> getSha1s() {
        List<String> sha1s = new ArrayList<>();
        for (FileToQuery file : filesToQuery) {
            sha1s.add(file.getSha1());
        }
        return sha1s;
    }

    public File getFileByMD5(String md5ToSearch) throws FileNotFoundException{
        for (FileToQuery file : filesToQuery) {
            if (file.getMd5().equals(md5ToSearch)) {
                return file.getFile();
            }
        }
        throw new FileNotFoundException();
    }

    public void removeFileByMD5(String md5ToRemove) throws FileNotFoundException{
        filesToQuery = filesToQuery.stream().filter(fileToQuery -> !fileToQuery.getMd5().equals(md5ToRemove))
                .collect(Collectors.toList());
    }
}

