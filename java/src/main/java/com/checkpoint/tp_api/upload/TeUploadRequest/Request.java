package com.checkpoint.tp_api.upload.TeUploadRequest;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Arrays;
import java.util.List;

/**
 * Created by edanha on 4/4/2017.
 */
@SuppressWarnings("unused")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Request
{
    private String file_type;
    private String file_name;
    private Te te;
    private Extraction extraction;
    private List<String> features;
    private String md5;

    public Request(String file_type, String file_name, List<String> reports, List<String> features, String md5, String method) {
        this.file_type = file_type;
        this.file_name = file_name;
        this.te = new Te(reports);
        this.features = features;
        this.md5 = md5;
        if (method.equals("convert")) {
            this.extraction = new Extraction("pdf");
        }
        else {
            this.extraction = new Extraction("clean", Arrays.asList("sensitive_hyperlinks",
                    "macros_and_code", "embedded_objects",
                    "database_queries", "pdf_launch_actions",
                    "pdf_sound_actions", "pdf_movie_actions",
                    "pdf_uri_actions", "pdf_javascript_actions",
                    "pdf_submit_form_actions", "pdf_go_to_remote_actions", "fast_save_data"));
        }

    }

    public Request(String file_type, String file_name, List<String> reports, List<String> features, String md5) {
        this.file_type = file_type;
        this.file_name = file_name;
        this.te = new Te(reports);
        this.features = features;
        this.md5 = md5;
        this.extraction = new Extraction(null);
    }

    public Extraction getExtraction() {
        return extraction;
    }

    public void setExtraction(Extraction extraction) {
        this.extraction = extraction;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Te getTe() {
        return te;
    }

    public void setTe(Te te) {
        this.te = te;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
