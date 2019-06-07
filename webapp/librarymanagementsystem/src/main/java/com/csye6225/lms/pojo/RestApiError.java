package com.csye6225.lms.pojo;

import java.util.Arrays;
import java.util.List;

public class RestApiError {

    private String message;
    private List<String> details;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public RestApiError(String message, List<String> details) {
        super();
        this.message = message;
        this.details = details;
    }

    public RestApiError(String message, String detail) {
        super();
        this.message = message;
        details = Arrays.asList(detail);
    }
}
