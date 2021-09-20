package eu.olympus.model;

import java.nio.ByteBuffer;

import org.apache.commons.codec.Charsets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SerializedKey {
	
	private String algorithm;
	private String format;
	private String encoded;

	public SerializedKey(){
		this.algorithm = null;
		this.format = null;
		this.encoded = null;
	}
	
	public SerializedKey(String algorithm, String format, String encoded) {
		this.algorithm = algorithm;
		this.format = format;
		this.encoded = encoded;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getEncoded() {
		return encoded;
	}

	public void setEncoded(String encoded) {
		this.encoded = encoded;
	}
	
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(getEncoded().getBytes(Charsets.UTF_8).length+200);
		buf.put(getFormat().getBytes(Charsets.UTF_8));
		buf.put(getAlgorithm().getBytes(Charsets.UTF_8));
		buf.put(getEncoded().getBytes(Charsets.UTF_8));
		return buf.array();
	}

}
