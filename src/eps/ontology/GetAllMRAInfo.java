/*
 *  Andre Cavalcante e Rafael Mendonca
 *  Copyright UFAM 2015-2016
 */
package eps.ontology;

import eps.MRAInfo;
import jade.content.AgentAction;

/**
 * Asks to YPA all skills registered
 * @author Rafael
 */
public class GetAllMRAInfo implements AgentAction { 
    MRAInfo[] mraInfoArr;

    public GetAllMRAInfo() {
    }

    public MRAInfo[] getMRAInfoArr() {
        return mraInfoArr;
    }

    public void setMRAInfoArr(MRAInfo[] mraInfoArr) {
        this.mraInfoArr = mraInfoArr;
    }
}
