/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package staudinger.cognitive;

import eps.MRA;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.ParallelBehaviour;

/**
 * Esta classe implementa um item de decisão num plano de execução (de um 
 * determinado agente produto) através de 3 "PlanItem"s, um para decisão e 
 * dois para escolhas.
 * 
 * @author Fábio Ricardo
 */
public class ParallelItem implements Item {
    private PlanItem item_1, item_2;
    private MRA requester;    //agente dono do plano ao qual a decisão será adicionado

    public ParallelItem(PlanItem item_1, PlanItem item_2) {
        this.item_1 = item_1;
        this.item_2 = item_2;
        this.setRequester(item_1.getRequester());
    }
    
    @Override
    public Behaviour execute(){
        ParallelBehaviour beh = new ParallelBehaviour(requester, ParallelBehaviour.WHEN_ALL);
        
        beh.addSubBehaviour(this.item_1.execute());
        beh.addSubBehaviour(this.item_2.execute());
        
        return beh; 
    }


    public MRA getRequester() {
        return requester;
    }

    public void setRequester(MRA requester) {
        this.requester = requester;
    }
    
    
}
