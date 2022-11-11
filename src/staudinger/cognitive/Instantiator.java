/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package staudinger.cognitive;

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

/**
 * Classe que descreve um agente Instanciador, que é um agente MRA da camada
 * cognitiva responsável por instanciar outros agentes para os agentes produtos.
 * 
 * @author Fábio Ricardo
 */
public class Instantiator extends MRA{
    
    ContainerController containerController;
    AgentController agentController;
    Runtime runtime;
    
    @Override
    protected void setup(){
        defaultSetup();
        addResponderBehaviour();
    }
    
    /**
     * Construtor padrão da classe. Cria o container onde os agentes serão 
     * inseridos e adiciona as skills ao vetor de skills.
     */
    public Instantiator() {
        this.skills = new Skill[] {instantiate};
        runtime = Runtime.instance();
        containerController = runtime.createAgentContainer(new ProfileImpl(false));
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Instantiator.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    public Skill instantiate = new Skill(this, "instantiate", "boolean", new String[]{"string", "string", "string"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            String nickname = this.getArgsValues()[0];
            String className = this.getArgsValues()[1];
            String argument1 = this.getArgsValues()[2];
            Behaviour beh = new OneShotBehaviour(myMRA){
                @Override
                public void action() {    
                    try {
                        agentController = containerController.createNewAgent(nickname, className, new String[]{argument1});
                        agentController.start();
                    } catch (StaleProxyException ex) {
                        Logger.getLogger(Instantiator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            myMRA.addBehaviour(beh);
            result = "true"; 
            isBusy = false;
        }
    };
    
    @Override
    protected Skill[] getSkills() {
        return this.skills;
    }
}
