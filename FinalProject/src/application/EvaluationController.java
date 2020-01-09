package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EvaluationController implements Initializable{

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
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    }


    @FXML
    void submit_duetime_click(ActionEvent event) {

    }

    @FXML
    void submit_click(ActionEvent event) {

    }


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		constraints_textbox.setDisable(true);
		submit_btn.setDisable(true);
		result_textbox.setDisable(true);
		request_change_textbox.setDisable(true);
		
	}

}
