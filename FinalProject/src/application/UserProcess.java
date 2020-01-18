package application;

import java.util.ArrayList;

/**
 * Class that represents the information of a User Process with all of its details
 */
public class UserProcess {
	private int    request_id;
	private int    system_num;
	private String role;
	private String intiatorId;
	private String problem_description;
	private String request_description;
	private String explanaton;
	private String notes;
	private String status;
	private String creation_date;
	private String handler_id;
	private String process_stage;
	private String current_stage_due_date;
	private String department;
	private String email;
	private String initiatorFirstName;
	private String initiatorLastName;
	private ArrayList<String> relatedDocuments;
	
	public ArrayList<String> getRelatedDocuments() {
		return relatedDocuments;
	}
	public void setRelatedDocuments(ArrayList<String> arrayList) {
		this.relatedDocuments = arrayList;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getIntiatorId() {
		return intiatorId;
	}
	public void setIntiatorId(String intiatorId) {
		this.intiatorId = intiatorId;
	}
	public int getSystem_num() {
		return system_num;
	}
	public void setSystem_num(int system_num) {
		this.system_num = system_num;
	}
	public String getProblem_description() {
		return problem_description;
	}
	public void setProblem_description(String problem_description) {
		this.problem_description = problem_description;
	}
	public String getRequest_description() {
		return request_description;
	}
	public void setRequest_description(String request_description) {
		this.request_description = request_description;
	}
	public String getExplanaton() {
		return explanaton;
	}
	public void setExplanaton(String explanaton) {
		this.explanaton = explanaton;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreation_date() {
		return creation_date;
	}
	public void setCreation_date(String creation_date) {
		this.creation_date = creation_date;
	}
	public String getHandler_id() {
		return handler_id;
	}
	public void setHandler_id(String handler_id) {
		this.handler_id = handler_id;
	}
	public String getProcess_stage() {
		return process_stage;
	}
	public void setProcess_stage(String process_stage) {
		this.process_stage = process_stage;
	}
	public String getCurrent_stage_due_date() {
		return current_stage_due_date;
	}
	public void setCurrent_stage_due_date(String current_stage_due_date) {
		this.current_stage_due_date = current_stage_due_date;
	}

	public void setRequest_id(int request_id) {
		this.request_id = request_id;
	}
	public int getRequest_id() {
		return request_id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getInitiatorFirstName() {
		return initiatorFirstName;
	}
	public void setInitiatorFirstName(String initiatorFirstName) {
		this.initiatorFirstName = initiatorFirstName;
	}
	public String getInitiatorLastName() {
		return initiatorLastName;
	}
	public void setInitiatorLastName(String initiatorLastName) {
		this.initiatorLastName = initiatorLastName;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	

}
