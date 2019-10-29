package agents;

import behaviours.FindAgents;
import behaviours.Print;
import behaviours.StateMachine;
import behaviours.WaitForMessage;
import helper.Transition;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import market.Company;
import market.WalletExamples;

import javax.sound.midi.SysexMessage;

public class AgentManager extends OurAgent {

  // The companies that the manager has in it's wallet, mapped by title
  private Hashtable<String, Company> wallet;

  private AID board;

  private AID investor;
  // Put agent initializations here
  protected void setup() {
    // Create the catalogue
    wallet = WalletExamples.getEx1();

    // Register the manager service in the yellow pages
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType(String.valueOf(AgentType.MANAGER));
    sd.setName("wall-Street-manager_" + getAID().getName());
    dfd.addServices(sd);
    try {
      DFService.register(this, dfd);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }

    Behaviour printStart = new Print("Waiting for msg");

    Behaviour findBoard = new FindAgents(AgentType.BOARD, this);

    Behaviour waitInform = new WaitForMessage(this,
            MessageTemplate.MatchPerformative(ACLMessage.INFORM), 0);

    Behaviour waitAssignInvestor = new WaitForMessage(this,
            MessageTemplate.MatchConversationId("assign-investor"), 0);

    Behaviour printEnd = new Print("MSG Received");

    Transition t1 = new Transition(printStart, findBoard);

    Transition t2 = new Transition(findBoard, waitInform);

    Transition t3 = new Transition(waitInform, waitAssignInvestor);

    Transition t4 = new Transition(waitAssignInvestor, printEnd);


    StateMachine sm = new StateMachine(this, printStart, printEnd, t1, t2, t3, t4);
    addBehaviour(sm);
  }

  @Override
  public void handleMessage(ACLMessage msg) {
    if(msg.getConversationId().equalsIgnoreCase("assign-investor")){
      String name = "unknown";
      try {
        AID investor = (AID) msg.getContentObject();
        name = investor.getName();
        this.investor = investor;
      } catch (UnreadableException e) {
        e.printStackTrace();
      }
      System.out.println(getAID().getName() + "assign investor:  " + name);
    } else
    System.out.println(msg.getPerformative() + ": " + msg.getContent());
  }

  // Put agent clean-up operations here
  protected void takeDown() {
    // Deregister from the yellow pages
    try {
      DFService.deregister(this);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }

    // Printout a dismissal message
    System.out.println("Seller-agent " + getAID().getName() + " terminating.");
  }


  private class SellCompanies extends ContractNetInitiator {

    public SellCompanies(Agent a, ACLMessage cfp) {
      super(a, cfp);
    }

    protected void handlePropose(ACLMessage propose, Vector v) {
      System.out
          .println("Agent " + propose.getSender().getName() + " proposed " + propose.getContent());
    }

    protected void handleRefuse(ACLMessage refuse) {
      System.out.println("Agent " + refuse.getSender().getName() + " refused");
    }

    protected void handleFailure(ACLMessage failure) {
      if (failure.getSender().equals(myAgent.getAMS())) {
        // FAILURE notification from the JADE runtime: the receiver
        // does not exist
        System.out.println("Responder does not exist");
      } else {
        System.out.println("Agent " + failure.getSender().getName() + " failed");
      }
    }

    protected void handleAllResponses(Vector responses, Vector acceptances) {

      // Evaluate proposals.
      int bestProposal = -1;
      AID bestProposer = null;
      ACLMessage accept = null;
      Enumeration e = responses.elements();
      while (e.hasMoreElements()) {
        ACLMessage msg = (ACLMessage) e.nextElement();
        if (msg.getPerformative() == ACLMessage.PROPOSE) {
          ACLMessage reply = msg.createReply();
          reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
          acceptances.addElement(reply);
          int proposal = Integer.parseInt(msg.getContent());
          if (proposal > bestProposal) {
            bestProposal = proposal;
            bestProposer = msg.getSender();
            accept = reply;
          }
        }
      }
      // Accept the proposal of the best proposer
      if (accept != null) {
        System.out.println(
            "Accepting proposal " + bestProposal + " from responder " + bestProposer.getName());
        accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
      }
    }

    protected void handleInform(ACLMessage inform) {
      System.out.println(
          "Agent " + inform.getSender().getName() + " successfully performed the requested action");
    }
  }

  @Override
  public void registerAgent(AID[] agents, AgentType type) {
    switch (type){
      case BOARD:
        try {
          this.board = agents[0];
          System.out.println("THIS IS MY BOARD " + this.board);
        }
        catch(Exception e){
          System.err.println(e);
        }
        break;
      default:
        System.err.println("Invalid agent type");
        break;
    }
  }

}
