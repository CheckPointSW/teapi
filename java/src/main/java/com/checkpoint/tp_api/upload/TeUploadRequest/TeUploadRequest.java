package com.checkpoint.tp_api.upload.TeUploadRequest;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edanha on 3/28/2017.
 */
@SuppressWarnings("unused")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class TeUploadRequest {
    public List<Request> request;

    public TeUploadRequest(String file_type, String file_name, List<String> reports, List<String> features, String md5, String method) {
        request = new ArrayList<>();
        request.add(new Request(file_type, file_name, reports, features, md5, method));
    }

    public TeUploadRequest(String file_type, String file_name, List<String> reports, List<String> features, String md5) {
        request = new ArrayList<>();
        request.add(new Request(file_type, file_name, reports, features, md5));
    }

    public List<Request> getRequest() {
        return request;
    }

    public void setRequest(List<Request> request) {
        this.request = request;
    }

}

