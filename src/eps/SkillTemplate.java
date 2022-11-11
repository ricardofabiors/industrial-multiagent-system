/*
 *  Andre Cavalcante e Rafael Mendonca
 *  Copyright UFAM 2015-2016
 */
package eps;

import jade.content.Concept;
import java.util.HashMap;

/**
 * Template for registry/search of skills
 *
 * @author andre
 */
public class SkillTemplate extends SkillBase implements Concept {

    public SkillTemplate() {
        this("skill", "void", new String[0]);
    }

    public SkillTemplate(String name) {
        this(name, "void", new String[0]);
    }

    public SkillTemplate(String name, String resultType) {
        this(name, resultType, new String[0]);
    }

    public SkillTemplate(String name, String resultType, String[] argsTypes) {
        this.name = name;
        this.resultType = resultType;
        this.argsTypes = argsTypes;
        properties = new HashMap<>();
    }
}
