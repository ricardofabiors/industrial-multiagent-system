/*
 * Copyright (c) Andre Cavalcante 2008-2015
 * All right reserved
 */
package eps;

/**
 * Define one exception in execute the executeSkill
 * @author andre
 */
public class SkillExecuteException extends Exception {

    public SkillExecuteException() {
    }

    public SkillExecuteException(String message) {
        super(message);
    }

    public SkillExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public SkillExecuteException(Throwable cause) {
        super(cause);
    }

    public SkillExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
