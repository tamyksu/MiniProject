package application;
	import java.util.ArrayList;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
	import javafx.scene.control.TableColumn;
	import javafx.scene.control.TableView;
	import javafx.scene.control.TextArea;


public class ControlerProcessMain {


    @FXML
    private Button newRequestBtn;

    @FXML
    void initizlizeTable(ActionEvent event) {
    	ArrayList<String> query = new ArrayList<String>();
    	query.add("get all relaqueryted requests");
    	query.add(Client.getInstance().getName());
    	Client.getInstance().handleMessageFromClientGUI(query);
    }

    @FXML
    void newRequest(ActionEvent event) {
    	ScreenController.getScreenController().activate("newRequestMain");
    }


}
