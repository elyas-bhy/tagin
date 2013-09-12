package ca.idrc.tagin.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EMFService {
	
	private static final EntityManagerFactory mFactory = Persistence.createEntityManagerFactory("transactions");
	
	private EMFService() {
		
	}
	
	/**
	 * Returns a singleton instance of EntityManagerFactory
	 * @return
	 */
	public static EntityManagerFactory getInstance() {
		return mFactory;
	}
	
	/**
	 * Returns a new EntityManager
	 * @return
	 */
	public static EntityManager createEntityManager() {
		return mFactory.createEntityManager();
	}

}
