/*
 *  Andre Cavalcante e Rafael Mendonca
 *  Copyright UFAM 2015-2016
 */
package eps;

import eps.ontology.EPSOntology;
import eps.ontology.Deregistry;
import eps.ontology.GetAllMRAInfo;
import eps.ontology.Registry;
import eps.ontology.Search;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Services of YPA
 *
 * @author Rafael
 */
public final class YPAServices {

    private static final AID ypaAID = new AID(YPA.YPA_AGENT_NAME, false);
    
    public static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\u001B[0m";

    private YPAServices() {
    }

    public static void registry(MRA thisAgent, MRAInfo mraInfo) throws YPAException {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.setSender(thisAgent.getAID());
        request.addReceiver(ypaAID);
        request.setOntology(EPSOntology.EPSONTOLOGYNAME);
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        request.setConversationId("registry");
        Registry reg = new Registry();
        reg.setMRAInfo(mraInfo);
        Action act = new Action(ypaAID, reg);
        try {
            thisAgent.getContentManager().fillContent(request, act);
            thisAgent.send(request);

            MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchOntology(EPSOntology.EPSONTOLOGYNAME));
            ACLMessage inform = thisAgent.blockingReceive(mt, 2000);
            if (inform == null) {
                throw new YPAException("Error of timeout communicating whith YPA.");
            } else {
                System.out.println(inform.getSender().getLocalName() + ": Serviço de registro feito com sucesso para o agente " + request.getSender().getLocalName());
            }
        } catch (Codec.CodecException | OntologyException ex) {
            throw new YPAException("Error registring skills by MRA: " + thisAgent.getLocalName() + ". Exception: " + ex);
        }
    }

    public static MRAInfo[] search(Agent thisAgent, SkillTemplate st) throws YPAException {
        return search(thisAgent, st, false);
    }

    public static MRAInfo[] search(Agent thisAgent, SkillTemplate st, boolean ignoreProperties) throws YPAException {
        MRAInfo[] result = new MRAInfo[0];
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(ypaAID);
        request.setSender(thisAgent.getAID());
        request.setOntology(EPSOntology.EPSONTOLOGYNAME);
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        request.setConversationId("search");
        Search sa = new Search();
        sa.setSkillTemplate(st);
        sa.setIgnoreProperties(ignoreProperties);
        Action act = new Action(ypaAID, sa);
        try {
            thisAgent.getContentManager().fillContent(request, act);
            thisAgent.send(request);
            
            MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.or(
                            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                            MessageTemplate.MatchPerformative(ACLMessage.FAILURE)),
                    MessageTemplate.MatchOntology(EPSOntology.EPSONTOLOGYNAME));
            ACLMessage msg = thisAgent.blockingReceive(mt);//, 2000);
            if (msg == null) {
                throw new YPAException("Error of timeout communicating whith YPA");
            } else {
                if (msg.getPerformative() == ACLMessage.FAILURE) {
                    throw new YPAException(msg.getContent());
                } else {
                    ContentElement ce = thisAgent.getContentManager().extractContent(msg);
                    sa = (Search) ((Action) ce).getAction();
                    result = sa.getMraInfoArr();
                    System.out.println(YELLOW + msg.getSender().getLocalName() + ": Serviço de busca feito com sucesso para o agente " + thisAgent.getLocalName() + RESET);   
                }
            }
        } catch (Codec.CodecException | OntologyException ex) {
            throw new YPAException("Error of search in YPA. Exception: " + ex);
        }
        return result;
    }

    public static MRAInfo[] getAllMRAInfoFromYPA(Agent thisAgent) throws YPAException {
        MRAInfo[] result = new MRAInfo[0];

        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(ypaAID);
        request.setSender(thisAgent.getAID());
        request.setOntology(EPSOntology.EPSONTOLOGYNAME);
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        request.setConversationId("getAllMRAInfo");
        GetAllMRAInfo getAll = new GetAllMRAInfo();     
        Action act = new Action(ypaAID, getAll);
        try {
            thisAgent.getContentManager().fillContent(request, act);
            thisAgent.send(request);
            MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.or(
                            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                            MessageTemplate.MatchPerformative(ACLMessage.FAILURE)),
                    MessageTemplate.MatchOntology(EPSOntology.EPSONTOLOGYNAME));
            ACLMessage msg = thisAgent.blockingReceive(mt, 2000);
            if (msg == null) {
                throw new YPAException("Error of timeout communicating whith YPA");
            } else {
                if (msg.getPerformative() == ACLMessage.FAILURE) {
                    throw new YPAException(msg.getContent());
                } else {
                    ContentElement ce = thisAgent.getContentManager().extractContent(msg);
                    getAll = (GetAllMRAInfo) ((Action) ce).getAction();
                    result = getAll.getMRAInfoArr();
                    System.out.println(msg.getSender().getLocalName() + ": Serviço de busca por todos os MRAInfos feito com sucesso para o agente " + thisAgent.getLocalName());
                }
            }
        } catch (Codec.CodecException | OntologyException ex) {
            throw new YPAException("Error getting skills in YPA. Exception: " + ex);
        }
        return result;
    }

    public static void deregister(MRA thisAgent) throws YPAException {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.setSender(thisAgent.getAID());
        request.addReceiver(ypaAID);
        request.setOntology(EPSOntology.EPSONTOLOGYNAME);
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        request.setConversationId("deregister");
        Deregistry dereg = new Deregistry();
        Action act = new Action(ypaAID, dereg);
        thisAgent.send(request);

        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchOntology(EPSOntology.EPSONTOLOGYNAME));
        ACLMessage inform = thisAgent.blockingReceive(mt, 2000);
        if (inform == null) {
            throw new YPAException("Error of timeout communicating whith YPA");
        } else {
            System.out.println(inform.getSender().getLocalName() + ": Serviço de desregistro feito com sucesso para o agente " + thisAgent.getLocalName());
            System.out.println(thisAgent.getLocalName() + " deresgitred.");
        }
    }

}
