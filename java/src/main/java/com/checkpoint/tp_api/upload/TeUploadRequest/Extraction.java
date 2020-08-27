package com.checkpoint.tp_api.upload.TeUploadRequest;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by edanha on 4/18/2017.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Extraction {
    private String method;
    private List<String> scrubbed_parts_codes;

    public Extraction(String method) {
        this.method = method;
    }

    public Extraction(String method, List<String> scrubbed_parts_codes) {
        this.method = method;
        this.scrubbed_parts_codes = scrubbed_parts_codes;
    }

    public List<String> getScrubbed_parts_codes() {
        return scrubbed_parts_codes;
    }

    public void setScrubbed_parts_codes(List<String> scrubbed_parts_codes) {
        this.scrubbed_parts_codes = scrubbed_parts_codes;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
