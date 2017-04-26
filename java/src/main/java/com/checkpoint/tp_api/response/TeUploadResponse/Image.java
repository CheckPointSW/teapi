package com.checkpoint.tp_api.response.TeUploadResponse;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by edanha on 4/4/2017.
 */
@SuppressWarnings("unused")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Image {
    private Report report;
    private String status;
    private String id;
    private int revision;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
