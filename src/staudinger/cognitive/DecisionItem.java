/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package staudinger.cognitive;

import eps.MRA;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

/**
 * Esta classe implementa um item de decisão num plano de execução (de um 
 * determinado agente produto) através de 3 "PlanItem"s, um para decisão e 
 * dois para escolhas.
 * 
 * @author Fábio Ricardo
 */
public class DecisionItem implements Item {
    private PlanItem decision, choice0, choice1;
    private MRA requester;    //agente dono do plano ao qual a decisão será adicionado

    /**
     * Construtor da classe. 
     * @param decision PlanItem que provém o comportamento de decisão
     * @param choice0 PlanItem que provém o comportamento de escolha0
     * @param choice1 PlanItem que provém o comportamento de escolha1
     */
    public DecisionItem(PlanItem decision, PlanItem choice0, PlanItem choice1) {
        this.setDecision(decision);
        this.setChoice0(choice0);
        this.setChoice1(choice1);
        this.setRequester(decision.getRequester());
    }
    
    /**
     * Implementação do método execute de Item. Cria-se um comportamento do tipo
     * Finite State Machine, no qual a decisão é o estado inicial e as escolhas 
     * são os estados finais. Caso o resultado do comportamento da decisão 
     * seja 0, a escolha0 se segue. Caso seja 1, a escolha1 é escolhida. 
     * @return O comportamento FSMBehaviour com a máquina de estado da decisão.
     */
    @Override
    public Behaviour execute(){
        FSMBehaviour myFSM = new FSMBehaviour(requester);
        
        //registra os estados que fazem parte da FSM
        myFSM.registerFirstState(this.decision.execute(), "decision");
        myFSM.registerLastState(this.choice0.execute(), "choice0");
        myFSM.registerLastState(this.choice1.execute(), "choice1");
        
        //registra as transições
        myFSM.registerTransition("decision", "choice0", 0);
        myFSM.registerTransition("decision", "choice1", 1);
        
        return myFSM; 
    }

    public PlanItem getDecision() {
        return decision;
    }

    public void setDecision(PlanItem decision) {
        this.decision = decision;
    }

    public PlanItem getChoice0() {
        return choice0;
    }

    public void setChoice0(PlanItem choice0) {
        this.choice0 = choice0;
    }

    public PlanItem getChoice1() {
        return choice1;
    }

    public void setChoice1(PlanItem choice1) {
        this.choice1 = choice1;
    }

    public MRA getRequester() {
        return requester;
    }

    public void setRequester(MRA requester) {
        this.requester = requester;
    }
    
    
}
