package akka.tutorial;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.tutorial.pojo.Calculate;

public class Pi {
	public static void main(String[] args) {
		Pi pi = new Pi();
	    pi.calculate(4, 10000, 10000);
	}
	
	public void calculate(final int nrOfWorkers, final int nrOfElements, final int nrOfMessages) {
		ActorSystem system = ActorSystem.create("PiSystem");
		
		ActorRef listener = system.actorOf(new Props(Listener.class), "listener");
		
		ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
			
			@Override
			public Actor create() {
				return new Master(nrOfWorkers, nrOfMessages, nrOfElements, listener);
			}
		}), "master");
		
		master.tell(new Calculate());
	}
}
