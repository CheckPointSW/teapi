package com.checkpoint.tp_api.response.TeUploadResponse;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by edanha on 4/4/2017.
 */
@SuppressWarnings("unused")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Report {
    private String verdict;
    private String xml_report;
    private String pdf_report;

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public String getXml_report() {
        return xml_report;
    }

    public void setXml_report(String xml_report) {
        this.xml_report = xml_report;
    }

    public String getPdf_report() {
        return pdf_report;
    }

    public void setPdf_report(String pdf_report) {
        this.pdf_report = pdf_report;
    }
}
