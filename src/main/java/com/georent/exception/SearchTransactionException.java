package com.georent.exception;

import org.springframework.transaction.CannotCreateTransactionException;

public class SearchTransactionException  extends CannotCreateTransactionException {


    public SearchTransactionException(String msg) {
        super(msg);
    }

    /**
     *
     * @param msg
     * @param cause
     */
    public SearchTransactionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
