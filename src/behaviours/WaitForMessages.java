package behaviours;

import agents.OurAgent;
import helper.State;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WaitForMessages extends SimpleBehaviour {

    private OurAgent agent;
    private int type;
    private int done;


    public WaitForMessages(OurAgent agent, int type) {
        this.agent = agent;
        this.type = type;
        this.done = -1;
        super.setBehaviourName("WaitMultipleMessage_" + this.agent.getName());
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(type);
        ACLMessage msg = this.agent.receive(mt);
        if(msg!=null){
            this.done = this.agent.handleMessage(msg);
            System.out.println(agent.getName() + " handling message: " + msg.getConversationId() + " done: " + this.done);
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        System.out.println(agent.getName() + " done: " + this.done);

        return (this.done != -1);
    }

    @Override
    public int onEnd() {
        return this.done;
    }
}
