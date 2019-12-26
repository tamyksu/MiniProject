package translator;

import java.io.Serializable;
import java.util.ArrayList;

public class Translator implements Serializable{

	private static final long serialVersionUID = 1L;
	private ArrayList<?> parmas;
	private OptionsOfAction request;
	
	
	public Translator(OptionsOfAction action, ArrayList<?> params) {
		this.parmas = params;
		this.request = action;		
	}


	public ArrayList<?> getParmas() {
		return parmas;
	}


	public void setParmas(ArrayList<?> parmas) {
		this.parmas = parmas;
	}


	public OptionsOfAction getRequest() {
		return request;
	}


	public void setRequest(OptionsOfAction request) {
		this.request = request;
	}
}
