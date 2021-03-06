package behaviours;

import agents.OurAgent;
import helper.Logger;
import helper.State;
import jade.core.behaviours.OneShotBehaviour;

public class SendMessage extends OneShotBehaviour {

    private OurAgent agent;
    private State state;
    public SendMessage(OurAgent agent, State state) {
        this.agent = agent;
        this.state = state;
        super.setBehaviourName("SendMessage" + state.toString() + this.agent.getAID().getName());
    }

    @Override
    public void action() {
        Logger.print(this.agent.getLocalName(),
            this.agent.getName() + " SendMessage.action: " + state.toString());
        this.agent.sendMessage(this.state);
    }

    @Override
    public int onEnd(){
        this.reset();

        return 0;
    }
}