/*
 * inspiration from 
 * https://www.javacodegeeks.com/2016/12/adding-microbenchmarking-build-process.html
 * and
 * https://www.mkyong.com/java/java-jmh-benchmark-tutorial/
 */

package eu.olympus.benchmark;

import eu.olympus.TestParameters;
import eu.olympus.model.RSASharedKey;
import eu.olympus.util.CommonCrypto;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

import eu.olympus.client.PestoClient;
import eu.olympus.client.PestoIdPRESTConnection;
import eu.olympus.client.SoftwareClientCryptoModule;
import eu.olympus.client.interfaces.ClientCryptoModule;
import eu.olympus.client.interfaces.UserClient;
import eu.olympus.model.Policy;
import eu.olympus.model.Predicate;
import eu.olympus.model.Attribute;

public class Benchmark {
	
	
	private static final int ITERATIONS = 30;
	private static final int WARMUP = 30;
	
	private static String user = "username";
	private static String password = "password";
	private static UserClient client;

	public static void main(String[] args) throws Exception {
		List<String> servIPs = new ArrayList<String>(2);
		servIPs.add(args[0]);
		servIPs.add(args[1]);
		List<PestoIdPRESTConnection> idps = new ArrayList<PestoIdPRESTConnection>();
		setup(servIPs, idps);

		List<Long> times;
		System.out.println("Executing " + ITERATIONS + " time each with " + WARMUP + " warmups");
		//
		times = benchmarkCreateUser();
		System.out.println("Create user average time is " + avg(times) + "ms with std " + std(times));
		//
		times = benchmarkAuthenticate();
		System.out.println("Authenticate average time is " + avg(times) + "ms with std " + std(times));
	}

	private static void setup(List<String> servIps, List<PestoIdPRESTConnection> idps) throws Exception {
		int serverCount = servIps.size();
		long startTime = System.currentTimeMillis();
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		KeyPair pair = keyGen.generateKeyPair();
		RSAPrivateCrtKey pk = (RSAPrivateCrtKey)pair.getPrivate();
		BigInteger d = pk.getPrivateExponent();

		Random rnd = new SecureRandom();
		BigInteger[] keyShares = new BigInteger[serverCount];
		BigInteger sum = BigInteger.ZERO;

		for(int i=0; i< serverCount-1; i++) {
			keyShares[i]= new BigInteger(pk.getModulus().bitLength()+8*CommonCrypto.STATISTICAL_SEC_BYTES, rnd).mod(pk.getModulus());
			sum = sum.add(keyShares[i]);
		}
		
		keyShares[serverCount-1] = d.subtract(sum);

		byte[] authKey = new byte[] {0x42};
		BigInteger[] oprfKeys = new BigInteger[serverCount];
		List<Map<Integer, BigInteger>> rsaBlindings = new ArrayList<Map<Integer, BigInteger>>(serverCount);
		List<Map<Integer, BigInteger>> oprfBlindings = new ArrayList<Map<Integer, BigInteger>>(serverCount);
		for(int i=0; i< serverCount; i++) {
			rsaBlindings.add(new HashMap<>(serverCount));
			oprfBlindings.add(new HashMap<>(serverCount));
			oprfKeys[i] = new BigInteger(CommonCrypto.BITS_IN_GROUP+8*CommonCrypto.STATISTICAL_SEC_BYTES, rnd).mod(CommonCrypto.CURVE_ORDER);
		}
		for(int i=0; i< serverCount; i++) {
			for(int j = i; j<serverCount; j++) {
				if(i != j) {
					BigInteger current = new BigInteger(pk.getModulus().bitLength()+8*CommonCrypto.STATISTICAL_SEC_BYTES, rnd).mod(pk.getModulus());;
					rsaBlindings.get(i).put(j, current);
					rsaBlindings.get(j).put(i, current);
					current = new BigInteger(CommonCrypto.COMPUTATION_SEC_BYTES * 8, rnd);
					oprfBlindings.get(i).put(j, current);
					oprfBlindings.get(j).put(i, current);
				}
			}
		}

		Properties systemProps = System.getProperties();
		systemProps.put("javax.net.ssl.keyStorePassword", TestParameters.TEST_TRUST_STORE_PWD);
		systemProps.put("javax.net.ssl.keyStore", TestParameters.TEST_KEY_STORE_LOCATION);
		systemProps.put("javax.net.ssl.trustStore", TestParameters.TEST_TRUST_STORE_LOCATION);
		systemProps.put("javax.net.ssl.trustStorePassword", TestParameters.TEST_TRUST_STORE_PWD);
		System.setProperties(systemProps);

		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
				new javax.net.ssl.HostnameVerifier(){

					public boolean verify(String hostname,
							javax.net.ssl.SSLSession sslSession) {
						//return hostname.equals("localhost");
						return true;
					}
				});
		System.out.println("creating connections");
		int port = 9998;
		for(int i = 0; i< serverCount; i++) {
			try {
				PestoIdPRESTConnection idp = idps.get(i);
				Map<Integer, String> others = new HashMap<>();
				for(int j = 0; j <serverCount; j++) {
					if(j != i) {
						others.put(j, "https://" + servIps.get(j) + ":"+(port+j));
					}
				}
				System.out.println("setting up server "+i);
				long s1 = System.currentTimeMillis();
				RSASharedKey rsaShare = new RSASharedKey(pk.getModulus(), keyShares[i], pk.getPublicExponent());
//				PABCConfiguration pabcConf = new PABCConfigurationImpl(port+i, port+i,
//						new ArrayList<String>(others.values()), null, null, null, null, rsaShare, rsaBlindings.get(i), oprfBlindings.get(i),
//						oprfKeys[i], authKey, i, 1000, 10000, 7200000,
//						new byte[] {0x42}, new HashSet<>());
				System.out.println("finished with server "+i+ "("+(System.currentTimeMillis()-s1)+")");
			} catch(Exception e) {
				System.out.println("Failed to start IdP");
			}
		}

