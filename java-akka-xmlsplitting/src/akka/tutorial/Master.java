package akka.tutorial;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import akka.tutorial.pojo.Calculate;
import akka.tutorial.pojo.PiApproximation;
import akka.tutorial.pojo.Result;
import akka.tutorial.pojo.Work;
import akka.util.Duration;

public class Master extends UntypedActor {
	
	private final int nrOfMessages;
	private final int nrOfElements;
	
	private final ActorRef worker;
	private final ActorRef listener;
	
	private double pi;
	private int nrOfResults;
	private final long start = System.currentTimeMillis();
	
	public Master(final int nrOfWorkers, final int nrOfMessages, final int nrOfElements, ActorRef listener) {
		this.nrOfMessages = nrOfMessages;
		this.nrOfElements = nrOfElements;
		this.listener = listener;
		
		worker = this.getContext().actorOf(new Props(Worker.class).withRouter(new RoundRobinRouter(nrOfWorkers)), "worker");
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Calculate) {
			for (int start = 0; start < nrOfMessages; start++) {
				worker.tell(new Work(start, nrOfElements), getSelf());
			}
		} else if (message instanceof Result) {
		    Result result = (Result) message;
		    pi += result.getValue();
		    nrOfResults += 1;
		    if (nrOfResults == nrOfMessages) {
		      // Send the result to the listener
		      Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
		      listener.tell(new PiApproximation(pi, duration), getSelf());
		      // Stops this actor and all its supervised children
		      getContext().stop(getSelf());
		    }
		  } else {
		    unhandled(message);
		  }
	}
}
