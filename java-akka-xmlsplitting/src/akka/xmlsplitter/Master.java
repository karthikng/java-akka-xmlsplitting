package akka.xmlsplitter;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import scala.collection.immutable.List;
import scala.xml.Node;
import scala.xml.XML;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinRouter;
import akka.xmlsplitter.pojo.Improviser;
import akka.xmlsplitter.pojo.StatusComplete;
import akka.xmlsplitter.pojo.TerminationRequest;
import akka.xmlsplitter.pojo.Work;

public class Master extends UntypedActor {
	
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private final int numberOfWorkers;
	
	private final ActorRef listener;
	
	private int returnedWorkersCount = 0;
	
	private final long start = System.currentTimeMillis();
	
	public Master(int numberOfWorkers, ActorRef listener) {
		this.numberOfWorkers = numberOfWorkers;
		this.listener = listener;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Improviser) {
			ActorRef worker =  getContext().actorOf(
					new Props(Worker.class).withRouter(new RoundRobinRouter(numberOfWorkers)), "worker");
			
			
			final String sourceFileName = "resource\\bdf-recociliation-bank.xml";
			final Path sourceFilePath = Paths.get(sourceFileName);
			
			try (final BufferedReader bufferedReader = Files.newBufferedReader(sourceFilePath)) {
				
				Node node = XML.load(bufferedReader);
				List<Node> bankAccounts = (node.$bslash$bslash("bankAccount")).toList();
				scala.collection.Iterator<Node> iterator = bankAccounts.iterator();
				 
				
				int divisionFactor = bankAccounts.length() / numberOfWorkers;
				int workerId = 1;
				java.util.List<Node> bankAccountList = new ArrayList<>();
				boolean isAllTaskSentToWorker = false;
				
				System.out.println("Time taken for loading xml : "+(System.currentTimeMillis() - start)/1000+" sec");
				while(iterator.hasNext()) {
					Node bankAccount = iterator.next();
					
					bankAccountList.add(bankAccount);
					isAllTaskSentToWorker = false;
							
					if(bankAccountList.size() > divisionFactor) {
						worker.tell(new Work(getBankAccountAsString(bankAccountList), workerId++), getSelf());
						bankAccountList = new ArrayList<>();
						isAllTaskSentToWorker = true;
					}
				}
				
				//Sending the last batch of XML details
				if(!isAllTaskSentToWorker) {
					worker.tell(new Work(getBankAccountAsString(bankAccountList), workerId++), getSelf());
				}
				
			}
			
		} else if (message instanceof StatusComplete) {
			
			StatusComplete complete = (StatusComplete) message;
			System.out.println("Worker - "+complete.getWorkerId()+" returned");
			
			returnedWorkersCount++;
			if(returnedWorkersCount == numberOfWorkers) {
				System.out.println("Total Time : "+(System.currentTimeMillis() - start)/1000+" sec");
				listener.tell(new TerminationRequest(), getSelf());
				getContext().stop(getSelf());
			}
			
		} else {
			unhandled(message);
		}
	}

	private StringBuilder getBankAccountAsString(java.util.List<Node> bankAccountList) {
		StringBuilder bankAccountString = new StringBuilder();
		for (Node node : bankAccountList) {
			bankAccountString.append(node.toString());
		}
		return bankAccountString;
	}
}
