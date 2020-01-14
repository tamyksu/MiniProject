package application;

public class EvaluationReport {
	private int processID;
	private String appraiserID;
	private String requestedChange;
	private String result;
	private String constraitsAndRisks;
	
	/**
	 * Constructor
	 * @param requestedChange
	 * @param result
	 * @param constraitsAndRisks
	 */
	public EvaluationReport(int processID,  String appraiserID, String requestedChange,
			String result, String constraitsAndRisks) {
		this.processID = processID;
		this.appraiserID = appraiserID;
		this.requestedChange = requestedChange;
		this.result = result;
		this.constraitsAndRisks = constraitsAndRisks;
	}
	
	
	/**
	 * Copy Constructor
	 * @param copy
	 */
	public EvaluationReport(EvaluationReport copy) {
		this.requestedChange = copy.getRequestedChange();
		this.result = copy.getResult();
		this.constraitsAndRisks = copy.getConstraitsAndRisks();
	}
	
	
	// ************************ Getters and Setters ************************
	public String getRequestedChange() {
		return requestedChange;
	}

	public void setRequestedChange(String requestedChange) {
		this.requestedChange = requestedChange;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getConstraitsAndRisks() {
		return constraitsAndRisks;
	}

	public void setConstraitsAndRisks(String constraitsAndRisks) {
		this.constraitsAndRisks = constraitsAndRisks;
	}


	public int getProcessID() {
		return processID;
	}


	public void setProcessID(int processID) {
		this.processID = processID;
	}


	public String getAppraiserID() {
		return appraiserID;
	}


	public void setAppraiserID(String appraiserID) {
		this.appraiserID = appraiserID;
	}
	
	// ************************ END of Getters and Setters ************************
}
