package com.cgz.capa.exceptions;

/**
 * Created by czarek on 12/01/15.
 */
public class AlgorithmErrorException extends Exception {
    public AlgorithmErrorException(Throwable cause) {
        super(cause);
    }

    public AlgorithmErrorException(String message, Throwable throwable) {
        super(message,throwable);
    }

    public AlgorithmErrorException(String message) {
        super(message);
    }
}

