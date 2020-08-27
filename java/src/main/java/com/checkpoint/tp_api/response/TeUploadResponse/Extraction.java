package com.checkpoint.tp_api.response.TeUploadResponse;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;

/**
 * Created by edanha on 4/18/2017.
 */
@JsonSerialize(include=Inclusion.NON_NULL)
public class Extraction {
    private String extract_result;
    private ExtractionData extraction_data;
    private String method;
    private String output_file_name;
    private Status status;
    private boolean tex_product;
    private String extract_content;
    private String extracted_file_download_id;
    private String time;

    public String getExtract_content() {
        return extract_content;
    }

    public void setExtract_content(String extract_content) {
        this.extract_content = extract_content;
    }

    public String getExtracted_file_download_id() {
        return extracted_file_download_id;
    }

    public void setExtracted_file_download_id(String extracted_file_download_id) {
        this.extracted_file_download_id = extracted_file_download_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getExtract_result() {
        return extract_result;
    }

    public void setExtract_result(String extract_result) {
        this.extract_result = extract_result;
    }

    public ExtractionData getExtraction_data() {
        return extraction_data;
    }

    public void setExtraction_data(ExtractionData extraction_data) {
        this.extraction_data = extraction_data;
    }

    public String getOutput_file_name() {
        return output_file_name;
    }

    public void setOutput_file_name(String output_file_name) {
        this.output_file_name = output_file_name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isTex_product() {
        return tex_product;
    }

    public void setTex_product(boolean tex_product) {
        this.tex_product = tex_product;
    }
}
