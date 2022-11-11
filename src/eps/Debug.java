/*
 * Copyright (c) Andre Cavalcante 2008-2015
 * All right reserved
 */
package eps;

/**
 * Implements an auxiliary class for debug process.
 * @author Andre
 */
public class Debug {

    private Debug() {
    }
    
    public final static int ERROR = 0;
    public final static int WARNING = 1;
    public final static int DEBUG2 = 2;
    public final static int DEBUG3 = 3;
    public final static int DEBUG = DEBUG3;
    public final static int INFO = 4;
    
    /**
     * Sets the default debug level message.
     */
    public static int debugLevel = DEBUG;
    
    /**
     * Sets the default debug level message.
     * @param level
     */
    public static void setDebugLevel(int level) {
        debugLevel = level;
    }
    
    public static int getDebugLevel() {
        return debugLevel;
    }
    
    /**
     * Prints a message with ERROR level
     * @param message
     */
    public static void printError(String message) {
        printMessageDebug(message, ERROR);
    }
    
    /**
     * Prints a message with WARNING level
     * @param message
     */
    public static void printWarning(String message) {
        printMessageDebug(message, WARNING);
    }

    /**
     * Prints a message with DEBUG level
     * @param message
     */
    public static void printDebug(String message) {
        printMessageDebug(message, DEBUG);
    }

    /**
     * Prints a message with INFO level
     * @param message
     */
    public static void printInfo(String message) {
        printMessageDebug(message, INFO);
    }

    /**
     * Prints a message with an arbitrary severity level
     * @param message
     * @param severity
     */
    public static void printMessageDebug(String message, int severity) {
        if(severity <= debugLevel) {
            System.out.println(message);
        }
    }
    
}
