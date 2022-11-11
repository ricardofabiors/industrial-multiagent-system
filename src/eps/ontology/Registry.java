/*
 *  Andre Cavalcante e Rafael Mendonca
 *  Copyright UFAM 2015-2016
 */
package eps.ontology;

import eps.MRAInfo;
import jade.content.AgentAction;

/**
 * AgentAction for YPA
 * @author andre
 */
public class Registry implements AgentAction {  
    private MRAInfo mraInfo;

    public Registry() {
    }

    public MRAInfo getMRAInfo() {
        return mraInfo;
    }

    public void setMRAInfo(MRAInfo mraInfo) {
        this.mraInfo = mraInfo;
    }
       
}
