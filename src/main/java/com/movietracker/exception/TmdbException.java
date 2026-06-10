package com.movietracker.exception;

public class TmdbException extends RuntimeException {

    public TmdbException(String message, Throwable cause) {
        super(message, cause);
    }

    public TmdbException(String message) {
        super(message);
    }
}
