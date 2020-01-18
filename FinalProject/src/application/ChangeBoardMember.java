package application;

import java.io.Serializable;


public class ChangeBoardMember  implements Serializable {
	private String id;
	private String name;

	/**
	 * Constructor
	 * @param id1
	 * @param name1
	 */
	public ChangeBoardMember(String id1, String name1) {
		this.id=id1;
		this.name=name1;
	}

	/**
	 * Get ID
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get Name
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	
}
