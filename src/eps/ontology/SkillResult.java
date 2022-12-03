
package eps.ontology;
import jade.content.AgentAction;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Fábio Ricardo
 */
public class SkillResult implements AgentAction {
    private final int key;
    private final ACLMessage reply;
    
    public SkillResult(int key, ACLMessage reply) {
        this.reply = reply;
        this.key = key;
    }
    
    public int getKey() {
        return key;
    }

    public ACLMessage getReplyPbj() {
        return reply;
    }
   

}
