package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ExaminationController {

    @FXML
    private Button examination_completed_btn;

    @FXML
    private Button fill_failure_report_btn;

    @FXML
    private TextArea failure_explanation;

    @FXML
    private Button back_btn;

    @FXML
    private TextField request_id_textbox;

    @FXML
    private Button submit_failure_report_btn;


    @FXML
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    }

    @FXML
    void submit_failure_report_click(ActionEvent event) {

    }

    @FXML
    void fill_failure_report_click(ActionEvent event) {

    }


    @FXML
    void examination_completed_click(ActionEvent event) {

    }

}
