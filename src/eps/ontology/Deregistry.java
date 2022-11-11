/*
 * Copyright (c) Andre Cavalcante 2008-2015
 * All right reserved
 */
package eps.ontology;

import eps.MRAInfo;
import jade.content.AgentAction;

/**
 * Deregister for YPA
 * @author andre
 */
public class Deregistry implements AgentAction {
    private MRAInfo mraInfo;

    public Deregistry() {
    }
    
    public MRAInfo getMRAInfo() {
        return mraInfo;
    }

    public void setMRAInfo(MRAInfo mraInfo) {
        this.mraInfo = mraInfo;
    }
    
}

