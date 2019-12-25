package application;
public class Person {
	
	private int requestId;
	private int informationSystem;
	private String status;
	private String creation_date;
	private String role;



	public Person(int requestId, int informationSystem, String status, String creation_date, String role) {
		this.requestId=requestId;
		this.informationSystem=informationSystem;
		this.status=status;
		this.creation_date=creation_date;
		this.role=role;
		}


	public int getRequestId() {
		return requestId;
	}


	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}


	public Integer getInformationSystem() {
		return informationSystem;
	}


	public void setInformationSystem(int informationSystem) {
		this.informationSystem = informationSystem;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getDate() {
		return creation_date;
	}


	public void setDate(String date) {
		this.creation_date = date;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}

}
