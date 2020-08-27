package com.checkpoint.tp_api.response.TeUploadResponse;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by edanha on 3/28/2017.
 */
@SuppressWarnings("unused")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class TexUploadResponse {
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}


