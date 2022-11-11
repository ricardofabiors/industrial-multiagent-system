/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hardware;

import jade.core.Agent;
import staudinger.cognitive.Box;

/**
 * Classe que provém/proverá métodos de acesso ao Hardware do staudinger
 * através de chamadas i/o pela rede. Os métodos atuais são provisórios e de uso
 * somente demonstrativo.
 * 
 * @author Fábio Ricardo
 */
public class HardwareAcess {
    
    public static final int UP = 1;
    public static final int DOWN = 0;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
    int readColor;
    int count = 0;
    
    public HardwareAcess(){
        readColor = Box.BLACK;
    }
    
    /**
     * Move a esteira do módulo pra baixo ou pra direita. É usado pela classe 
     * "RotateConveyor" para passar o caixote para o próximo módulo escolhido na
     * produção.
     * @param thisAgent Agente "RotateConveyor" a usar o método.
     * @param direction "Direção" (na verdade, é o destino) para onde a esteira 
     * rotativa levará o caixote.
     * @return Uma "string" representando uma variável "boolean", que indica se
     * o movimento foi feito.
     */
    public static String moveRotateConveyor(Agent thisAgent, int direction){
        String result;
        switch (direction) {
            case DOWN:
                System.out.println(thisAgent.getLocalName() + ": Movendo para baixo..."); 
                result = "true";
                break;
            case RIGHT:
                System.out.println(thisAgent.getLocalName() + ": Movendo para direita..."); 
                result = "true";
                break;
            default:
                System.out.println(thisAgent.getLocalName() + ": Direção inválida.");
                result = "false";
                break;
        }
        return result;
    }
    
    /**
     * Compara a cor do caixote lida pelo sensor (representada pela variável
     * "readColor") com o a cor passada como parâmetro.
     * @param thisAgent Agente "RotateConveyor" a usar o método.
     * @param desiredColor Cor desejada para comparação.
     * @return Uma "string" representando uma variável "boolean", que indica se
     * a cor passada como parâmetro coincide com a lida pelo sensor.
     */
    public String checkColor(Agent thisAgent, int desiredColor){
        String result;
        if(desiredColor == readColor){
            System.out.println(thisAgent.getLocalName() + ": A cor do caixote coincide com a desejada"); 
            result = "true";
        } 
        else {
            System.out.println(thisAgent.getLocalName() + ": A cor do caixote NÃO coincide com a desejada"); 
            result = "false";
            count++;
            if(count == 2) readColor = Box.GREEN;
        }
        return result;
    }
}
