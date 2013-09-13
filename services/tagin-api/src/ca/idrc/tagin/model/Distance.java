package ca.idrc.tagin.model;

public class Distance {
	
	private Double distance;
	
	public Distance() {
		
	}
	
	public Distance(Double distance) {
		this.distance = distance;
	}
	
	public void setValue(Double distance) {
		this.distance = distance;
	}
	
	public Double getValue() {
		return distance;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[Distance: " + getValue());
		sb.append("]");
		return sb.toString();
	}

}
