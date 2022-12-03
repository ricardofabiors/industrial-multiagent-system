/*
 * Copyright (c) Andre Cavalcante 2008-2015
 * All right reserved
 */
package eps;

import de.re.easymodbus.modbusclient.ModbusClient;
import eps.ontology.Execute;
import eps.ontology.EPSOntology;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OntologyServer;
import jade.core.behaviours.WakerBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class define one Mecatronic Agent. To create a mecatronic agent, create a
 * class extends MRA.
 *
 * @author andre
 */
public abstract class MRA extends Agent {

    private Behaviour autorunBeh;
    public final static String MRA_AGENT_NAME = "mra";
    public ModbusClient modbusClient;
    
    protected Skill[] skills;           //vetor de skills do MRA
    protected MRAInfo myMrainfo;        //conjuto de informações do MRA
    protected String cost = "1";        //custo (tempo) (não utilizado ainda)
    protected boolean isBusy = false;   //se está ocupado
    
    public static final String GREEN = "\033[0;32m";    //cor verde a ser utilizada nos prints
    public static final String RESET = "\u001B[0m";
    
    protected ThreadedBehaviourFactory tbf = new ThreadedBehaviourFactory();
    
    
    public MRA() {
    }

    /**
     * The Mecatronic Agent MUST implements this method to return its information
     * @return the MRAInfo from this Mecatronic Agent.
     */
    protected abstract MRAInfo getMRAInfo();

    /**
     * The Mecatronic Agent MUST implements this method to return an array
     * of the truly skills of this MRA.
     */
    protected abstract Skill[] getSkills();

    /**
     * This method executed after initialization father agent allowing son
     * initialization.
     */
    protected void init() {
    }

    /**
     * This method executed in OneShotBehaviour after initialization agent (end)
     * Automatic code executed.
     */
    protected void autorun() {
    }

    /**
     * Call to stop a CyclicBehaviour.
     */
    protected void stopAutorun() {
        removeBehaviour(autorunBeh);
    }

    @Override
    protected void setup() {
        defaultSetup();
        //Execute the autorun
        addBehaviour(new WakerBehaviour(this, 1000) {
            @Override
            public void onWake() {
                init();
                autorunBeh = new CyclicBehaviour(myAgent) {
                    @Override
                    public void action() {
                        autorun();
                    }
                };
                myAgent.addBehaviour(autorunBeh);
            }
        });
    }
    
    /**
     * Configurações iniciais de um MRA para ser chamado nos métodos "setup()"
     * das classes filhas. Registra o MRA no YPA, além de registrar a ontologia
     * e a linguagem.
     */
    protected void defaultSetup() {
        //registry the language and ontology
        getContentManager().registerLanguage(new SLCodec());
        getContentManager().registerOntology(EPSOntology.instance());

        //Registry the MRA in YPA
        try {
            YPAServices.registry(this, getMRAInfo());
        } catch (YPAException ex) {
            System.out.println("YPA return with error. Exception: " + ex);
        }

        addBehaviour(new OntologyServer(this, EPSOntology.instance(), ACLMessage.REQUEST, this));
    }

    @Override
    protected void takeDown() {
        this.tbf.interrupt();
    }

    
    /**
     * Cria e adiciona um comportamento cíclico que permite ao MRA participar
     * de várias redes de contratos seguindo o contract-net-protocol do FIPA 
     * como "participante". Os métodos que especificam o tratamento de CFPs e
     * ACCEPT_PROPOSALs são sobrescritos com chamadas de outros métodos (servos) 
     * da classe MRA. Assim, o programador, ao criar classes filhas de MRA, pode 
     * sobrescrever tais métodos e chamar esta mesma função para adicionar o 
     * comportamento "respondedor" agora específico para sua aplicação.
     */
    protected void addResponderBehaviour() {
        //cria um template para especificar as mensagens que interessam
        MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchProtocol("fipa-contract-net"),
            MessageTemplate.MatchPerformative(ACLMessage.CFP)
        );
        
