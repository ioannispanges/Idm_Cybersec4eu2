package eu.olympus.server.interfaces;

public interface IdPRESTWrapper extends VirtualIdP {

    /**
     * Return the ID used to identify this IdP
     */
    public int getId();

    public void addPartialServerSignature(String ssid, byte[] signature);

    public void addPartialMFASecret(String ssid, String secret, String type);

    public void addMasterShare(String newSsid, byte[] share);

    public void setKeyShare(int id, byte[] newShare);

}
