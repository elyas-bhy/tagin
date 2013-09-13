package ca.idrc.tagin.model;

public class URN {
	
	private String urn;
	
	public URN() {
		
	}
	
	public URN(String value) {
		this.urn = value;
	}

	public String getValue() {
		return urn;
	}

	public void setValue(String urn) {
		this.urn = urn;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[URN: " + getValue());
		sb.append("]");
		return sb.toString();
	}

}
