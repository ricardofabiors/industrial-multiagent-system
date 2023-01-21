/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eps;

import staudinger.cognitive.*;
import eps.MRA;
import eps.MRAInfo;
import eps.Skill;
import eps.SkillExecuteException;
import eps.Util;
import jade.core.ProfileImpl;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import java.util.Vector;
import staudinger.physical.Conveyor;
import static staudinger.physical.Conveyor.BACKWARD;
import static staudinger.physical.Conveyor.FORWARD;

/**
 * Classe que descreve um agente Instanciador, que é um agente MRA da camada
 * cognitiva responsável por instanciar outros agentes para os agentes produtos.
 * 
 * @author Fábio Ricardo
 */
public class MRACoalition extends MRA{
    protected SkillTemplate skil1_1, skil1_2;
    protected boolean key_1, key_2;
        
    @Override
    protected void setup(){
        defaultSetup();
        //addResponderBehaviour();
    }
    
    /**
     * Construtor padrão da classe. Cria o container onde os agentes serão 
     * inseridos e adiciona as skills ao vetor de skills.
     */
    public MRACoalition(String from_to){
        move.addProperty(from_to, "yes");
        this.skills = new Skill[] {move};
    }
    
    @Override
    protected MRAInfo getMRAInfo() {
        myMrainfo = new MRAInfo(); 
        myMrainfo.setAID(this.getLocalName());
        myMrainfo.setSkills(Util.fromSkill(getSkills()));
        return myMrainfo;
    }
    
    /**
     * Implementa uma skill chamada instantiate, que será externalizada como 
     * serviço através do YPA e é capaz de instanciar outros agentes.
     */
    protected Skill move = new Skill(this, "move", "boolean", new String[]{"int"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            int direction = Integer.parseInt(getArgsValues()[0]);
            switch (direction) {
                case FORWARD:
                    System.out.println(this.myMRA.getLocalName() + ": Movendo para frente...");
                    
                    try {
                        Thread.sleep(7000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MRACoalition.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    result = "true";
                    break;

                case BACKWARD:
                    System.out.println(this.myMRA.getLocalName() + ": Movendo para trás...");
                    result = "true";
                    break;
                default:
                    System.out.println(this.myMRA.getLocalName() + ": Direção inválida.");
                    result = "false";
                    break;
            }
            isBusy = false;
        }
    };
    
    @Override
    protected void serveHandleAllResponses(Vector responses, Vector acceptances, String requesterName, Behaviour this_behaviour){
        ACLMessage bestPropose = null;
        int bestCost = 1000;        //infinito
        System.out.println(requesterName + ": Lendo proposes");
        for (int i = 0; i < responses.size(); ++i) {
            ACLMessage propose = (ACLMessage) responses.get(i);
            if (propose.getPerformative() == ACLMessage.PROPOSE) {
                int executer_cost = Integer.parseInt(propose.getContent());
                System.out.println(requesterName + ": Custo de " + executer_cost + " para o " + propose.getSender().getLocalName());
                if (bestPropose == null || executer_cost < bestCost) {
                    bestPropose = propose;
                    bestCost = executer_cost;
                }
            }
        }
        if (bestPropose != null && bestCost == 0) {
            ACLMessage accept = bestPropose.createReply();
            accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            accept.setContent("accept");
            acceptances.add(accept);
            System.out.println(requesterName + ": Accept enviado para " + bestPropose.getSender().getLocalName());
        }
        else{
            System.out.println(requesterName + ": Reiniciando execução remota - a melhor proposta (" + bestPropose.getSender().getLocalName() + ") tem custo maior que 0");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(NewOrder.class.getName()).log(Level.SEVERE, null, ex);
            }
            this_behaviour.reset();
        }
        
        if(!isReady()){
            
        }
    }
    
    @Override
    protected Skill[] getSkills() {
        return this.skills;
    }
    
    protected boolean isReady() {
        if(key_1 && key_2){
            return true;
        }
        else return false;
    }
}
