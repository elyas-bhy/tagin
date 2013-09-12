package ca.idrc.tagin.spi.v1;

import java.util.List;
import java.util.UUID;

import ca.idrc.tagin.dao.TaginDao;
import ca.idrc.tagin.model.Beacon;
import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;
import ca.idrc.tagin.model.Pattern;

public class URNManager {
	
	private static TaginDao dao;
	
	/**
	 * Generates a URN to identify the passed fingerprint.
	 * A new URN is created if the fingerprint does not share any beacon with other fingerprints.
	 * If it does, it is merged into its closest neighbour, and they share the same URN.
	 * @param taginDao
	 * @param fp
	 */
	public static void generateURN(TaginDao taginDao, Fingerprint fp) {
		dao = taginDao;
		List<Neighbour> neighbours = fp.findCloseNeighbours();
		if (neighbours.isEmpty()) {
			UUID urn = UUID.randomUUID();
			fp.setUrn(urn.toString().replace("-", ""));
			dao.persistFingerprint(fp);
		} else {
			Neighbour n = neighbours.get(0);
			Fingerprint existingFp = n.getFingerprint();
			Pattern p1 = existingFp.getPattern();
			existingFp.merge(fp);
			fp.setUrn(existingFp.getUrn());
			
			// Push away any neighbour that has now become too close
			List<Beacon> changeVector = p1.calculateChangeVector(existingFp.getPattern());
			pushAwayNeighbours(n.getFingerprint(), changeVector);
		}
	}
	
	/**
	 * Performs a vectorial translation on the fingerprint's neighbours
	 * @param fp
	 * @param changeVector
	 */
	private static void pushAwayNeighbours(Fingerprint fp, List<Beacon> changeVector) {
		List<Neighbour> neighbours = fp.findCloseNeighbours();
		for (Neighbour n : neighbours) {
			Fingerprint fn = n.getFingerprint();
			fn.displaceBy(changeVector);
		}
		// Propagate changes
		for (Neighbour n : neighbours) {
			pushAwayNeighbours(n.getFingerprint(), changeVector);
		}
	}
	
}
