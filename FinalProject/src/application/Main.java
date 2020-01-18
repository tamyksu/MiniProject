package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	
	public static Stage primaryStage;
	Scene baseScene;
	
	public static String ip = "localhost";
	public static int port = 25565;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent root = FXMLLoader.load(getClass().getResource("/application/basePanel.fxml"));
			baseScene = new Scene(root, 1500,1500);
			baseScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(baseScene);
			
			initializeScreenController(baseScene);
			
		    ControllerProcessMain.getInstance().logout_btn.setOnAction( __ ->
		    {
		      System.out.println( "Logout app!" );
		      primaryStage.close();
		      finalizeScreenController();
		      ScreenController.setScreenController(null);
		      Platform.runLater( () -> new Main().start( new Stage() ) );
		    } );
			
			ScreenController.getScreenController().activate("login");
			
			primaryStage.show();

			
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
	
	public void finalizeScreenController()
	{
		ScreenController.getScreenController().removeScreen("login");
		ScreenController.getScreenController().removeScreen("processesMain");
		ScreenController.getScreenController().removeScreen("evaluation");
		ScreenController.getScreenController().removeScreen("decisionMaking");
		ScreenController.getScreenController().removeScreen("execution");
		ScreenController.getScreenController().removeScreen("examination");
		ScreenController.getScreenController().removeScreen("newRequest");
		ScreenController.getScreenController().removeScreen("staffMain");
		ScreenController.getScreenController().removeScreen("supervisor_processesMain");
		ScreenController.getScreenController().removeScreen("active_reports");
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
			ScreenController.getScreenController().addScreen("delay_reports", FXMLLoader.load(getClass().getResource("/application/DelayReports.fxml")));
			ScreenController.getScreenController().addScreen("extension_reports", FXMLLoader.load(getClass().getResource("/application/ExtensionReports.fxml")));

		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(args.length>0)
		{
			Main.ip=args[0];
			Main.port=Integer.parseInt(args[1]);
		}
		launch(args);
	}
	
}
