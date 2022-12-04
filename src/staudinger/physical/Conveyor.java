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
import static staudinger.physical.DestinyConveyor.LEFT;
import static staudinger.physical.DestinyConveyor.RIGHT;

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
    int moveCoilAddress, isMovedAddress; //atuador e sensor de confirmação 'moveu' da esteira
    int receiveCoilAddress, isReceivedAddress; //atuador e sensor de confirmação 'recebeu' da esteira
    int sendCoilAddress, isSentAddress; //atuador e sensor de confirmação 'enviou' da esteira
    boolean is_loaded;
    
    
    /**
     * Construtor padrão da classe. Seta a propriedade da skill "move" e adiciona
     * as skills ao vetor de skills.
     * @param from_to Indica o ponto inicial e final do movimento realizado 
     * pela skill. Exemplo: "p1 to p2".
     */
    public Conveyor(String from_to, int moveCoilAddress, int isMovedAddress, int receiveCoilAddress, 
                    int isReceivedAddress, int sendCoilAddress, int isSentAddress){
        move.addProperty(from_to, "yes");
        send.addProperty(from_to, "yes");
        receive.addProperty(from_to, "yes");
        this.skills = new Skill[] {move, receive, send};
        this.moveCoilAddress = moveCoilAddress;
        this.isMovedAddress = isMovedAddress;
        this.receiveCoilAddress = receiveCoilAddress;
        this.isReceivedAddress = isReceivedAddress;
        this.sendCoilAddress = sendCoilAddress;
        this.isSentAddress = isSentAddress;
        this.is_loaded = false;
    }
    
    @Override
    protected void setup(){
        defaultSetup();
        addResponderBehaviour();
        setupModbusClient();
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
                    
                    writeCoil(moveCoilAddress, true);
                    System.out.println(this.myMRA.getLocalName() + ": Movendo para frente...");
                    
//                    while(readInput(isMovedAddress) != true){   
//                    }
//                    writeCoil(moveCoilAddress, false); 
                    
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Conveyor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    System.out.println(this.myMRA.getLocalName() + ": Movido.");
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
     * Implementa uma skill chamada "move", que será externalizada como 
     * serviço através do YPA e é capaz de mover os caixotes (de certa forma,
     * os produtos) de um módulo ao outro.
     */
    protected Skill send = new Skill(this, "send", "boolean", new String[]{"int"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            int direction = Integer.parseInt(getArgsValues()[0]);
            switch (direction) {
                case FORWARD:
                    
                    writeCoil(sendCoilAddress, true);
                    System.out.println(this.myMRA.getLocalName() + ": Enviando para frente...");
                    
//                    while(readInput(isSentAddress) != true){
//                    }
//                    writeCoil(sendCoilAddress, false); 
                    
                    System.out.println(this.myMRA.getLocalName() + ": Enviado.");
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
     * Implementa uma skill chamada "move", que será externalizada como 
     * serviço através do YPA e é capaz de mover os caixotes (de certa forma,
     * os produtos) de um módulo ao outro.
     */
   public Skill receive = new Skill(this, "receive", "boolean", new String[]{"int"}){
        @Override
        public void execute() throws SkillExecuteException {
            isBusy = true;
            int from_direction = Integer.parseInt(getArgsValues()[0]);
            switch (from_direction) {
                case LEFT:
                    
                    writeCoil(receiveCoilAddress, true); 
                    System.out.println(this.myMRA.getLocalName() + ": Recebendo...");
                    
//                    while(readInput(isReceivedAddress) != true){
//                        //System.out.println(this.myMRA.getLocalName() + ": Recebendo..." + readInput(9));
//                    }
//                    writeCoil(receiveCoilAddress, false); 
                    
                    System.out.println(this.myMRA.getLocalName() + ": Recebido.");
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
            is_loaded = true;
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

    
