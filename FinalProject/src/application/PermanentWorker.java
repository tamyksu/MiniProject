package application;

/**
 * class for the workers in the icmdb.workers table
 *
 */
public class PermanentWorker {

	private String userID;
	private String userFullName;
	private String role;
	
	PermanentWorker(String userID, String userFullName, String role)
	{
		this.userID = userID;
		this.userFullName = userFullName;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
}
