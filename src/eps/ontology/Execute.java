/*
 * Copyright (c) Andre Cavalcante 2008-2015
 * All right reserved
 */
package eps.ontology;

import eps.MRAInfo;
import eps.SkillTemplate;
import jade.content.AgentAction;

/**
 * Define one agent action for execute skill
 * @author andre
 */
public class Execute implements AgentAction {
    private SkillTemplate skill;
    private MRAInfo mrasInfo;
    
    public Execute() {
    }
    
    public SkillTemplate getSkillTemplate() {
        return skill;
    }

    public void setSkillTemplate(SkillTemplate skill) {
        this.skill = skill;
    }

    public MRAInfo getMRAInfo() {
        return mrasInfo;
    }

    public void setMRAInfo(MRAInfo mrasInfo) {
        this.mrasInfo = mrasInfo;
    }
    

}
