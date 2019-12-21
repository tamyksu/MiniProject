package application;

public class Person {
	
	private String id; // User ID
	private String cs;

	public Person(String id, String cs) {
		this.id=id;
		this.cs=cs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCs() {
		return cs;
	}

	public void setCs(String cs) {
		this.cs = cs;
	}
	
}
