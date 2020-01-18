package application;

public class TemporaryWorker {

	private String userID;
	private String userFullName;
	private	int processID;
	private String role;
	
	public TemporaryWorker(String userID, String userFullName, int processID, String role)
	{
		this.userID = userID;
		this.userFullName = userFullName;
		this.processID = processID;
		this.role = role;
	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
