package ca.idrc.tagin.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import ca.idrc.tagin.model.Beacon;
import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;
import ca.idrc.tagin.model.Pattern;
import ca.idrc.tagin.model.URN;
import ca.idrc.tagin.spi.v1.URNManager;

public class TaginEntityManager implements TaginDao {

	private EntityManager mEntityManager;
	private Cache mFingerprintCache;

	public TaginEntityManager() {
		mEntityManager = EMFService.createEntityManager();
		try {
			CacheFactory factory = CacheManager.getInstance().getCacheFactory();
			mFingerprintCache = factory.createCache(Collections.emptyMap());
		} catch (CacheException e) {
			Logger.getLogger(TaginDao.class.getName()).severe("Unable to create cache using internal mechanism");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String persistPattern(Pattern pattern) {
		Fingerprint fp = new Fingerprint(pattern);
		URNManager.generateURN(this, fp);
		mFingerprintCache.put(fp.getUrn(), fp);
		return fp.getUrn();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void persistFingerprint(Fingerprint fp) {
		mEntityManager.getTransaction().begin();
		mEntityManager.persist(fp);
		mEntityManager.flush();
		fp.getPattern().setId(fp.getPattern().getKey().getId());
		mEntityManager.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Pattern> listPatterns() {
		Query query = mEntityManager.createQuery("select p from Pattern p");
		List<Pattern> patterns = query.getResultList();
		for (Pattern p : patterns) {
			p.getBeacons(); // Forces eager-loading
		}
		return patterns;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Fingerprint> listFingerprints() {
		Query query = mEntityManager.createQuery("select f from Fingerprint f");
		List<Fingerprint> fingerprints = query.getResultList();
		return fingerprints;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pattern getPattern(Long id) {
		Pattern p = findPattern(id);
		if (p != null)
			p.getBeacons(); // Forces eager-loading
		return p;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Fingerprint getFingerprint(Long id) {
		Fingerprint fp = mEntityManager.find(Fingerprint.class, id);
		if (fp != null)
			fp.getPattern().getBeacons(); // Forces eager-loading
		return fp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Fingerprint getFingerprint(String urn) {
		if (mFingerprintCache.containsKey(urn)) {
			return (Fingerprint) mFingerprintCache.get(urn);
		}
		return findFingerprint(urn);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Neighbour> getNeighbours(Fingerprint fp) {
		List<Neighbour> neighbours = new ArrayList<Neighbour>();
		for (Beacon b : fp.getPattern().getBeacons().values()) {
			// TODO use this query instead when KEY() method is implemented in Google Datanucleus JPQL
			//Query query = mEntityManager.createQuery("select p from Pattern p join p.beacons b where KEY(b) = :bId");
			//query.setParameter("bId", "'" + b.getId() + "'");
			Query query = mEntityManager.createQuery("select p from Pattern p");
			List<Pattern> patterns = query.getResultList();

			for (Pattern p : patterns) {
				if (p.contains(b.getId())) {
					Fingerprint f = mEntityManager.find(Fingerprint.class, p.getKey().getParent());
					if (f.getId() != fp.getId()) {
						neighbours.add(new Neighbour(f, fp.rankDistanceTo(f)));
					}
				}
			}
		}
		return neighbours;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<URN> fetchNumOfNeighbours(Fingerprint fp, Integer maxCount) {
		Map<String,URN> neighbours = new LinkedHashMap<String,URN>();
		for (Neighbour n : getNeighbours(fp)) {
			String key = n.getFingerprint().getUrn();
			if (key != null && !neighbours.containsKey(key))
				neighbours.put(key, new URN(key));
			if (neighbours.size() >= maxCount)
				break;
		}
		
		if (neighbours.size() >= maxCount || neighbours.size() == 0) {
			return new ArrayList<URN>(neighbours.values());
		} else {
			return fetchNumOfNeighboursAux(fp.getUrn(), 0, maxCount, neighbours);
		}
	}
	
	/**
	 * Helper method for {@link fetchNumOfNeighbours(Fingerprint, Integer)}
	 * @param initialURN 
	 * @param index
	 * @param maxCount
	 * @param neighbours
	 * @return
	 */
	private List<URN> fetchNumOfNeighboursAux(String initialURN, int index, Integer maxCount, Map<String, URN> neighbours) {
		List<URN> urns = new ArrayList<URN>(neighbours.values());
		Fingerprint fp = getFingerprint(urns.get(index).getValue());
		index++;
		
		for (Neighbour n : getNeighbours(fp)) {
			String key = n.getFingerprint().getUrn();
			if (key != null && !neighbours.containsKey(key) && !key.equals(initialURN))
				neighbours.put(key, new URN(key));
			if (neighbours.size() >= maxCount)
				break;
		}
		
		if (neighbours.size() >= maxCount || index == neighbours.size()) {
			return new ArrayList<URN>(neighbours.values());
		} else {
			return fetchNumOfNeighboursAux(initialURN, index, maxCount, neighbours);
		}
	}
	
	/**
	 * Retrieves a pattern with the specified ID
	 * @param id
	 * @return the matching fingerprint if found, or null.
	 */
	@SuppressWarnings("unchecked")
	private Pattern findPattern(Long id) {
		Pattern p = null;
		Query query = mEntityManager.createQuery("select p from Pattern p where p.id = " + id);
		List<Pattern> result = query.getResultList();
		if (result.size() > 0) {
			p = result.get(0);
		}
		return p;
	}
	
	/**
	 * Retrieves a fingerprint with the specified URN
	 * @param urn
	 * @return the matching fingerprint if found, or null.
	 */
	@SuppressWarnings("unchecked")
	private Fingerprint findFingerprint(String urn) {
		Fingerprint fp = null;
		Query query = mEntityManager.createQuery("select f from Fingerprint f where f.urn = '" + urn + "'");
		List<Fingerprint> result = query.getResultList();
		if (result.size() > 0) {
			fp = result.get(0);
		}
		return fp;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removePattern(Long id) {
		Pattern p = findPattern(id);
		if (p != null) {
			Fingerprint parent = mEntityManager.find(Fingerprint.class, p.getKey().getParent());
			mEntityManager.remove(parent);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFingerprint(String urn) {
		Fingerprint fp = findFingerprint(urn);
		if (fp != null)
			mEntityManager.remove(fp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		mEntityManager.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beginTransaction() {
		mEntityManager.getTransaction().begin();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commitTransaction() {
		mEntityManager.getTransaction().commit();
	}

}
