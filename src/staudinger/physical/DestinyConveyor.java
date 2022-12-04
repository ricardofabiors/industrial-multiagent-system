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
 * Classe responsável por modelar as esteiras de acesso ao armazém do 
 * demonstrador Staudinger. É responsável por receber os caixotes para que haja
 * uma inserção no armazém.
 * 
 * @author Fábio Ricardo
 */
public class DestinyConveyor extends MRA{
    //"direções" (ou destinos)
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
    /**
     * Construtor padrão da classe. Seta a propriedade da skill "receive" e adiciona
     * as skills ao vetor de skills.
     * @param from_to Indica o ponto inicial e final do recebimento realizado 
     * pela skill. Exemplo: "p1 to p2".
     */
    public DestinyConveyor(String from_to){
        receive.addProperty(from_to, "yes");
        this.skills = new Skill[] {receive, stop};
    }
    
    @Override
    protected void setup(){
        defaultSetup();
    }
    
    /**
     * Implementa uma skill chamada "receive", que será externalizada como 
     * serviço através do YPA e é capaz de receber os caixotes (de certa forma,
     * os produtos) para inserir no armazém.
     */
    public Skill receive = new Skill(this, "receive", "boolean", new String[]{"int"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            int from_direction = Integer.parseInt(getArgsValues()[0]);
            switch (from_direction) {
                case LEFT:
                    System.out.println(this.myMRA.getLocalName() + ": Recebendo da esquerda..."); 
                    result = "true";
                    break;
                case RIGHT:
                    System.out.println(this.myMRA.getLocalName() + ": Recebendo da direita..."); 
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
