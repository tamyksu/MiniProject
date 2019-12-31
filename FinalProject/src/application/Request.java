package application;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Request implements Serializable {
	
	private String userID;
	private int informationSystemNumber;
	private String ProblemDescription;
	private String requestDescription;
	private String explanation;
	private String notes;
	private Date currentDate;
	
	
	/* ********************   Constructor   ******************** */
	public Request(String userId ,int informationSystemNumber, String problemDescription, String requestDescription,
			String explanation, String notes) {
		super();
		this.userID = userId;
		this.informationSystemNumber = informationSystemNumber;
		this.ProblemDescription = problemDescription;
		this.requestDescription = requestDescription;
		this.explanation = explanation;
		this.notes = notes;
	}


	/* ********************   Getters and Setters   ******************** */
	public int getInformationSystemNumber() {
		return informationSystemNumber;
	}


	public void setInformationSystemNumber(int informationSystemNumber) {
		this.informationSystemNumber = informationSystemNumber;
	}


	public String getProblemDescription() {
		return ProblemDescription;
	}


	public void setProblemDescription(String problemDescription) {
		ProblemDescription = problemDescription;
	}


	public String getRequestDescription() {
		return requestDescription;
	}


	public void setRequestDescription(String requestDescription) {
		this.requestDescription = requestDescription;
	}


	public String getExplanation() {
		return explanation;
	}


	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	public Date getCurrentDate() {
		return currentDate;
	}


	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	

	public String getUserID() {
		return userID;
	}


	public void setUserID(String userID) {
		this.userID = userID;
	}


	/* ********************   toString   ******************** */
	@Override
	public String toString() {
		return "Request [informationSystemNumber=" + informationSystemNumber + ", ProblemDescription="
				+ ProblemDescription + ", requestDescription=" + requestDescription + ", explanation=" + explanation
				+ ", notes=" + notes + ", currentDate=" + currentDate + "]";
	}
	
	
	
}
