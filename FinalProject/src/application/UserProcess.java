package application;

import java.sql.Date;
//
public class UserProcess {
	
	private String requestId;
	private String role;
	private String intiatorId;
	private int    system_num;
	private String problem_description;
	private String request_description;
	private String explanaton;
	private String notes;
	private String status;
	private Date creation_date;
	private String handler_id;
	private String process_stage;
	private String current_stage_due_date;
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
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
	public Date getCreation_date() {
		return creation_date;
	}
	public void setCreation_date(Date creation_date) {
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



}
