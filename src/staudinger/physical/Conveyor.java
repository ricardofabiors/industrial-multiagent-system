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
 * Classe que modela os módulos Conveyor Belt do demonstrador Staudinger. É 
 * responsável por mover os caixotes de um módulo ao outro. 
 * 
 * @author Fábio Ricardo
 */
public class Conveyor extends MRA{
    //"direções" (ou destinos)
    public static final int FORWARD = 1;
    public static final int BACKWARD = 0;
    
    /**
     * Construtor padrão da classe. Seta a propriedade da skill "move" e adiciona
     * as skills ao vetor de skills.
     * @param from_to Indica o ponto inicial e final do movimento realizado 
     * pela skill. Exemplo: "p1 to p2".
     */
    public Conveyor(String from_to){
        move.addProperty(from_to, "yes");
        this.skills = new Skill[] {move, stop};
    }
    
    @Override
    protected void setup(){
        defaultSetup();
        addResponderBehaviour();
    }
    
    /**
     * Implementa uma skill chamada "move", que será externalizada como 
     * serviço através do YPA e é capaz de mover os caixotes (de certa forma,
     * os produtos) de um módulo ao outro.
     */
    protected Skill move = new Skill(this, "move", "boolean", new String[]{"int"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            int direction = Integer.parseInt(getArgsValues()[0]);
            switch (direction) {
                case FORWARD:
                    System.out.println(this.myMRA.getLocalName() + ": Movendo para frente...");
                {
                    try {
                        Thread.sleep(7000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Conveyor.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
}

    
