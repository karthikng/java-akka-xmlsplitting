package akka.xmlsplitter;

import akka.actor.UntypedActor;
import akka.xmlsplitter.pojo.TerminationRequest;

public class Terminator extends UntypedActor{
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof TerminationRequest) {
			System.out.println("XML splitting completed...");
			getContext().system().shutdown();
		} else {
			unhandled(message);
		}
	}
}
