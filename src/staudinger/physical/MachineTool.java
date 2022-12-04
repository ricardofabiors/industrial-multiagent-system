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

/**
 * Classe que representa a parte principal do módulo Conveyor Belt with 
 * Machine Tool, isto é, a parte que atua tampando os caixotes.
 * 
 * @author Fábio Ricardo
 */
public class MachineTool extends MRA{
    private boolean sensor;
    
    /**
     * Construtor padrão da classe. Seta a propriedade da skill "cover" e adiciona
     * as skills ao vetor de skills.
     * @param myPosition Indica o ponto de atuação da skill. Exemplo: "p1".
     */
    public MachineTool(String myPosition){
        this.sensor = true;
        cover.addProperty(myPosition, "yes");
        this.skills = new Skill[] {cover, stop};
    }
    
    @Override
    protected void setup(){
        defaultSetup();
    }
    
    /**
     * Implementa uma skill chamada "cover", que será externalizada como 
     * serviço através do YPA e é capaz de tampar os caixotes.
     */
    protected Skill cover = new Skill(this, "cover", "boolean", new String[]{"void"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            if(isThereBox()){
                System.out.println(this.myMRA.getLocalName() + ": Tampando caixote..."); 
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
            System.out.println(this.myMRA.getLocalName() + ": O módulo parou.");         
        }
    };
    
    @Override
    protected MRAInfo getMRAInfo() {
        myMrainfo = new MRAInfo(); 
        myMrainfo.setAID(this.getLocalName());
        myMrainfo.setSkills(Util.fromSkill(getSkills()));
        return myMrainfo;
    }
    
    @Override
    protected Skill[] getSkills() {
        return this.skills;
    }
    
    private boolean isThereBox(){
        if(sensor){
            return true;
        }
        else return false;
    }
    
}
