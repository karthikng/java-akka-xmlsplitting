package akka.tutorial;

import akka.actor.UntypedActor;
import akka.tutorial.pojo.Result;
import akka.tutorial.pojo.Work;

public class Worker extends UntypedActor {
	
	
	public void onReceive(Object message) {
	    if (message instanceof Work) {
	      Work work = (Work) message;
	      double result = calculatePiFor(work.getStart(), work.getNrOfElements());
	      getSender().tell(new Result(result), getSelf());
	    } else {
	      unhandled(message);
	    }
	  }

	private double calculatePiFor(int start, int nrOfElements) {
		double res = 0.0;
		for(int i=start * nrOfElements ; i < ((start + 1) * nrOfElements - 1); i++) {
			res += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
		}
		return res;
	}
}
