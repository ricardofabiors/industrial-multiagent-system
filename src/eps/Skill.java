/*
 * Copyright (c) Andre Cavalcante 2008-2015
 * All right reserved
 */
package eps;

import java.util.HashMap;

/**
 * Define one Skill.
 *
 * @author andre
 */
public abstract class Skill extends SkillBase {

    protected final MRA myMRA;
    
    /**
     * A skill is abstraction of method remote execute.
     *
     * @param name
     * @param argsTypes
     * @param resultType
     */
    public Skill(MRA myMRA, String name, String resultType, String[] argsTypes) {
        this.myMRA = myMRA;
        this.name = name;
        this.resultType = resultType;
        this.argsTypes = argsTypes;
        this.result = null;
        properties = new HashMap<>();
    }

    /**
     * This method implements a skill action.
     * Executed when a skill is required
     *
     * @throws eps.SkillExecuteException
     */
    public abstract void execute() throws SkillExecuteException;
}
