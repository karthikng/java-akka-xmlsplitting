package akka.tutorial.experiments.staxparsing;

import java.io.IOException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class StaxParserDemo {
	
	private final String ELEMENT_BANK_ACCOUNT = "bankAccount";
	
	public static void main(String[] args) throws Exception {
		StaxParserDemo demo = new StaxParserDemo();
		demo.processXML();
	}
	
	private void processXML() throws XMLStreamException, IOException {
		StringBuilder nodeData = null;
		boolean isElementParsingInProcess = false;
		
		long start = System.currentTimeMillis();
		
		XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
		XMLEventReader reader = xmlInputFactory.createXMLEventReader(
				ClassLoader.getSystemResourceAsStream("resource/bdf-recociliation-bank.xml"));
		
		while(reader.hasNext()) {
			XMLEvent event = reader.nextEvent();

			switch (event.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				
				if(isElementParsingInProcess) {
					nodeData.append(event.toString().trim());
				} else if(event.asStartElement().getName().getLocalPart().equals(ELEMENT_BANK_ACCOUNT)) {
					isElementParsingInProcess = true;
					nodeData = new StringBuilder();
					nodeData.append(event.toString().trim());
				}
				break;

			case XMLStreamConstants.CHARACTERS:
				if(isElementParsingInProcess) {
					nodeData.append(event.toString().trim());
				}
				break;
				
			case XMLStreamConstants.END_ELEMENT:
				
				if(isElementParsingInProcess) {
					nodeData.append(event.toString().trim());
				}
				if(event.asEndElement().getName().getLocalPart().equals(ELEMENT_BANK_ACCOUNT)) {
					isElementParsingInProcess = false;
//					System.out.println("nodeData : "+nodeData);
					nodeData = null;
				}
				break;
			default:
				break;
			}
		}
		System.out.println("Time taken : "+(System.currentTimeMillis() - start));
	}
}
