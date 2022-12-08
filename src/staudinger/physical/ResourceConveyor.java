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
 * Classe responsável por modelar a esteira que leva os caixotes até os recursos,
 * que estão localizados em duas posições: R1 e R2.
 * 
 * @author Fábio Ricardo
 */
public class ResourceConveyor extends MRA{
    //posições (ou destinos)
    public static final int R1 = 0;
    public static final int R2 = 1;
    public static final int THE_END = 2;
    
    /**
     * Construtor padrão da classe. Seta as propriedades da skill "move" e adiciona
     * as skills ao vetor de skills.
     * @param from_to1 Indica o ponto inicial e final do movimento realizado do
     * início da esteira até R1. No referencial atual, esse parâmetro recebe "p6 to p7".
     * @param from_to2 Indica o ponto inicial e final do movimento realizado do
     * início da esteira até R2. No referencial atual, esse parâmetro recebe "p6 to p8".
     * @param from_to3 Indica o ponto inicial e final do movimento realizado de R1
     * até R2. No referencial atual, esse parâmetro recebe "p7 to p8".
     * @param from_to4 Indica o ponto inicial e final do movimento realizado de R2
     * até o final da esteira. No referencial atual, esse parâmetro recebe "p8 to p9".
     */
    public ResourceConveyor(String from_to1, String from_to2, String from_to3, String from_to4){
        move.addProperty(from_to1, "yes");
        move.addProperty(from_to2, "yes");
        move.addProperty(from_to3, "yes");
        move.addProperty(from_to4, "yes");
        this.skills = new Skill[] {move, stop};
    }
     
    @Override
    protected void setup(){
        defaultSetup();
        addResponderBehaviour();
    }
    
    /**
     * Implementa uma skill chamada "move", que será externalizada como serviço 
     * através do YPA e é capaz de, de acordo com o valor passado como argumento, 
     * pode mover um caixote para a posição R1, R2 ou para o fim do módulo 
     * (isto é, próximo módulo).
     */
    public Skill move = new Skill(this, "move", "boolean", new String[]{"int"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            int to_position = Integer.parseInt(getArgsValues()[0]);
            switch (to_position) {
                case R1:
                    System.out.println(this.myMRA.getLocalName() + ": Movendo para R1..."); 
                    result = "true";
                    break;
                case R2:
                    System.out.println(this.myMRA.getLocalName() + ": Movendo para R2..."); 
                    result = "true";
                    break;
                case THE_END:
                    System.out.println(this.myMRA.getLocalName() + ": Movendo para o final..."); 
                    result = "true";
                    break;
                default:
                    System.out.println(this.myMRA.getLocalName() + ": Destino inválido");
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
