package eu.olympus.server.interfaces;

public interface IdentityProver {

	public boolean isValid(String idProof, String username);

	public void addAttributes(String proof, String username);
}
