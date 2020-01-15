package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent root = FXMLLoader.load(getClass().getResource("/application/basePanel.fxml"));
			Scene baseScene = new Scene(root, 1500,1500);
			baseScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(baseScene);
			primaryStage.show();
		
			initializeScreenController(baseScene);
			ScreenController.getScreenController().activate("login");
	
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
		System.exit(0);
	}
	
	public void initializeScreenController(Scene baseScene)
	{
		new ScreenController(baseScene);

		try {
			ScreenController.getScreenController().addScreen("login", FXMLLoader.load(getClass().getResource("/application/Login.fxml")));
			ScreenController.getScreenController().addScreen("processesMain", FXMLLoader.load(getClass().getResource("/application/ProcessesMain.fxml")));
			ScreenController.getScreenController().addScreen("evaluation", FXMLLoader.load(getClass().getResource("/application/Evaluation.fxml")));
			ScreenController.getScreenController().addScreen("decisionMaking", FXMLLoader.load(getClass().getResource("/application/DecisionMaking.fxml")));
			ScreenController.getScreenController().addScreen("execution", FXMLLoader.load(getClass().getResource("/application/Execution.fxml")));
			ScreenController.getScreenController().addScreen("examination", FXMLLoader.load(getClass().getResource("/application/Examination.fxml")));
			ScreenController.getScreenController().addScreen("newRequest", FXMLLoader.load(getClass().getResource("/application/NewRequestForm.fxml")));
			ScreenController.getScreenController().addScreen("staffMain", FXMLLoader.load(getClass().getResource("/application/StaffMain.fxml")));
			ScreenController.getScreenController().addScreen("supervisor_processesMain", FXMLLoader.load(getClass().getResource("/application/Supervisor_ProcessesMain.fxml")));
			ScreenController.getScreenController().addScreen("active_reports", FXMLLoader.load(getClass().getResource("/application/ActiveReports.fxml")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
