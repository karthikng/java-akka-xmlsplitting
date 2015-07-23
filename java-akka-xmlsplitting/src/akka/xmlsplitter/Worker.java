package akka.xmlsplitter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import scala.xml.Node;
import akka.actor.UntypedActor;
import akka.xmlsplitter.pojo.StatusComplete;
import akka.xmlsplitter.pojo.Work;

public class Worker extends UntypedActor {

	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Work) {
			Work work = (Work) message;
			saveToFile(work.getBankAccounts(), work.getWorkerId());
			System.out.println("Worker - "+work.getWorkerId()+" completed");
			sender().tell(new StatusComplete(work.getWorkerId()), getSelf());
		} else {
			unhandled(message);
		}
	}

	private void saveToFile(final List<Node> bankAccounts, final int workerId) throws IOException {
		Path path = Paths.get("..\\..\\Canbedeleted\\xmlsamples\\output"+workerId+".xml");
//		System.out.println("path : "+path.toAbsolutePath());
		
		Files.createFile(path);
		for (Node bankAccount : bankAccounts) {
			Files.write(path, bankAccount.toString().getBytes(), StandardOpenOption.APPEND);
		}
		bankAccounts.clear();
	}
}