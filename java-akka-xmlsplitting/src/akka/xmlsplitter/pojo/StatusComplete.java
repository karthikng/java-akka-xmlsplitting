package akka.xmlsplitter.pojo;

public class StatusComplete {
	private final int workerId;
	
	public StatusComplete(int workerId) {
		this.workerId = workerId;
	}
	
	public int getWorkerId() {
		return workerId;
	}
}
