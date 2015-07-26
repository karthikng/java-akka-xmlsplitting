package akka.xmlsplitter;

import java.io.IOException;
import java.util.Properties;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.xmlsplitter.constants.CommonConstants;
import akka.xmlsplitter.pojo.Improviser;
import akka.xmlsplitter.stax.StAXMaster;

public class XMLSplitter {
	
	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		properties.load(ClassLoader.getSystemResourceAsStream(CommonConstants.COMMON_PROPS));
		
		XMLSplitter xmlSplitter = new XMLSplitter();
		xmlSplitter.processTransactions(Integer.parseInt(properties.getProperty(CommonConstants.NUM_OF_WORKERS)));
	}

	private void processTransactions(int numberOfWorkers) throws IOException {
		System.out.println("Processing transactions ...");
		
		ActorSystem system = ActorSystem.create("XMLSplitting");
		
		ActorRef terminator = system.actorOf(new Props(Terminator.class), "terminator");
		
		ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Actor create() {
//				return new Master(numberOfWorkers, listener);
				return new StAXMaster(terminator, numberOfWorkers);
			}
		}), "improviser");
		
		master.tell(new Improviser());
	}
	
	/*private static StringBuilder readAllContentXML() {

		try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get("resource\\RelData.xml"))) {

			Node node = XML.load(bufferedReader);
			List<Node> tableInfos = (node.$bslash$bslash("tableInfo")).toList();
			scala.collection.Iterator<Node> tableInfoiterator = tableInfos.iterator();

			while(tableInfoiterator.hasNext()) {
				Node tableInfo = tableInfoiterator.next();
				System.out.println(tableInfo.$bslash("name").text());

			}


		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}*/
}