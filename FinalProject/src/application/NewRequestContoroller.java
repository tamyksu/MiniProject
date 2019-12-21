package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class NewRequestContoroller {

	Client client;
	
	
    @FXML
    private Button sendRequestBtn;

    @FXML
    private TextArea ProblemDescription;

    @FXML
    private TextArea notes;

    @FXML
    private TextArea informationSystemNumber;

    @FXML
    private TextArea Documents;

    @FXML
    private TextArea explanation;

    @FXML
    private TextArea requestDescription;

    @FXML
    void sendRequest(ActionEvent event) {
    	
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Date format
    	LocalDate localDate = LocalDate.now(); // Current Date
    	
    	ArrayList<String> newRequestArrayList = new ArrayList<String>();
    	newRequestArrayList.add("newRequest");
    	newRequestArrayList.add(informationSystemNumber.getText());
    	newRequestArrayList.add(ProblemDescription.getText());
    	newRequestArrayList.add(requestDescription.getText());
    	newRequestArrayList.add(explanation.getText());
    	newRequestArrayList.add(notes.getText());
    	newRequestArrayList.add(new String(dtf.format(localDate)));
    	
    	client.handleMessageFromClientGUI(newRequestArrayList);
    	
    	/*
    	 * 
    	 * 	Not Finished!!!
    	 * 	To be continued...
    	 * 
    	 * 
    	 */
    }

}
