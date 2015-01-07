package com.cgz.capa.exceptions;

/**
 * Created by czarek on 06/01/15.
 */
public class ServiceErrorException extends Exception {

    public ServiceErrorException(Throwable cause) {
        super(cause);
    }

    public ServiceErrorException(String message, Throwable throwable) {
        super(message,throwable);
    }

    public ServiceErrorException(String message) {
        super(message);
    }
}
