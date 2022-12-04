/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package staudinger.cognitive;

import eps.MRAInfo;
import eps.Product;
import eps.SkillTemplate;
import eps.Util;
import eps.YPAException;
import eps.ontology.EPSOntology;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Os serviços finais do sistema Staudinger geralmente dependem da cor do caixote.
 * Por isso, ao solicitar um pedido qualquer (no momento, somente a produção está
 * disponível) que necessite pegar um caixote da pilha e verificar sua cor, um 
 * agente de "pedido genérico" pegará o caixote e verificará sua cor. Atualmente, 
 * o agente "Gateway" intancia esse agente somente para produção. Esta classe 
 * descreve tal agente.
 * 
 * @author Fábio Ricardo
 */
public class TestOrder extends Product{
    //"direções" ou destinos
    public static final int DOWN = 0;
    public static final int UP = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
    protected int requestedColor;        //cor requisitada (usado para produção)
    protected int requestedQuantity;     //quantidade de bolinhas requisitada (usado para inserção)
    private Plan myPlan;                 //plano de execução do agente
    
    /**
     * Construtor padrão da classe que recebe a cor e a quantidade de bolinhas.
     * @param color Cor requisitada do caixote.
     * @param quantity Quantidade de bolinhas requisitada.
     */
    public TestOrder(int color, int quantity) {
        this.requestedColor = color;
        this.requestedQuantity = quantity;
        this.myPlan = new Plan(this);
    }
        
    @Override
    protected void setup() {
        defaultSetup(); 
        produce();
    }
    
    /**
     * Implementação do método "Produce". Cria-se o plano chamando o método 
     * "createPlan" e, em seguida, o mesmo é executado ("executePlan").
     */
    @Override
    protected void produce(){
        try {
            createPlan();
        } catch (YPAException ex) {
            Logger.getLogger(TestOrder.class.getName()).log(Level.SEVERE, null, ex);
        }
        executePlan();
    }
    
    /**
     * Cria um plano de execução adicionando "PlanItem"s ao atributo "myPlan".
     * Atualmente, o agente "novo pedido" (ou "pedido genérico") conhece as 
     * skills necessárias para realizar sua função. Portanto, tais skills são 
     * definidas nos "SkillTemplate"s e em seguida são passadas num método que
     * cria/adiciona um novo item ao plano de execução. O último item é um item 
     * de decisão, que permite que o agente possa solicitar uma instanciação (ao
     * "Instantiator") de um agente do tipo produção ou inserção.
     */
    private void createPlan() throws YPAException{
        
        //conveyor 1 move o caixote 
        SkillTemplate st1 = new SkillTemplate("move", "boolean", new String[]{"int"});
        st1.addProperty("p0 to p1", "yes");
        st1.setArgsValues(new String[]{"1"});
        myPlan.addNewPlanItem(st1);    //adiciona novo item no plano de execução

//        //conveyor 2 move o caixote 
//        SkillTemplate st2 = new SkillTemplate("move", "boolean", new String[]{"int"});
//        st2.addProperty("p2 to p3", "yes");
//        st2.setArgsValues(new String[]{"1"});
//        myPlan.addNewPlanItem(st2);    //adiciona novo item no plano de execução        
        
//        //conveyor 1 envia o caixote para a conveyor 2
//        SkillTemplate st = new SkillTemplate("send", "boolean", new String[]{"int"});
//        st.addProperty("p1 to p2", "yes");
//        st.setArgsValues(new String[]{"1"});
//        myPlan.addNewPlanItem(st); 
//        
//        //conveyor 2 recebe o caixote 
//        SkillTemplate st1 = new SkillTemplate("receive", "boolean", new String[]{"int"});
//        st1.addProperty("p2 to p3", "yes");
//        st1.setArgsValues(new String[]{"2"});
//        myPlan.addNewPlanItem(st1); 
//        
//        //conveyor 2 move o caixote para a conveyor 3
//        SkillTemplate st3 = new SkillTemplate("move", "boolean", new String[]{"int"});
//        st3.addProperty("p2 to p3", "yes");
//        st3.setArgsValues(new String[]{"1"});
//        myPlan.addNewPlanItem(st3);    //adiciona novo item no plano de execução    
        
    }
    
