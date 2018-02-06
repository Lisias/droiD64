package droid64.db;

import java.util.List;

public interface DiskDao {

	/**
	 * Get list of all stored disks.
	 * @return List of Disk
	 * @throws DatabaseException
	 */
	public List<Disk> getAllDisks() throws DatabaseException;
	
	/**
	 * Get one specified disk
	 * @param diskId
	 * @return Disk
	 * @throws DatabaseException
	 */
	public Disk getDisk(long diskId) throws DatabaseException;
	
	/**
	 * Update one Disk in database
	 * @param disk Disk
	 * @throws DatabaseException
	 */
	public void update(Disk disk) throws DatabaseException;
	
	/**
	 * Delete one disk from database
	 * @param disk
	 * @throws DatabaseException
	 */
	public void delete(Disk disk) throws DatabaseException;
	
	/**
	 * Search for disks
	 * @param criteria search criteria
	 * @return List of Disk
	 * @throws DatabaseException
	 */
	public List<Disk> search(DiskSearchCriteria criteria) throws DatabaseException;
	
	/**
	 * Save a disk composite.
	 * @param disk Disk
	 * @throws DatabaseException
	 */
	public void save(Disk disk) throws DatabaseException;
	
	/**
	 * Get Disk by filename
	 * @param fileName
	 */
	public Disk getDiskByFileName(String fileName) throws DatabaseException;
}
