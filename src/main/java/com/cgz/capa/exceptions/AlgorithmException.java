package com.cgz.capa.exceptions;

/**
 * Created by czarek on 12/01/15.
 */
public class AlgorithmException extends Exception {
    public AlgorithmException(Throwable cause) {
        super(cause);
    }

    public AlgorithmException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public AlgorithmException(String message) {
        super(message);
    }
}

