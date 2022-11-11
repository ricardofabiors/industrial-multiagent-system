/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eps;

import staudinger.cognitive.PlanItem;

/**
 * Classe que modela os  
 * 
 * 
 * @author Fábio Ricardo
 */
public class MRACoalition extends MRA{
    
    /**
     * Construtor padrão da classe. Seta a propriedade da skill "move" e adiciona
     * as skills ao vetor de skills.
     * @param from_to Indica o ponto inicial e final do movimento realizado 
     * pela skill. Exemplo: "p1 to p2".
     */
    public MRACoalition(String from_to){
//        move.addProperty(from_to, "yes");;
//        this.skills = new Skill[] {move};
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
//    protected Skill move = new Skill(this, "move", "boolean", new String[]{"int"}){
//        @Override
//        public void execute() throws SkillExecuteException {
//            isBusy = true;
//            int direction = Integer.parseInt(getArgsValues()[0]);
//            switch (direction) {
//                case FORWARD:
//                    
//                    //conveyor 2 recebe o caixote 
//                    SkillTemplate st1 = new SkillTemplate("receive", "boolean", new String[]{"int"});
//                    st1.addProperty("p2 to p3", "yes");
//                    st1.setArgsValues(new String[]{"2"});
//                    PlanItem parallel_1 = myPlan.createNewPlanItem(st1);
//
//                    //conveyor 1 move o caixote para a conveyor 2
//                    SkillTemplate st2 = new SkillTemplate("move", "boolean", new String[]{"int"});
//                    st2.addProperty("p1 to p2", "yes");
//                    st2.setArgsValues(new String[]{"1"});
//                    PlanItem parallel_2 = myPlan.createNewPlanItem(st2);
//
//                    myPlan.addNewParallelItem(parallel_1, parallel_2);
//                    myPlan.execute();
//                    
//                    result = "true";
//                    break;
//
//                    
//                case BACKWARD:
//                    System.out.println(this.myMRA.getLocalName() + ": Movendo para trás...");
//                    result = "true";
//                    break;
//                    
//                default:
//                    System.out.println(this.myMRA.getLocalName() + ": Direção inválida.");
//                    result = "false";
//                    break;
//            }
//            isBusy = false;
//        }
//    };
//    
  
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

    
