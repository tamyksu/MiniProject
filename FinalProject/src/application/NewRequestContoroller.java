package application;


import java.io.IOException;
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
import javafx.scene.control.TextField;

public class NewRequestContoroller {

	
	
	
    @FXML
    private Button sendRequestBtn;

    @FXML
    private TextArea ProblemDescription;

    @FXML
    private TextArea notes;

    @FXML
    private TextField informationSystemNumber;

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
    	
    	int sysNum = Integer.parseInt(informationSystemNumber.getText().toString());
    	String probDesc = ProblemDescription.getText().toString();
    	String reqDesc = requestDescription.getText().toString();
    	String expla = explanation.getText().toString();
    	String notes1 = notes.getText().toString();
    	
    	Request nr = new Request(sysNum, probDesc, reqDesc, expla, notes1);
    	System.out.println(nr.toString());
    	
    	try {
			Client.instance.sendToServer(nr);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

}
