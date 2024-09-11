package io.codefresh.gradleexample.exception;

public class IncorrectFormatOfRequestException extends RuntimeException{
    public IncorrectFormatOfRequestException(String message) {
        super(message);
    }
}
