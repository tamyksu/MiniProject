package translator;

public enum OptionsOfAction {

	checkNAMEParmenent,
	INITIALIZE_COMBO_BOX,
	SELECTCHAIRMAN,
	UPDATEPERMANENT,
	DELETEPERMANENT,
	checkDB,
	CURRENT_IN_ROLE,
	LOGIN, 
	GETRELATEDREQUESTS,
	NEWREQUEST,
	GET_APPRAISER_AND_PERFORMANCE_LEADER_CB_DATA,
	APPOINT_APPRAISER_OR_PERFORMANCE_LEADER,
	GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC,
	SET_EVALUATION_OR_EXECUTION_DUE_TIME,
	ADD_EVALUATION_OR_EXECUTION_EXTENSION_TIME,
	GETALLPROCESSES,
	GETALLINFORMATIONSYSTEMS,
	FREEZE_PROCESS,
	DEFROST_PROCESS,
	INSERT_FAILURE_REPORT,
	SHUTDOWN_PROCESS, 
	DOWNLOADFILE,
	EXAMINATION_COMPLETED,
	FILL_FAILURE_REPORT_CLICK,
	Fill_Evalution_Number_Of_Days,
	Fill_Evalution_Form,
	REJECTE_PROCESS,
	Get_Active_Statistic,
	Get_Evaluation_Report_For_Process_ID,
	Approve_Decision,
	More_Info_Decision,
	Execution_Suggest_Number_Of_Days,
	Execution_Completed,
	GET_RELATED_MESSAGES,
	DECLINE_EVALUATION_OR_EXECUTION_DUE_TIME,
	DECLINE_EVALUATION_OR_EXECUTION_EXTENSION_TIME,
	RECOVER_PASSWORD,
	SEND_EXTENSION_REQUEST,
	Get_All_Change_Board_Members,
	Appoint_Examiner,
	SelectDelayReport,
	SelectExtensionReport;
}



/* GETALLINFORMATIONSYSTEMS - is an option that is currently NOT in use.
 * If it will be in use, it will be for the NewRequestController.
 * Maybe if we would like to get all the information Systems from the database.
 */
