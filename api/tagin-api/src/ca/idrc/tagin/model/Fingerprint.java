package ca.idrc.tagin.model;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Fingerprint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Pattern pattern;
	
	@Basic
	private String urn;

	public Fingerprint() {

	}
	
	public Fingerprint(Pattern pattern) {
		this.pattern = pattern;
		this.urn = null;
	}

	public Long getId() {
		return id;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}
	
	public String toString() {
		return getClass().getName() +
				"[ID: " + getId() +
				", URN: " + getUrn() +
				", pattern: " + getPattern().toString() + "]";
	}

	
}
