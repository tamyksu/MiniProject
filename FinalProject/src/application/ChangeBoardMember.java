package application;

import java.io.Serializable;

public class ChangeBoardMember  implements Serializable {
	private String id;
	private String name;

	
	public ChangeBoardMember(String id1, String name1) {
		this.id=id1;
		this.name=name1;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	
}
