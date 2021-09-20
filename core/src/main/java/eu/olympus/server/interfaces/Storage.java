package eu.olympus.server.interfaces;

import eu.olympus.model.Attribute;
import eu.olympus.model.MFAInformation;

import java.util.Map;

/**
 * Generic interface for storage. 
 * The generic storage interface is used to manage user attributes
 * in a general fashion, independent of the choice of cryptographic
 * algorithms.
 * 
 */
public interface Storage {

	/**
	 * Checks if a the storage has an entry for the specified username.
	 * @param username The username 
	 * @return True if the username has an entry
	 */
	public boolean hasUser(String username);
	
	/**
	 * Get a map containing all attributes registrered for a username.
	 * @param username The username of the attributes to fetch
	 * @return A map containing the attributes
	 */
	public Map<String, Attribute> getAttributes(String username);
	
	/**
	 * Store a map of attributes to a specific user. 
	 * @param username The username of the user.
	 * @param attributes The attributes to store.
	 */
	public void addAttributes(String username, Map<String, Attribute> attributes);
	
	/**
	 * Store a single attribute to a specific user. 
	 * @param username The username of the user.
	 * @param key The name of the attribute
	 * @param value The value of the attribute.
	 */
	public void addAttribute(String username, String key, Attribute value);
	
	/**
	 * Delete a single attribute from a specific user. 
	 * @param username The username of the user.
	 * @param attributeName The name of the attribute to delete
	 */
	public boolean deleteAttribute(String username, String attributeName);

	/**
	 * Delete the user and all attached attributes.
	 * @param username The user to delete
	 * @return 
	 */
	public boolean deleteUser(String username);

	/**
	 * 
	 * @param username
	 * @param secret
	 */
	public void assignMFASecret(String username, String type, String secret);

	public Map<String, MFAInformation> getMFAInformation(String username);

	public void activateMFA(String username, String type);

	public void deleteMFA(String username, String type);

	/**
	 * Returns the time at which a user is allowed to attempt an authorization again
	 */
	public long getLastAuthAttempt(String username);

	/**
	 * Returns the number of failed authentications a user has attempted:
	 */
	public int getNumberOfFailedAuthAttempts(String username);

	/**
	 * Registers a failed authentication attempt for a user
	 */
	public void failedAuthAttempt(String username);

	/**
	 * Clears the number of failed authentication attempts for a user.
	 */

	public void clearFailedAuthAttempts(String username);

	/**
	 * Returns the number of failed MFA a user has attempted:
	 */
	public int getNumberOfFailedMFAAttempts(String username);

	/**
	 * Registers a failed MFA attempt for a user
	 */
	public void failedMFAAttempt(String username);

	/**
	 * Clears the number of failed MFA attempts for a user.
	 */
	public void clearFailedMFAAttempts(String username);
	/**
	 * Returns the time at which a user is allowed to attempt an authorization again
	 */
	public long getLastMFAAttempt(String username);
}
