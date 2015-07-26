package akka.xmlsplitter.pojo;


public class Work {
	private final StringBuilder bankAccount;
	
	private final int workerId;
	
	public Work(final StringBuilder bankAccount, int workerId) {
		this.bankAccount = bankAccount;
		this.workerId = workerId;
		
	}

	public StringBuilder getBankAccount() {
		return bankAccount;
	}
	
	public int getWorkerId() {
		return workerId;
	}
}
