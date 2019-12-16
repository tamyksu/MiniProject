package application;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import application.Controller;
import client.ChatClient;

public class LoginController {

    @FXML
    private TextField serverIP;

    @FXML
    private Button conectButton;


	public void start(Stage primaryStage) throws Exception {	
		Parent root = FXMLLoader.load(getClass().getResource("/gui/AcademicFrame.fxml"));
				
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AcademicFrame.css").toExternalForm());
		primaryStage.setTitle("Academic Managment Tool");
		primaryStage.setScene(scene);
		
		primaryStage.show();		
	}
	
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("exit Academic Tool");
		//System.exit(0);			
	}
	
    
    
	public void setServerID(ActionEvent event) throws IOException {
		
		Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("/application/GUI.fxml").openStream());
		Controller controller = loader.getController();
		controller.setIp(serverIP.getText());
		
		try {
			controller.initialize(serverIP.getText());
			
			((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
			Scene scene = new Scene(root);			
			scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
			
			primaryStage.setScene(scene);		
			primaryStage.show();
		}
		catch(UnknownHostException ex)
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText("Unknown Host!");
			alert.showAndWait();
		}
		catch(ConnectException ex)
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText("Could not connect to this IP");
			alert.showAndWait();
		}

		

	}
	
	
}
