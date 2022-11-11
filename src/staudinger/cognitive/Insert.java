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
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Esta classe descreve uma inserção de caixote dentro do sistema. Os agentes 
 * objetos dela representam uma inserção de fato e são responsáveis por levar
 * tais caixotes à "DestinyConveyor" adequada. Na descrição do sistema, esse 
 * destino é somente um, o que permite que tais agentes, em seus planos de execução,
 * saibam as skills que deverão ser solicitadas, assim como a ordem de execução
 * das mesmas. No momento, a inserção de caixotes está resumida à emergência na
 * produção de um produto. Entretanto, em versões futuras tais inserções poderão
 * ser ofertadas como um serviço final para o usuário, assim como a produção de
 * um produto.
 * 
 * @author Fábio Ricardo
 */
public class Insert extends Product{
    //"direções" ou destinos
    public static final int UP = 1;
    public static final int DOWN = 0;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
    protected int requestedColor;   //cor requisitada (atributo não utilizado até então)
    protected Box myBox;            //caixote atual atrelado ao agente (atributo não utilizado até então)
    private Plan myPlan;            //plano de execução do agente
    
    /**
     * Construtor da classe que recebe a cor do caixote.
     * @param color Cor do caixote a ser inserido.
     */
    public Insert(int color) {
        this.requestedColor = color;
        this.myPlan = new Plan(this);
    }
    
    /**
     * Construtor padrão da classe. É passado "UNKNOWN" para a cor que será 
     * atribuída somente no método "setup" do agente, uma vez que esse será 
     * instanciado pelo agente "Instantiator" como resultado de uma skill.
     */    
    public Insert() {
        this(Box.UNKNOWN);
    }
    
    /**
     * Implementação do método "setup" do agente. Aqui é verificado através do 
     * "if" se a instanciação do agente foi feita pelo "Instantiator" ou "Gateway". 
     * Essas instanciações são diferentes porque o "Gateway" adiciona ao container
     * um agente cuja a classe foi inicializada através do construtor que recebe
     * a cor, enquanto o "Instantiator" cria de fato o agente através de um 
     * AgentController, e, portanto, recebe a cor do caixote pelos argumentos 
     * passados na função "createNewAgent".
     */    
    @Override
    protected void setup() {
        //permite que a cor do caixote seja passada pelo argumento do agente
        if(requestedColor == Box.UNKNOWN){
            Object[] args = getArguments();
            if(args != null){
                this.requestedColor = Integer.parseInt((String) args[0]);
            }
        }
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
            Logger.getLogger(Insert.class.getName()).log(Level.SEVERE, null, ex);
        }
        executePlan();
    }
    
    /**
     * Cria um plano de execução adicionando "PlanItem"s ao atributo "myPlan".
     * Atualmente, o agente inserção conhece as skills necessárias para chegar
     * ao destino adequado. Portanto, tais skills são definidas nos "SkillTemplate"s
     * e em seguida são passadas num método que cria/adiciona um novo item ao plano
     * de execução.
     */
    private void createPlan() throws YPAException{
        //rotate conveyor 1 move o caixote para a conveyor 2
        SkillTemplate st = new SkillTemplate("move", "boolean", new String[]{"int"});
        st.addProperty("to p2", "yes");
        st.addProperty("to p11", "yes");
        st.setArgsValues(new String[]{"3"});
        myPlan.addNewPlanItem(st);    //adiciona novo item no plano de execução
        
        //conveyor 2 move o caixote para a destiny conveyor 1
        SkillTemplate st1 = new SkillTemplate("move", "boolean", new String[]{"int"});
        st1.addProperty("p11 to p12", "yes");
        st1.setArgsValues(new String[]{"1"});
        myPlan.addNewPlanItem(st1);    //adiciona novo item no plano de execução
        
        //destiny conveyor 1 recebe o caixote 
        SkillTemplate st2 = new SkillTemplate("receive", "boolean", new String[]{"int"});
        st2.addProperty("p9 to p10", "yes");
        st2.setArgsValues(new String[]{"2"});
        myPlan.addNewPlanItem(st2);    //adiciona novo item no plano de execução
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

    @Override
    public void doDelete() {
        super.doDelete(); 
    }
    
    /**
     * Sobrescreve o método "takeDown" de "Agent" especificando que o agente 
     * "Insert" avisará ao "Gateway" que houve uma inserção.
     */
    @Override
    protected void takeDown() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setOntology(EPSOntology.EPSONTOLOGYNAME);
        
        String splittedName[] = getLocalName().split(" ");
        String conversationID = splittedName[1];
        msg.setConversationId(conversationID);
        
        String orderName = conversationID.substring(6, conversationID.length()-1);
        
        msg.setSender(getAID());
        //msg.addReceiver(new AID("Gateway", AID.ISLOCALNAME));
        msg.addReceiver(new AID(orderName, AID.ISLOCALNAME));
        msg.setContent("Insert");
        send(msg);
        System.out.println(getLocalName() + ": msg INFORM enviada para o meu NewOrder");
    }

}
