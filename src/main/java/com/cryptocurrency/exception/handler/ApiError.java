package com.cryptocurrency.exception.handler;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date timestamp;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private int status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String error;

    public ApiError(Date timestamp, int status, String error) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
    }

    public static final class ApiErrorBuilder {
        private Date timestamp;
        private int status;
        private String error;

        public ApiErrorBuilder() {
        }

        public static ApiErrorBuilder anApiError() {
            return new ApiErrorBuilder();
        }

        public ApiErrorBuilder withTimestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiErrorBuilder withStatus(int status) {
            this.status = status;
            return this;
        }

        public ApiErrorBuilder withError(String error) {
            this.error = error;
            return this;
        }

        public ApiError build() {
            return new ApiError(timestamp, status, error);
        }
    }
}
