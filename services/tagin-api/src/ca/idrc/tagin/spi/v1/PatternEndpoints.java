package ca.idrc.tagin.spi.v1;

import java.util.List;

import javax.inject.Named;

import ca.idrc.tagin.dao.TaginDao;
import ca.idrc.tagin.dao.TaginEntityManager;
import ca.idrc.tagin.model.Pattern;
import ca.idrc.tagin.model.URN;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(
	name = "tagin",
	version = "v1"
)
public class PatternEndpoints {

	/**
	 * Endpoint method that returns a URN corresponding to the pattern passed.
	 * @param pattern 
	 * @return
	 */
	@ApiMethod(
			name = "patterns.add",
			path = "patterns",
			httpMethod = HttpMethod.POST
	)
	public URN addPattern(Pattern pattern) {
		TaginDao dao = new TaginEntityManager();
		String urn = dao.persistPattern(pattern);
		dao.close();
		return new URN(urn);
	}

	/**
	 * Endpoint method that returns a list of all stored patterns.
	 * @return
	 */
	@ApiMethod(
			name = "patterns.list",
			path = "patterns",
			httpMethod = HttpMethod.GET
	)
	public List<Pattern> listPatterns() {
		TaginDao dao = new TaginEntityManager();
		List<Pattern> patterns = dao.listPatterns();
		dao.close();
		return patterns;
	}
	
	/**
	 * Endpoint method that retrieves a pattern with the specified ID.
	 * @param id
	 * @return the matching pattern if found, or null.
	 */
	@ApiMethod(
			name = "patterns.get",
			path = "patterns/{pattern_id}",
			httpMethod = HttpMethod.GET
	)
	public Pattern getPattern(@Named("pattern_id") Long id) {
		TaginDao dao = new TaginEntityManager();
		Pattern p = dao.getPattern(id);
		dao.close();
		return p;
	}
	
	/**
	 * Endpoint method that deletes a pattern with the specified ID from the datastore.
	 * @param id
	 */
	@ApiMethod(
			name = "patterns.remove",
			path = "patterns/{pattern_id}",
			httpMethod = HttpMethod.DELETE
	)
	public void removePattern(@Named("pattern_id") Long id) {
		TaginDao dao = new TaginEntityManager();
		dao.removePattern(id);
		dao.close();
	}
	
}
