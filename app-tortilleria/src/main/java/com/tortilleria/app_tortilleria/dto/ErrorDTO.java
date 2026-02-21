package com.tortilleria.app_tortilleria.dto;

import java.time.LocalDateTime;

public class ErrorDTO {
    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;

    public ErrorDTO(int status, String error, String message) {
        this.timeStamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public LocalDateTime getTimeStamp() { return timeStamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
}
