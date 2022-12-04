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
import hardware.HardwareAcess;
import staudinger.cognitive.Box;

/**
 * Modela os módulos Rotate Conveyor Belt do demosntrador Staudinger e é responsável
 * por receber, mover e verificar a cor dos caixotes.
 * 
 * @author Fábio Ricardo
 */
public class RotateConveyor extends MRA{
    //"direções" (ou destinos)
    public static final int UP = 1;
    public static final int DOWN = 0;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
    protected int readColor = Box.BLACK;
    HardwareAcess acess = new HardwareAcess();
    
    /**
     * Construtor padrão da classe. Seta as propriedades da skill "move", "receive"
     * e "checkColor", além de adicioná-las ao vetor de skills.
     * @param to1 Indica o primeiro possível ponto final do movimento realizado 
     * pela skill "move" (para baixo). Exemplo: "to p1".
     * @param to2 Indica o segundo possível ponto final do movimento realizado 
     * pela skill "move" (para direita). Exemplo: "to p1".
     * @param from1 Indica o primeiro possível ponto inicial do recebimento realizado 
     * pela skill "receive" (de cima). Exemplo: "from p1".
     * @param from2 Indica o segundo possível ponto inicial do recebimento realizado 
     * pela skill "receive" (da direita). Exemplo: "from p1".
     */
    public RotateConveyor(String to1, String to2, String from1, String from2){
        move.addProperty(to1, "yes");
        move.addProperty(to2, "yes");
        receive.addProperty(from1, "yes");
        receive.addProperty(from2, "yes");
        checkColor.addProperty(from1, "yes");
        checkColor.addProperty(from2, "yes");
        this.skills = new Skill[] {move, receive, checkColor, stop};
    }
    
    @Override
    protected void setup(){
        defaultSetup();
    }
    
    /**
     * Implementa uma skill chamada "move", que será externalizada como 
     * serviço através do YPA e é capaz de mover os caixotes da rotate conveyor
     * para o próximo módulo.
     */
    protected Skill move = new Skill(this, "move", "boolean", new String[]{"int"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            int direction = Integer.parseInt(getArgsValues()[0]);
            
            result = HardwareAcess.moveRotateConveyor(myMRA, direction);
            
            isBusy = false;
        }
    };
    
    /**
     * Implementa uma skill chamada "receive", que será externalizada como 
     * serviço através do YPA e é capaz de receber os caixotes de outros módulos
     * para a rotate conveyor.
     */
    public Skill receive = new Skill(this, "receive", "boolean", new String[]{"int"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            int direction = Integer.parseInt(getArgsValues()[0]);
            switch (direction) {
                case UP:
                    System.out.println(this.myMRA.getLocalName() + ": Recebendo de cima..."); 
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
     * Implementa uma skill chamada "checkColor", que será externalizada como 
     * serviço através do YPA e é capaz de comparar a cor do caixote lida pelo
     * sensor (representada pela variável "readColor") com o a cor passada como 
     * parâmetro. Retorna true se a cor coincidir.
     */
    public Skill checkColor = new Skill(this, "checkColor", "boolean", new String[]{"int"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            int desiredColor = Integer.parseInt(getArgsValues()[0]);
            
            result = acess.checkColor(myMRA, desiredColor);
            
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
