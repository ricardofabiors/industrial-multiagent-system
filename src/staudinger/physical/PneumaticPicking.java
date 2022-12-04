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
 * Classe que modela o módulo Pneumatic Picking, que insere bolinhas nos caixotes.
 * 
 * @author Fábio Ricardo
 */
public class PneumaticPicking extends MRA{
    private boolean sensor;
    
    /**
     * Construtor padrão da classe. Seta a propriedade da skill "insert" e adiciona
     * as skills ao vetor de skills.
     * @param myPosition Indica o ponto de atuação da skill. Exemplo: "p1".
     */
    public PneumaticPicking(String myPosition){
        this.sensor = true;
        insert.addProperty(myPosition, "yes");
        this.skills = new Skill[] {insert, stop};
    }

    @Override
    protected void setup(){
        defaultSetup();
    }
    
    /**
     * Implementa uma skill chamada "insert", que será externalizada como 
     * serviço através do YPA e é capaz de inserir bolinhas no caixote.
     */
    protected Skill insert = new Skill(this, "insert", "boolean", new String[]{"int"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            int quantity = Integer.parseInt(getArgsValues()[0]);
            if(quantity != 0 && isThereBox()){
                System.out.println(this.myMRA.getLocalName() + ": Inserindo " + getArgsValues()[0] + " produtos..."); 
                result = "true";
            }
            else{
                System.out.println(this.myMRA.getLocalName() + ": Nenhum produto para inserir"); 
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
