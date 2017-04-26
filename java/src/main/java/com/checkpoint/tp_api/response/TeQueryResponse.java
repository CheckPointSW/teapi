package com.checkpoint.tp_api.response;

import com.checkpoint.tp_api.response.TeUploadResponse.Response;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by edanha on 3/29/2017.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class TeQueryResponse {
    private List<Response> response;

    public List<Response> getResponse() {
        return response;
    }

    // used by objectMapper
    public void setResponse(List<Response> response) {
        this.response = response;
    }
}
