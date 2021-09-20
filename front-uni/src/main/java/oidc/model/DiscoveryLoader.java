package oidc.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class DiscoveryLoader {

    private JSONObject discoveryFile;

    /**
     * A json loader that can load a discovery JSON file.
     * @param filepath path to file
     */
    public DiscoveryLoader(String filepath) {
        JSONParser parser = new JSONParser();
        try {
            discoveryFile = (JSONObject) parser.parse(new FileReader(filepath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject out() {
        return discoveryFile;
    }
}
