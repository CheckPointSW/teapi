package com.checkpoint.tp_api.response.TeUploadResponse;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by edanha on 4/4/2017.
 */
@SuppressWarnings("unused")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private Status status;
    private String md5;
    private String sha1;
    private String sha256;
    private String file_type;
    private String file_name;
    private List<String> features;
    private Te te;
    private Extraction extraction;

    public Extraction getExtraction() {
        return extraction;
    }

    public void setExtraction(Extraction extraction) {
        this.extraction = extraction;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSha1() { return sha1; }

    public void setSha1(String sha1) { this.sha1 = sha1; }

    public String getSha256() { return sha256; }

    public void setSha256(String sha256) { this.sha256 = sha256; }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public Te getTe() {
        return te;
    }

    public void setTe(Te te) {
        this.te = te;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }
}
