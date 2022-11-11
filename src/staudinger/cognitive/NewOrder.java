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
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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
public class NewOrder extends Product{
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
    public NewOrder(int color, int quantity) {
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
            Logger.getLogger(NewOrder.class.getName()).log(Level.SEVERE, null, ex);
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
        //pega um novo caixote
        SkillTemplate st = new SkillTemplate("getNewBox", "boolean", new String[]{"void"});
        st.addProperty("p0 to p1", "yes");
        myPlan.addNewPlanItem(st);    //adiciona novo item no plano de execução
        
        //rotate conveyor recebe o caixote
        SkillTemplate st0 = new SkillTemplate("receive", "boolean", new String[]{"int"});
        st0.addProperty("from p1", "yes");
        st0.setArgsValues(new String[]{"1"});
        myPlan.addNewPlanItem(st0);    //adiciona novo item no plano de execução
        
        //rotate conveyor analisa a cor do caixote
        SkillTemplate st1 = new SkillTemplate("checkColor", "boolean", new String[]{"int"});
        st1.addProperty("from p1", "yes");
        st1.setArgsValues(new String[]{String.valueOf(requestedColor)});
        PlanItem decision = myPlan.createNewPlanItem(st1);    //adiciona novo item no plano de execução
        
        //possível instanciação de um agente do tipo Insert
        SkillTemplate st2 = new SkillTemplate("instantiate", "boolean", new String[]{"string", "string", "string"});
        st2.setArgsValues(new String[]{("Insert (from-" + this.getLocalName() + ")"), "staudinger.cognitive.Insert", String.valueOf(requestedColor)});
        PlanItem choice0 = myPlan.createNewPlanItem(st2);    //adiciona novo item no plano de execução        
        
        //possível instanciação de um agente do tipo Production
        SkillTemplate st3 = new SkillTemplate("instantiate", "boolean", new String[]{"string", "string", "string"});
        st3.setArgsValues(new String[]{("Production (from-" + this.getLocalName() + ")"), "staudinger.cognitive.Production", String.valueOf(requestedQuantity)});
        PlanItem choice1 = myPlan.createNewPlanItem(st3);    //adiciona novo item no plano de execução        
        
        myPlan.addNewDecisionItem(decision, choice0, choice1);    //adiciona novo item de decisão no plano de execução
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
        
    }
}
