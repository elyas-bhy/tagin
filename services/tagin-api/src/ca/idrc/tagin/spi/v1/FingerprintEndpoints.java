package ca.idrc.tagin.spi.v1;

import java.util.List;

import javax.inject.Named;

import ca.idrc.tagin.dao.TaginDao;
import ca.idrc.tagin.dao.TaginEntityManager;
import ca.idrc.tagin.model.Fingerprint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(
	name = "tagin",
	version = "v1"
)
public class FingerprintEndpoints {


	/**
	 * Endpoint method that returns a list of all stored fingerprints
	 * @return
	 */
	@ApiMethod(
			name = "fingerprints.list",
			path = "fingerprints",
			httpMethod = HttpMethod.GET
	)
	public List<Fingerprint> listFingerprints() {
		TaginDao dao = new TaginEntityManager();
		List<Fingerprint> fingerprints = dao.listFingerprints();
		dao.close();
		return fingerprints;
	}

	/**
	 * Endpoint method that retrieves a fingerprint with the specified ID
	 * @param id the fingerprint's ID
	 * @return the matching fingerprint if found, or null.
	 */
	@ApiMethod(
			name = "fingerprints.get",
			path = "fingerprints/{fingerprint_id}",
			httpMethod = HttpMethod.GET
	)
	public Fingerprint getFingerprint(@Named("fingerprint_id") Long id) {
		TaginDao dao = new TaginEntityManager();
		Fingerprint fp = dao.getFingerprint(id);
		dao.close();
		return fp;
	}

}
