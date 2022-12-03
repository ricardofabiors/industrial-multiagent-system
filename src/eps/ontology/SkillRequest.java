
package eps.ontology;
import jade.content.AgentAction;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Fábio Ricardo
 */
public class SkillRequest implements AgentAction {
    private final Execute exc;
    private final ACLMessage reply;
    private final int key;
    
    public SkillRequest(Execute exc, ACLMessage reply, int key) {
        this.reply = reply;
        this.exc = exc;
        this.key = key;
    }
    
    public Execute getExecuteObj() {
        return exc;
    }

    public ACLMessage getReplyObj() {
        return reply;
    }
    
    public int getKey() {
        return key;
    }

}
