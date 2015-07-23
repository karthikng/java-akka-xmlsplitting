package akka.xmlsplitter.pojo;

import java.util.List;

import scala.xml.Node;

public class Work {
	private final List<Node> bankAccounts;
	
	private final int workerId;
	
	public Work(List<Node> bankAccount, int workerId) {
		this.bankAccounts = bankAccount;
		this.workerId = workerId;
		
	}

	public List<Node> getBankAccounts() {
		return bankAccounts;
	}
	
	public int getWorkerId() {
		return workerId;
	}
}
