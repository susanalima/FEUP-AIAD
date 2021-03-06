package behaviours;

import agents.OurAgent;
import helper.Logger;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WaitForMessages extends SimpleBehaviour {

    private OurAgent agent;
    private int type;
    private int done;

    private final static int INITIAL_DONE = -1;


    public WaitForMessages(OurAgent agent, int type) {
        this.agent = agent;
        this.type = type;
        this.done = INITIAL_DONE;
        super.setBehaviourName("WaitMultipleMessage_" + this.agent.getName());
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(type);
        ACLMessage msg = this.agent.receive(mt);
        if(msg!=null){
            this.done = this.agent.handleMessage(msg);
            Logger.print(this.agent.getLocalName(),
                agent.getName() + " handling message: " + msg.getConversationId() + " done: "
                    + this.done);
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        Logger.print(this.agent.getLocalName(), agent.getLocalName() + " done: " + this.done);

        return (this.done != -1);
    }

    @Override
    public int onEnd() {
        int done = this.done;
        Logger.print(this.agent.getLocalName(), agent.getLocalName() + " wfm: " + done);
        this.done = INITIAL_DONE;
        this.reset();

        return done;
    }
}
