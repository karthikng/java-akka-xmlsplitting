package akka.xmlsplitter;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ErrorHandler extends UntypedActor {
	
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private final ActorRef child = getContext().actorOf(Props.empty(), "child");
	{
		this.getContext().watch(child);
	}
	
	ActorRef lastSender = getContext().system().deadLetters();
	
	@Override
	public void onReceive(Object message) throws Exception {
		
		if(message.equals("kill")) {
			
			log.debug("recieved kill message");
			getContext().stop(child);
			lastSender = getSender();
			
		} else if(message instanceof Terminated) {
			
			log.debug("recieved termination message");
			Terminated terminated = (Terminated) message;
			
			if(terminated.getActor() == child) {
				lastSender.tell("finished");
			}
			
		} else {
			unhandled(message);
		}
	}
}
