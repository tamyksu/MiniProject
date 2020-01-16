package application;
import java.util.HashMap;

public class MyHashMaps {
	public static MyHashMaps instance = new MyHashMaps();
	public static HashMap<Double, String> processStages;
	public static HashMap<Double, String> informationSystems;
	
	
	private MyHashMaps() {
		processStages = new HashMap<>();
		processStages.put((double) 1 , "Appointing Appraiser");
		processStages.put((double) 2, "Appraiser is defining evaluation stage due time");
		processStages.put((double) 2.5, "Supervisor denied the evaluation due time");
		processStages.put((double) 3, "Supervisor examines the evaluation stage due time");
		processStages.put((double) 4, "Appraiser is evaluating");
		processStages.put((double) 5, "Changes Control Board is making decision");
		processStages.put((double) 6, "Appointing performance leader");
		processStages.put((double) 7, "Performance Leader is defining execution stage due time");
		processStages.put((double) 7.5, "Supervisor declining the execution stage due time");
		processStages.put((double) 8, "Supervisor approving the execution stage due time");
		processStages.put((double) 9, "Prfomance Leader is executing");
		processStages.put((double) 10, "Appointing Examiner");
		processStages.put((double) 11, "Examiner is working");
		processStages.put((double) 12, "Supervisor is closing the process");
	}
	
	public static MyHashMaps getInstance() {
		return instance;
	}
	
	public static String getProcessStageText(double num) {
		return processStages.get((Double) num);		
	}
	
	public static String getInformationSystemText(int num) {
		return processStages.get(num);		
	}
	
	

}
