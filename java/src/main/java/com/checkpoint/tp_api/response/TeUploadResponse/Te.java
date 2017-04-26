package com.checkpoint.tp_api.response.TeUploadResponse;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by edanha on 4/4/2017.
 */
@SuppressWarnings("unused")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Te {
    private String combined_verdict;
    private int trust;
    private List<Image> images;
    private int score;
    private int confidence;
    private int severity;
    private Status status;

    public String getCombined_verdict() {
        return combined_verdict;
    }

    public void setCombined_verdict(String combined_verdict) {
        this.combined_verdict = combined_verdict;
    }

    public int getTrust() {
        return trust;
    }

    public void setTrust(int trust) {
        this.trust = trust;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }
}
