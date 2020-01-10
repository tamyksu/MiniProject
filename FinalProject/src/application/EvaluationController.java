package application;

import java.net.URL;
import java.util.ResourceBundle;
import client.Client;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import translator.OptionsOfAction;
import translator.Translator;
import javafx.scene.control.Alert.AlertType;

public class EvaluationController implements Initializable{
	
	public static EvaluationController instance;

    @FXML
    private TextArea constraints_textbox;

    @FXML
    private Button back_btn;

    @FXML
    private TextField days_textbox;

    @FXML
    private TextArea request_change_textbox;

    @FXML
    private Button submit_duetime_btn;

    @FXML
    private Button submit_btn;

    @FXML
    private TextArea result_textbox;
    
    @FXML
    private Label initiator_name_text;

    @FXML
    private Label initiator_email_text;

    @FXML
    private Label initiator_role_text;

    @FXML
    private Label information_system_text;

    @FXML
    private Label current_stage_text;

    @FXML
    private Label requested_change_text;

    @FXML
    private Label explanation_text;

    @FXML
    private Label notes_text;

    @FXML
    private Label request_date_text;

    @FXML
    private Label request_id_text;

    @FXML
    private Label documents_text;

    @FXML
    private Label status_text;

    @FXML
    private Label current_stage_due_time_text;
    
    @FXML
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    }


    @FXML
    void submit_duetime_click(ActionEvent event) {
    	
    	if(days_textbox.getText().trim().isEmpty() || !NewRequestController.isNumeric(days_textbox.getText())) {
    		new Alert(AlertType.ERROR, "You must fill all the details!").show();
    	}
    	else {
    		ArrayList<Integer> arr = new ArrayList<>();
    		arr.add(Integer.parseInt(days_textbox.getText()));
    		Translator translator = new Translator(OptionsOfAction.Fill_Evalution_Number_Of_Days, arr);
    		Client.getInstance().handleMessageFromClientGUI(translator);
    	}
    }

    @FXML
    void submit_click(ActionEvent event) {

    }
    
    public void updateProcessInformation()
    {
		UserProcess process = Client.getInstance().getProcesses().getMyProcess().get(Integer.parseInt(ControllerProcessMain.getInstance().getRequestID()));
		initiator_name_text.setText(process.getIntiatorId());
		initiator_email_text.setText(process.getEmail());
		initiator_role_text.setText(process.getRole());
		information_system_text.setText("" + process.getSystem_num());
		current_stage_text.setText(process.getProcess_stage());
		requested_change_text.setText(process.getRequest_description());
		explanation_text.setText(process.getExplanaton());
		notes_text.setText(process.getNotes());
		request_date_text.setText(process.getCreation_date());
		request_id_text.setText("" + process.getRequest_id());
		status_text.setText(process.getStatus());
		current_stage_due_time_text.setText(process.getCurrent_stage_due_date());
    }


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		constraints_textbox.setDisable(true);
		submit_btn.setDisable(true);
		result_textbox.setDisable(true);
		request_change_textbox.setDisable(true);
		
	}

}
