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
	
	/**
	 * Default Constructor
	 */
	public EvaluationReport() {
	}


	// ************************ Getters and Setters ************************
	
	/**
	 * Get the Requested Change
	 * @return the String of getRequestedChange
	 */
	public String getRequestedChange() {
		return requestedChange;
	}

	/**
	 * Get the Result
	 * @return String of 
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Get the Constraints And Risks
	 * @return String of getConstraitsAndRisks
	 */
	public String getConstraitsAndRisks() {
		return constraitsAndRisks;
	}

	/**
	 * Get the ProcessID
	 * @return processID
	 */
	public int getProcessID() {
		return processID;
	}

	/**
	 * Get the Appraiser ID
	 * @return appraiserID
	 */
	public String getAppraiserID() {
		return appraiserID;
	}

	
	// ************************ END of Getters and Setters ************************
}
