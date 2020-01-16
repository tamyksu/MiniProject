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
		processStages.put((double) 4.5, "Appraiser is waiting for extension request reply");
		processStages.put((double) 4.6, "Appraiser received a confirmation for his extension request");
		processStages.put((double) 5, "Changes Control Board is making decision");
		processStages.put((double) 5.5, "Chairman is waiting for extension request reply");
		processStages.put((double) 5.6, "Chairman received a confirmation for his extension request");
		processStages.put((double) 6, "Appointing performance leader");
		processStages.put((double) 7, "Performance Leader is defining execution stage due time");
		processStages.put((double) 7.5, "Supervisor declining the execution stage due time");
		processStages.put((double) 8, "Supervisor approving the execution stage due time");
		processStages.put((double) 9, "Prfomance Leader is executing");
		processStages.put((double) 9.5, "Prfomance Leader is waiting for extension request reply");
		processStages.put((double) 9.6, "Prfomance Leader received a confirmation for his extension request");
		processStages.put((double) 10, "Appointing Examiner");
		processStages.put((double) 11, "Examiner is working");
		processStages.put((double) 11.1, "Examiner is working");
		processStages.put((double) 11.2, "Examiner is working");
		processStages.put((double) 11.3, "Examiner is working");
		processStages.put((double) 11.5, "Examiner is waiting for extension request reply");
		processStages.put((double) 11.6, "Examiner received a confirmation for his extension request");
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
