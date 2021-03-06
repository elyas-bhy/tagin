package ca.idrc.tagin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Key;

@Entity
public class Pattern implements Serializable {

	private static final long serialVersionUID = 9092532996122510034L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;
	
	@Basic
	private Long id;

	@OneToMany(cascade = CascadeType.ALL)
	private Map<String,Beacon> beacons;

	@Basic
	private Double maxRssi;

	public Pattern() {
		this.id = null;
		this.beacons = new HashMap<String,Beacon>();
		this.maxRssi = Beacon.NULL_RSSI;
	}

	public Key getKey() {
		return key;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Map<String,Beacon> getBeacons() {
		return beacons;
	}
	
	public void setBeacons(Map<String, Beacon> beacons) {
		this.beacons = beacons;
	}

	public Double getMaxRssi() {
		return maxRssi;
	}

	public void put(String bssid, Integer frequency, Integer dbm) {
		Beacon beacon = new Beacon(bssid, frequency, dbm);
		beacons.put(beacon.getId(), beacon);
	}
	
	public boolean contains(String bssid, Integer frequency) {
		return beacons.containsKey(bssid + ";" + frequency);
	}
	
	public boolean contains(String id) {
		return beacons.containsKey(id);
	}
	
	/**
	 * Updates all of the instance's beacons ranks
	 * according to the maximal RSSI measure found.
	 */
	public void updateRanks() {
		ArrayList<Beacon> values = new ArrayList<Beacon>(beacons.values());
		Collections.sort(values);
		maxRssi = values.get(0).getRssi();
		for (Beacon beacon : beacons.values()) {
			beacon.updateRank(maxRssi);
		}
	}
	
	/**
	 * Returns a vector that represents the difference between
	 * the instance pattern and the passed pattern.
	 * @param p
	 * @return
	 */
	public List<Beacon> calculateChangeVector(Pattern p) {
		Map<String,Beacon> beacons = new HashMap<String,Beacon>();
		ArrayList<Beacon> result = new ArrayList<Beacon>();
		for (Beacon beacon : this.getBeacons().values()) {
			beacons.put(beacon.getId(), beacon);
		}
		
		for (Beacon beacon : p.getBeacons().values()) {
			if (beacons.containsKey(beacon.getId())) {
				Beacon b = beacons.get(beacon.getId());
				Beacon v = new Beacon();
				v.setId(beacon.getId());
				v.setRank(beacon.getRank() - b.getRank());
				result.add(v);
			} else {
				result.add(beacon);
			}
		}
		return result;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[Key: " + getKey());
		sb.append(", maxRSSI: " + getMaxRssi());
		sb.append(", beacons: " + getBeacons().toString());
		sb.append("]");
		return sb.toString();
	}

}