		ClientCryptoModule cryptoModule = new SoftwareClientCryptoModule(new Random(1), ((RSAPublicKey)idps.get(0).getCertificate()).getModulus());
		client = new PestoClient(idps, cryptoModule);
		
		System.out.println("setup took "+(System.currentTimeMillis()-startTime)+" ms");
	}

	 private static double avg(List<Long> times) {
		 double sum = 0;
		 for (int i = 0; i < times.size(); i++) {
			 sum += times.get(i).doubleValue();
		}
		 return sum/times.size();
	}

	private static double std(List<Long> times) {
		double avg = avg(times);
		double squaredDiff = 0.0;
		for (int i = 0; i < times.size(); i++) {
			squaredDiff += (avg - times.get(i).doubleValue()) * (avg - times.get(i).doubleValue());
		}
		return Math.sqrt(squaredDiff/times.size());
	}
	 
	 private static List<Long> benchmarkCreateUser() throws Exception{

			List<Long> times = new ArrayList<>(ITERATIONS);
			long startTime = 0;
			long endTime = 0;
			for (int i = 0; i < ITERATIONS + WARMUP; i++) {
				startTime = java.lang.System.currentTimeMillis();
				client.createUser(user+i, password);
				endTime = java.lang.System.currentTimeMillis();
				Thread.sleep(20);
				if(i >= WARMUP){
					times.add(endTime - startTime);
				}
			}
			return times;
	 }
	 
	 private static List<Long> benchmarkAuthenticate() throws Exception{
		 List<Long> times = new ArrayList<>(ITERATIONS);
		 Map<String, Attribute> attributes = new HashMap<>();
		 attributes.put("Name", new Attribute("Jon Doe"));
		 attributes.put("Nationality", new Attribute("DK"));

		 long startTime = 0;
		 long endTime = 0;
		 for (int i = 0; i < ITERATIONS + WARMUP; i++) {
			 startTime = java.lang.System.currentTimeMillis();
			 Policy policy = new Policy();
				List<Predicate> predicates = new ArrayList<>();
				policy.setPredicates(predicates);
			 client.authenticate(user+i, password, policy, null, "NONE");
			 endTime = java.lang.System.currentTimeMillis();
			 Thread.sleep(20);
			 if(i >= WARMUP){
				 times.add(endTime - startTime);
			 }
		}
		 return times;
	 }
}
