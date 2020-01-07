package application;

public class InformationSystem {

	private int infomationSystemNumber;
	private String infomationSystemName;
	
	public InformationSystem(int infomationSystemNumber, String infomationSystemName) {
		super();
		this.infomationSystemNumber = infomationSystemNumber;
		this.infomationSystemName = infomationSystemName;
	}

	public int getInfomationSystemNumber() {
		return infomationSystemNumber;
	}

	public void setInfomationSystemNumber(int infomationSystemNumber) {
		this.infomationSystemNumber = infomationSystemNumber;
	}

	public String getInfomationSystemName() {
		return infomationSystemName;
	}

	public void setInfomationSystemName(String infomationSystemName) {
		this.infomationSystemName = infomationSystemName;
	}

	@Override
	public String toString() {
		return infomationSystemNumber + " " + infomationSystemName;
	}

	
	
}
