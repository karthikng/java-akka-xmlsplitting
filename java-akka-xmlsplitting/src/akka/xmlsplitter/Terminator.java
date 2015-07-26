package akka.xmlsplitter;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.xmlsplitter.pojo.TerminationRequest;

public class Terminator extends UntypedActor{
	
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof TerminationRequest) {
			log.debug("XML splitting completed...");
			getContext().system().shutdown();
		} else {
			unhandled(message);
		}
	}
}
