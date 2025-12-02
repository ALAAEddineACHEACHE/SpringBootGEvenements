package com.Gestion.Evenements.exception;



import org.springframework.http.HttpStatus;

public class CustomAppException extends RuntimeException {
    private final HttpStatus status;
    private final String title;

    public CustomAppException(HttpStatus status, String title, String message) {
        super(message);
        this.status = status;
        this.title = title;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }
}
