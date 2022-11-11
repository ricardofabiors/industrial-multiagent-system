/*
 * Copyright (c) Andre Cavalcante 2008-2015
 * All right reserved
 */
package eps;

/**
 * Exception released when error in YPA
 * @author Andre
 */
public class YPAException extends Exception {

    public YPAException() {
    }

    public YPAException(String message) {
        super(message);
    }

    public YPAException(String message, Throwable cause) {
        super(message, cause);
    }

    public YPAException(Throwable cause) {
        super(cause);
    }

    public YPAException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
