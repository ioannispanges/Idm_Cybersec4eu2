package eu.olympus.model;

public class MFAInformation {

	private final String type;
	private String secret;
	private final long creation;
	private boolean activated;
	
	public MFAInformation(String type, String secret, long timeStamp, boolean activated) {
		this.type = type;
		this.secret = secret;
		this.creation = timeStamp;
		this.activated = activated;
	}
	
	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public long getCreation() {
		return creation;
	}
	
	public String getType() {
		return type;
	}
}