        Behaviour responder = new ContractNetResponder(this, template){
            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) {
                return serveHandleCfp(cfp);     
            }
            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
                return serveHandleAcceptProposal(cfp, propose, accept); 
            }
        };
        
        addBehaviour(tbf.wrap(responder));
    }
    
    /**
     * Método servo que especifica como são tratados os CFPs que chegam para o 
     * MRA. É verificado se o MRA tem a skill desejada. Se sim, o mesmo envia um 
     * PROPOSE com seu custo (tempo) (ainda não implementado) para o agente que 
     * fez a chamada por proposta. Programadores podem sobrescrever este método 
     * para descrever uma forma específica para sua aplicação.
     * @param cfp Mensagem ACL do tipo CFP a ser analisada.
     * @return A mensagem ACL de resposta, que pode REFUSE ou PROPOSE.
     */
    protected ACLMessage serveHandleCfp(ACLMessage cfp) {
        System.out.println(getLocalName() + ": Cfp recebido de " + cfp.getSender().getLocalName());
        ACLMessage reply = cfp.createReply();
        reply.setPerformative(ACLMessage.REFUSE);       //por default, o perfomativo é refuse
        try {                        
            Execute exc = (Execute) cfp.getContentObject();
            SkillTemplate requestedSkill = (SkillTemplate) exc.getSkillTemplate();
            System.out.println(getLocalName() + ": Extraindo skill");
            for (Skill skill : skills) {
                //verifica se a skill solicitada é válida e muda o perfomativo, se for o caso
                if (Util.fromSkill(skill).equalsWithoutAllProperties(requestedSkill)) { 
                    if (!isBusy) {
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(cost);
                        System.out.println(getLocalName() + ": Skill coincidente identificada");
                    }
                    else System.out.println(getLocalName() + ": Ocupado!");
                }
            }
        } catch (UnreadableException ex) {
            Logger.getLogger(MRA.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ContentObject inválido.");
        }
        return reply;
    }
    
    /**
     * Método servo que especifica como são tratados as aceitações das propostas
     * feitas pelo MRA. É verificado novamente se o MRA tem a skill desejada. Se 
     * sim, executa-se a skill e envia-se um INFORM ou FAILURE, dependendo do
     * resultado da execução. Programadores podem sobrescrever este método 
     * para descrever uma forma específica para sua aplicação.
     * @param cfp Mensagem ACL recebida pelo MRA.
     * @param propose Mensagem ACL com a proposta enviada pelo MRA.
     * @param accept Mensagem ACL de aceitação da proposta recebida pelo MRA.
     * @return A mensagem ACL de resposta que informa se a skill foi executada. 
     */
    protected ACLMessage serveHandleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        System.out.println(getLocalName() + ": Accept recebida de " + accept.getSender().getLocalName());
        ACLMessage reply = accept.createReply();
        try {                        
            Execute exc = (Execute) cfp.getContentObject();
            SkillTemplate requestedSkill = exc.getSkillTemplate();
            for (Skill sk : skills) {
                //verifica se a skill solicitada é válida e a executa, se for o caso
                if (Util.fromSkill(sk).equalsWithoutAllProperties(requestedSkill)) { 
                    sk.setArgsTypes(requestedSkill.getArgsTypes());
                    sk.setArgsValues(requestedSkill.getArgsValues());
                    try {
                        sk.execute();
                        if("failed".equals(sk.getResult())) {
                            reply.setPerformative(ACLMessage.FAILURE);
                            reply.setContent("failed");
                        }
                        else {
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent(sk.getResult());
                        }
                    } catch (SkillExecuteException ex) {
                        Logger.getLogger(MRA.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (UnreadableException ex) {
            Logger.getLogger(MRA.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ContentObject inválido.");
        }
        System.out.println(getLocalName() + ": Resposta enviada ao Accept de " + accept.getSender().getLocalName());
        
        return reply;         
    }

    /**
     * Cria e adiciona um comportamento de execução remota que permite ao MRA 
     * iniciar uma rede de contratos seguindo o FIPA contract-net-protocol.
     * Os métodos que especificam a preparação de CFPs, o tratamento de respostas
     * e tratamento de INFORMs são sobrescritos com chamadas de outros métodos 
     * (servos) da classe MRA. Assim, o programador, ao criar classes filhas de
     * MRA, pode sobrescrever tais métodos e chamar esta mesma função para realizar
     * uma execução remota agora específica para sua aplicação.
     * @param skill "SkillTemplate" que define a skill a ser executada.
     * @return O comportamento ("Behaviour") que conduz a execução remota.
     */
    public Behaviour newRemoteExecuteBehaviour(SkillTemplate skill){
        Agent requester = this;
        Behaviour cfp_execution_beh = new ContractNetInitiator(requester, null){
            int onEnd_result;   //resultado do comportamento de execução remota a ser retornado no método "onEnd()"
            @Override
            protected Vector prepareCfps(ACLMessage cfp) {
                Vector v = null;
                try {
                    v = servePrepareCfps(requester, cfp, skill);
                } catch (YPAException ex) {
                    Logger.getLogger(MRA.class.getName()).log(Level.SEVERE, null, ex);
                }
                return v;
            }
            @Override
            protected void handleAllResponses(Vector responses, Vector acceptances) {
                serveHandleAllResponses(responses, acceptances, requester.getLocalName());
            }
            @Override
            protected void handleInform(ACLMessage inform) {                
                onEnd_result = serveHandleInform(inform, requester.getLocalName());
            }
            @Override
            public int onEnd(){     //retorna o resultado do comportamento
                return onEnd_result;
            }
        };
        return cfp_execution_beh;
    }
    
    /**
     * Método servo que especifica como são preparados os CFPs a serem enviados
     * para os participantes. Solicita-se ao YPA uma busca pelos MRAs capazes de
     * executar tal skill. Em seguida, é criado um CFP para cada "MRAInfo" da 
     * lista e guardado num vetor para ser mandado de uma vez para os participantes.
     * Programadores podem sobrescrever este método para descrever uma forma 
     * específica para sua aplicação.
     * @param requester O agente a solicitar a execução da skill.
     * @param cfp cfp CFP inicialmente usado como base da iniciação da rede. Na classe,
     * por padrão, é um objeto "null", uma vez que o mesmo será criado somente nesta
     * função, com base na execução definida pela arquitetura EPSCore.
     * @param skill "SkillTemplate" que define a skill a ser executada.
     * @return Um vetor de CFPs que permite definições específicas para cada CFP
     * a ser enviado (porém, geralmente subusado aqui).
     * @throws YPAException
     */
    protected Vector servePrepareCfps(Agent requester, ACLMessage cfp, SkillTemplate skill) throws YPAException{
        System.out.println(requester.getLocalName() + ": Preparando cfp");
        System.out.println(requester.getLocalName() + ": Solicitando busca ao YPA");
        MRAInfo[] mrainfos = YPAServices.search(this, skill); 
        Vector v = new Vector();
        for (MRAInfo mrainfo : mrainfos){
            cfp = new ACLMessage(ACLMessage.CFP);
            cfp.setProtocol("fipa-contract-net");
            String executerName = mrainfo.getAID();
            cfp.addReceiver(new AID(executerName, AID.ISLOCALNAME));
            Execute exc = new Execute();
            exc.setMRAInfo(mrainfo);
            exc.setSkillTemplate(skill);

            try {
                cfp.setContentObject(exc);
            } catch (IOException ex) {
                Logger.getLogger(MRA.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            v.add(cfp);
        }
        System.out.println(requester.getLocalName() + ": Cfp preparado");
        return v;
    }
    
    /**
     * Método servo que especifica como são tratadas as respostas (PROPOSEs e
     * REFUSEs). Verifica-se qual o agente que enviou a melhor proposta (i.e. 
     * menor custo) e para esse é enviado uma aceitação de proposta. Os REFUSEs
     * são ignorados. Programadores podem sobrescrever este método para descrever
     * uma forma específica para sua aplicação.
     * @param responses Vetor de mensagens ACL que contém todas as respostas recebidas.
     * @param acceptances Vetor de mensagens ACL que contém todas as propostas recebidas.
     * @param requesterName Nome do agente a solicitar a skill.
     */
    protected void serveHandleAllResponses(Vector responses, Vector acceptances, String requesterName){
        ACLMessage bestPropose = null;
        int bestCost = 1000;        //infinito
        System.out.println(requesterName + ": Lendo proposes");
        for (int i = 0; i < responses.size(); ++i) {
            ACLMessage propose = (ACLMessage) responses.get(i);
            if (propose.getPerformative() == ACLMessage.PROPOSE) {
                int executer_cost = Integer.parseInt(propose.getContent());
                System.out.println(requesterName + ": Custo de " + executer_cost + " para o " + propose.getSender().getLocalName());
                if (bestPropose == null || executer_cost < bestCost) {
                    bestPropose = propose;
                    bestCost = executer_cost;
                }
            }
        }
        if (bestPropose != null) {
            ACLMessage accept = bestPropose.createReply();
            accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            accept.setContent("accept");
            acceptances.add(accept);
            System.out.println(requesterName + ": Accept enviado para " + bestPropose.getSender().getLocalName());
        }
    }
    
    /**
     * Método servo que especifica como são tratados os INFORMs recebidos.
     * Verifica-se o conteúdo do INFORM, que, por padrão, informa o resultado
     * da skill em "String" representando uma variável booleana. Além disso, este
     * método também é responsável por informar o resultado do comportamento de 
     * execução remota. Tal resultado é utilizado no método "onEnd()" (importante
     * para criar "FSMBehaviour"s). Programadores podem sobrescrever este método
     * para descrever uma forma específica para sua aplicação.
     * @param inform INFORM recebido pelo MRA.
     * @param requesterName Nome do agente a solicitar a skill.
     * @return Um inteiro que representa o resultado do comportamento de execução
     * remota (0 pra false e 1 para true).
     */
    protected int serveHandleInform(ACLMessage inform, String requesterName) {                
        switch (inform.getContent()) {
            case "true":
                System.out.println(inform.getSender().getLocalName() + ": Resultado da skill solicitada -> true");
                System.out.println(MRA.GREEN + inform.getSender().getLocalName() + ": Skill realizada com sucesso para o agente " + requesterName + RESET);
                return 1;
            case "false":
                System.out.println(inform.getSender().getLocalName() + ": Resultado da skill solicitada -> false");
                System.out.println(MRA.GREEN + inform.getSender().getLocalName() + ": Skill realizada com sucesso para o agente " + requesterName + RESET);
                return 0;
            default:
                return 2;
        }
    }
    
    protected void setupModbusClient() {
        this.modbusClient = new ModbusClient("127.0.0.1", 502);
        try
	{
            modbusClient.Connect();
            
	}
	catch (Exception e)
	{		
	}
    }
    
    protected void writeCoil(int coilAddress, boolean value) {
        try
	{
            this.modbusClient.WriteSingleCoil(coilAddress, value); //escreve no atuador do led
	}
	catch (Exception e)
	{		
	}
    }
    
    protected boolean readInput(int inputAddress) {
        boolean result = false;
        try
	{
            result = this.modbusClient.ReadDiscreteInputs(inputAddress, 1)[0]; //lê
	}
	catch (Exception e)
	{		
	}
        return result;
    }
}
