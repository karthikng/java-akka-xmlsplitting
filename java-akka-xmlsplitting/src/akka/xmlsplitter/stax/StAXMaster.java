package akka.xmlsplitter.stax;

import java.util.Properties;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.XMLEvent;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinRouter;
import akka.xmlsplitter.Worker;
import akka.xmlsplitter.constants.CommonConstants;
import akka.xmlsplitter.pojo.Improviser;
import akka.xmlsplitter.pojo.TerminationRequest;
import akka.xmlsplitter.pojo.Work;


public class StAXMaster extends UntypedActor {
	
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	final ActorRef terminator;
	
	final int numberOfWorkers;
	
	final long start;
	
	public StAXMaster(ActorRef terminator, int numberOfWorkers) {
		this.terminator = terminator;
		this.numberOfWorkers = numberOfWorkers;
		
		start = System.currentTimeMillis();
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		
		ActorRef worker = getContext().actorOf(
				new Props(Worker.class).withRouter(new RoundRobinRouter(numberOfWorkers)), "staxworker");
		
		if(message instanceof Improviser) {
			
			boolean isElementParsingInProcess = false;
			StringBuilder bankAccount = null;
			
			XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
			
			Properties properties = new Properties();
			properties.load(ClassLoader.getSystemResourceAsStream(CommonConstants.COMMON_PROPS));
			
			XMLEventReader reader = xmlInputFactory.createXMLEventReader(
					ClassLoader.getSystemResourceAsStream(properties.getProperty(CommonConstants.XML_TO_READ)));
			
			int workerId = 0;
			while(reader.hasNext()) {
				XMLEvent event = reader.nextEvent();

				switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					
					if(isElementParsingInProcess) {
						bankAccount.append(event.toString().trim());
					} else if(event.asStartElement().getName().getLocalPart().equals(CommonConstants.ELEMENT_BANK_ACCOUNT)) {
						isElementParsingInProcess = true;
						bankAccount = new StringBuilder();
						bankAccount.append(event.toString().trim());
						workerId++;
					}
					break;

				case XMLStreamConstants.CHARACTERS:
					if(isElementParsingInProcess) {
						bankAccount.append(event.toString().trim());
					}
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					
					if(isElementParsingInProcess) {
						bankAccount.append(event.toString().trim());
					}
					if(event.asEndElement().getName().getLocalPart().equals(CommonConstants.ELEMENT_BANK_ACCOUNT)) {
						isElementParsingInProcess = false;
						
						//Calling the worker with each of the bankAccount
						worker.tell(new Work(bankAccount, workerId), getSelf());
						
						bankAccount = null;
					}
					break;
				default:
					break;
				}
			}
			
			System.out.println("Time taken : "+(System.currentTimeMillis() - start));
			
			//Stopping the master before shutting down the system 
			getContext().stop(getSelf());
			
			//calling terminator to finally shutdown the system
			terminator.tell(new TerminationRequest());
			
		} else {
			unhandled(message);
		}
	}
}