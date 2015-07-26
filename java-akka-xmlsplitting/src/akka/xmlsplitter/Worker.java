package akka.xmlsplitter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.xmlsplitter.pojo.StatusComplete;
import akka.xmlsplitter.pojo.Work;

public class Worker extends UntypedActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Work) {
			Work work = (Work) message;
			
			log.debug("Worker-"+work.getWorkerId()+" started...");
			saveToFile(work.getBankAccount(), work.getWorkerId());
			
			log.debug("Worker - "+work.getWorkerId()+" completed");
			sender().tell(new StatusComplete(work.getWorkerId()), getSelf());
			
		} else {
			unhandled(message);
		}
	}

	private void saveToFile(final StringBuilder bankAccount, final int workerId) throws IOException {
		Path path = Paths.get(".\\bin\\output"+workerId+".xml");
//		System.out.println("path : "+path.toAbsolutePath());
		
		Files.createFile(path);
		Files.write(path, bankAccount.toString().getBytes(), StandardOpenOption.APPEND);
	}
}