package com.checkpoint.tp_api.response.TeUploadResponse;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by edanha on 4/18/2017.
 */
@SuppressWarnings("unused")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ExtractionData {
    private String input_extension;
    private String input_real_extension;
    private String message;
    private String orig_file_url;
    private String output_file_name;
    private String protection_name;
    private String protection_type;
    private double risk;
    private String scrub_activity;
    private String scrub_method;
    private double scrub_result;
    private String scrub_time;
    private String scrubbed_content;

    public String getInput_extension() {
        return input_extension;
    }

    public void setInput_extension(String input_extension) {
        this.input_extension = input_extension;
    }

    public String getInput_real_extension() {
        return input_real_extension;
    }

    public void setInput_real_extension(String input_real_extension) {
        this.input_real_extension = input_real_extension;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrig_file_url() {
        return orig_file_url;
    }

    public void setOrig_file_url(String orig_file_url) {
        this.orig_file_url = orig_file_url;
    }

    public String getOutput_file_name() {
        return output_file_name;
    }

    public void setOutput_file_name(String output_file_name) {
        this.output_file_name = output_file_name;
    }

    public String getProtection_name() {
        return protection_name;
    }

    public void setProtection_name(String protection_name) {
        this.protection_name = protection_name;
    }

    public String getProtection_type() {
        return protection_type;
    }

    public void setProtection_type(String protection_type) {
        this.protection_type = protection_type;
    }

    public double getRisk() {
        return risk;
    }

    public void setRisk(double risk) {
        this.risk = risk;
    }

    public String getScrub_activity() {
        return scrub_activity;
    }

    public void setScrub_activity(String scrub_activity) {
        this.scrub_activity = scrub_activity;
    }

    public String getScrub_method() {
        return scrub_method;
    }

    public void setScrub_method(String scrub_method) {
        this.scrub_method = scrub_method;
    }

    public double getScrub_result() {
        return scrub_result;
    }

    public void setScrub_result(double scrub_result) {
        this.scrub_result = scrub_result;
    }

    public String getScrub_time() {
        return scrub_time;
    }

    public void setScrub_time(String scrub_time) {
        this.scrub_time = scrub_time;
    }

    public String getScrubbed_content() {
        return scrubbed_content;
    }

    public void setScrubbed_content(String scrubbed_content) {
        this.scrubbed_content = scrubbed_content;
    }


}
