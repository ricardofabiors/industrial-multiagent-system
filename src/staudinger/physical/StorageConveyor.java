/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package staudinger.physical;

import eps.MRA;
import eps.MRAInfo;
import eps.Skill;
import eps.SkillExecuteException;
import eps.Util;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe que modela o módulo Register Storage with Conveyor Belt do demonstrador
 * Staudinger. Esse módulo é responsável por pegar um novo caixote da pilha de 
 * caixotes.
 * 
 * @author Fábio Ricardo
 */
public class StorageConveyor extends MRA{
    
    private boolean sensorIn;
    
    /**
     * Construtor padrão da classe. Seta a propriedade da skill "getNewBox" e 
     * adiciona as skills ao vetor de skills.
     * @param from_to Indica o ponto inicial e final do movimento realizado 
     * pela skill "getNewBox" da pilha. Exemplo: "p1 to p2".
     */
    public StorageConveyor(String from_to){
        this.sensorIn = true;
        getNewBox.addProperty(from_to, "yes");
        this.skills = new Skill[] {getNewBox, stop};
    }
    
    @Override
    protected void setup(){
        defaultSetup();
//        addResponderBehaviour();
//        addResponderBehaviour();
//        addResponderBehaviour();
//        addSkillExecutionBehaviour();
    }
    
    /**
     * Implementa uma skill chamada "getNewBox", que será externalizada como 
     * serviço através do YPA e é capaz de pegar um novo caixote da pilha e movê-lo
     * para o próximo módulo
     */
    protected Skill getNewBox = new Skill(this, "getNewBox", "boolean", new String[]{"void"}){   
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            if(isThereBox()){
                System.out.println(this.myMRA.getLocalName() + ": Pegando e movendo um novo caixote..."); 
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Conveyor.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(this.myMRA.getLocalName() + ": Caixote pego e movido"); 
                result = "true";
            }
            else{
                result = "false";
            }
            isBusy = false;
        }
    };
    
    /**
     * Implementa uma skill chamada "stop", que será externalizada como 
     * serviço através do YPA e é capaz de parar a esteira do módulo. (não usada até então)
     */
    protected Skill stop = new Skill(this, "stop", "boolean", new String[]{"void"}){
        @Override
        public void execute() throws SkillExecuteException {
            System.out.println(this.myMRA.getLocalName() + ": Minha esteira parou.");         
        }
    };
    
    @Override
    protected Skill[] getSkills() {
        return this.skills;
    }
    
    @Override
    protected MRAInfo getMRAInfo() {
        myMrainfo = new MRAInfo(); 
        myMrainfo.setAID(this.getLocalName());
        myMrainfo.setSkills(Util.fromSkill(getSkills()));
        return myMrainfo;
    }
    
    private boolean isThereBox(){
        if(sensorIn){
            return true;
        }
        else return false;
    }
}
