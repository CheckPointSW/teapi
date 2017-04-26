package com.checkpoint.tp_api.query;

import com.checkpoint.tp_api.dir.FileToQuery;
import com.checkpoint.tp_api.upload.TeUploadRequest.Request;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edanha on 3/26/2017.
 */
@SuppressWarnings("unused")
public class TeQueryRequest {
    private List<Request> request;

    public TeQueryRequest(List<FileToQuery> fileToQueryList, List<String> reports, List<String> features, String method) {
        request = new ArrayList<>();
        for (FileToQuery fileToQuery : fileToQueryList) {
            request.add(new Request(FilenameUtils.getExtension(fileToQuery.getFile().getName()),
                    fileToQuery.getFile().getName(), reports, features, fileToQuery.getMd5(), method));
        }
    }

    public TeQueryRequest(List<FileToQuery> fileToQueryList, List<String> reports, List<String> features) {
        request = new ArrayList<>();
        for (FileToQuery fileToQuery : fileToQueryList) {
            request.add(new Request(FilenameUtils.getExtension(fileToQuery.getFile().getName()),
                    fileToQuery.getFile().getName(), reports, features, fileToQuery.getMd5()));
        }
    }

    public List<Request> getRequest() {
        return request;
    }

    public void setRequest(List<Request> request) {
        this.request = request;
    }
}