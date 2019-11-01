package agents;

import helper.State;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public abstract class OurAgent extends Agent {

  public abstract void handleMessage(ACLMessage msg);
  public abstract void registerAgent(AID[] agents, AgentType type);
  public abstract void sendMessage(State type);
  public abstract int onEnd(State state);

}
