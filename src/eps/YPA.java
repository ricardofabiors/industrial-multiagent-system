/*
 * Copyright (c) Andre Cavalcante 2008-2015
 * All right reserved
 */
package eps;

import eps.ontology.Deregistry;
import eps.ontology.Registry;
import eps.ontology.Search;
import eps.ontology.EPSOntology;
import eps.ontology.GetAllMRAInfo;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OntologyServer;
import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.Event;
import jade.domain.introspection.IntrospectionVocabulary;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Yellow Page Agent (YPA)
 *
 * @author andre
 */
public class YPA extends Agent {

    public final static String YPA_AGENT_NAME = "ypa";

    private final Set<MRAInfo> table;

    public YPA() {
        table = Collections.synchronizedSet(new HashSet<MRAInfo>());
    }

    @Override
    protected void setup() {
        handleDeadAgentEvent();
        addBehaviour(new OntologyServer(this, EPSOntology.instance(), ACLMessage.REQUEST, this));
        addMyResponderBehaviour();
    }
    
    /**
     * Subscriber for realize deregister of agents.
     */
    private void handleDeadAgentEvent(){
        AMSSubscriber myAMSSubscriber = new AMSSubscriber() {
            @Override
            protected void installHandlers(Map handlers) {
                // Associate an handler to dead-agent events
                AMSSubscriber.EventHandler terminationsHandler = new AMSSubscriber.EventHandler() {
                    @Override
                    public void handle(Event ev) {
                        DeadAgent da = (DeadAgent) ev;
                        System.out.println("Dead agent " + da.getAgent().getName());
                        deregistry(da.getAgent().getLocalName());
                    }
                };
                handlers.put(IntrospectionVocabulary.DEADAGENT, terminationsHandler);
            }
        };
        addBehaviour(myAMSSubscriber);
    }
    
    /**
     * Cria e adiciona um comportamento cíclico que responde (handle) as 
     * requisições feitas pelos agentes MRA (registro, desregistro e search).
     */
    protected void addMyResponderBehaviour(){
        //cria um template para especificar as mensagens do tipo request
        MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        
        //cria e adiciona um comportamento cíclico que analisa os requests recebidos
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive(template);
                if (msg != null) {
                    String conversationId = msg.getConversationId();
                    ContentElement ce = null;
                    //extrai o contentElement da mensagem
                    try {
                        ce = myAgent.getContentManager().extractContent(msg);   
                    } catch (Codec.CodecException | OntologyException ex) {
                        Logger.getLogger(YPA.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //verifica o tipo de conversa e repassa adequadamente o contentElement para as funções 
                    switch(conversationId){
                        case "registry":
                            serveRegistryRequest((Registry) ((Action) ce).getAction(), msg);
                            break;
                        case "search":
                            serveSearchRequest((Search) ((Action) ce).getAction(), msg);
                            break;
                        case "getAllMRAInfo":
                            serveGetAllMRAInfoRequest((GetAllMRAInfo) ((Action) ce).getAction(), msg);
                            break;
                        default: 
                            break;
                    }
                }
                else {
                    block();
                }
            }
        });
    }
    
    @Override
    protected void takeDown() {
        super.takeDown();
    }

    public void serveRegistryRequest(Registry reg, ACLMessage request) {
        MRAInfo mraInfo;
        mraInfo = reg.getMRAInfo();
        boolean b = table.add(mraInfo);

        ACLMessage inform = request.createReply();
        inform.setPerformative(ACLMessage.INFORM);
        inform.setContent(b ? "OK" : "Already present. Not set");
        send(inform);
    }

    public void serveSearchRequest(Search search, ACLMessage request) {
        boolean ignoreProperties;
        SkillTemplate st;
        List<MRAInfo> mraInfoList;

        ignoreProperties = search.isIgnoreProperties();
        st = search.getSkillTemplate();
        mraInfoList = new ArrayList<>();
        for (MRAInfo mraInfo : table) {
            if (mraInfo.hasSkillTemplate(st, ignoreProperties)) {
                mraInfoList.add(mraInfo);
            }
        }

        search = new Search();
        search.setMraInfoArr(mraInfoList.toArray(new MRAInfo[0]));
        Action act = new Action();
        act.setAction(search);
        act.setActor(this.getAID());

        ACLMessage inform = request.createReply();
        try {
            inform.setPerformative(ACLMessage.INFORM);
            getContentManager().fillContent(inform, act);
        } catch (Codec.CodecException | OntologyException ex) {
            inform.setPerformative(ACLMessage.FAILURE);
            inform.setContent("Error generating inform. Exception: " + ex);
        }
        send(inform);  
    }

    public void serveGetAllMRAInfoRequest(GetAllMRAInfo getAll, ACLMessage request) {
        getAll.setMRAInfoArr(table.toArray(new MRAInfo[0]));
        Action act = new Action();
        act.setAction(getAll);
        act.setActor(this.getAID());

        ACLMessage msg = request.createReply();
        try {
            msg.setPerformative(ACLMessage.INFORM);
            getContentManager().fillContent(msg, act);
        } catch (Codec.CodecException | OntologyException ex) {
            msg.setPerformative(ACLMessage.FAILURE);
            msg.setContent("Error getting skills from YPA. Exception: " + ex);
        }
        send(msg);
    }

    public void serveDeregistryRequest(Deregistry dereg, ACLMessage request) {
        deregistry(request.getSender().getLocalName());
        ACLMessage inform = request.createReply();
        inform.setPerformative(ACLMessage.INFORM);
        send(inform);
    }

    private void deregistry(String aid) {
        MRAInfo mraInfo;
        Iterator<MRAInfo> it = table.iterator();
        while (it.hasNext()) {
            mraInfo = it.next();
            if (mraInfo.getAID().equals(aid)) {
                table.remove(mraInfo);
                System.out.println(this.getLocalName() + ": Serviço de desregistro feito com sucesso para o agente " + mraInfo.getAID());
                return;
            }
        }
    }

}
