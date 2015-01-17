package com.cgz.capa.exceptions;

/**
 * Created by czarek on 06/01/15.
 */
public class WebApplicationException extends Exception {

    public WebApplicationException(Throwable cause) {
        super(cause);
    }

    public WebApplicationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public WebApplicationException(String message) {
        super(message);
    }
}
