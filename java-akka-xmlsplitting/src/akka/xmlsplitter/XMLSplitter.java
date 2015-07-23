package akka.xmlsplitter;

import java.io.IOException;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.xmlsplitter.pojo.Improviser;

public class XMLSplitter {
	
	public static void main(String[] args) throws IOException {
		XMLSplitter xmlSplitter = new XMLSplitter();
		xmlSplitter.processTransactions(100);
	}

	private void processTransactions(int numberOfWorkers) throws IOException {
		System.out.println("Processing transactions ...");
		
		ActorSystem system = ActorSystem.create("XMLSplitting");
		
		ActorRef listener = system.actorOf(new Props(Terminator.class), "listener");
		
		ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Actor create() {
				return new Master(numberOfWorkers, listener);
			}
		}), "improviser");
		
		master.tell(new Improviser());
	}
	
	/*private static StringBuilder readAllContentXML() {
		
		try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get("resource\\AllContentDesc.xml"))) {
			
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