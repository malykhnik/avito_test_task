package io.codefresh.gradleexample.exception;

public class IncorrectFormatOfRequest extends RuntimeException{
    public IncorrectFormatOfRequest(String message) {
        super(message);
    }
}