    /**
     * Executa o plano de execução "myPlan", que é atributo da classe. É chamado
     * no método produce.
     */
    private void executePlan(){
        myPlan.execute();
    }
    
    @Override
    protected MRAInfo getMRAInfo() {
        myMrainfo = new MRAInfo(); 
        myMrainfo.setAID(this.getLocalName());
        myMrainfo.setSkills(Util.fromSkill(getSkills()));
        return myMrainfo;
    }
    
     /**
     * Sobrescreve a forma de lidar com as respostas que chegaram para o iniciador
     * da rede de contrato pra execução remota, de modo a tentar de novo a execução
     * caso o melhor custo seja maior que 0
     */
//    protected void serveHandleAllResponses(Vector responses, Vector acceptances, String requesterName, Behaviour this_remote_exc_beh){
//        ACLMessage bestPropose = null;
//        int bestCost = 1000;        //infinito
//        System.out.println(requesterName + ": Lendo proposes");
//        for (int i = 0; i < responses.size(); ++i) {
//            ACLMessage propose = (ACLMessage) responses.get(i);
//            if (propose.getPerformative() == ACLMessage.PROPOSE) {
//                int executer_cost = Integer.parseInt(propose.getContent());
//                System.out.println(requesterName + ": Custo de " + executer_cost + " para o " + propose.getSender().getLocalName());
//                if (bestPropose == null || executer_cost < bestCost) {
//                    bestPropose = propose;
//                    bestCost = executer_cost;
//                }
//            }
//        }
//        if (bestPropose != null) {
//            if(bestCost == 0){
//                ACLMessage accept = bestPropose.createReply();
//                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
//                accept.setContent("accept");
//                acceptances.add(accept);
//                System.out.println(requesterName + ": Accept enviado para " + bestPropose.getSender().getLocalName());
//            }
//            else{
//                ACLMessage refuse = bestPropose.createReply();
//                refuse.setPerformative(ACLMessage.REFUSE);
//                refuse.setContent("refuse");
//                //acceptances.add(accept);
//                System.out.println(requesterName + ": Menor custo diferente de 0 - para " + bestPropose.getSender().getLocalName());
//                System.out.println(requesterName + ": Tentando de novo...");
//                           
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(TestOrder.class.getName()).log(Level.SEVERE, null, ex);
//                }
//           
//                this_remote_exc_beh.reset();
//            }
//        }
//    }

    /**
     * Sobrescreve o método "takeDown" de "Agent" especificando que o agente 
     * "NewOrder" avisará ao "Gateway" o resultado da solicitação.
     */
    @Override
    protected void takeDown() {
        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchPerformative(ACLMessage.FAILURE)),
            MessageTemplate.MatchConversationId("(from-" + getLocalName() + ")")
        );
        ACLMessage msg = blockingReceive(mt);
        
        //analisa a mensagem de feedback
        if (msg == null) {
            System.out.println(getLocalName() + ": Tentativa de produção falhou! Insert/Production demorou muito pra responder");   
        } 
        else {
            ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
            msg2.setOntology(EPSOntology.EPSONTOLOGYNAME);
            msg2.setSender(getAID());
            msg2.addReceiver(new AID("Gateway", AID.ISLOCALNAME));
            
            if (msg.getPerformative() == ACLMessage.FAILURE) {
                System.out.println(getLocalName() + ": Tentativa de produção falhou! Insert/Production falharam"); 
            } 
            else {
                if("Insert".equals(msg.getContent())){
                    msg2.setContent("Insert");
                }
                else if("Production".equals(msg.getContent())){
                    msg2.setContent("Production");
                }
            }
            send(msg2);
            System.out.println(getLocalName() + ": msg INFORM enviada para o Gateway");
        }
        this.tbf.interrupt();
    }
}
