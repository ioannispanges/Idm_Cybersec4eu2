package eu.olympus.oidc;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.olympus.client.*;
import eu.olympus.client.interfaces.ClientCryptoModule;
import eu.olympus.client.interfaces.CredentialManagement;
import eu.olympus.client.interfaces.UserClient;
import eu.olympus.client.storage.InMemoryCredentialStorage;
import eu.olympus.model.Attribute;
import eu.olympus.model.AttributeIdentityProof;
import eu.olympus.model.Operation;
import eu.olympus.model.PABCConfigurationImpl;
import eu.olympus.model.Policy;
import eu.olympus.model.Predicate;
import eu.olympus.model.exceptions.AuthenticationFailedException;
import eu.olympus.model.exceptions.OperationFailedException;
import eu.olympus.model.exceptions.TokenGenerationException;
import eu.olympus.model.exceptions.UserCreationFailedException;
import eu.olympus.oidc.model.SignIdentityProof;
import eu.olympus.oidc.server.identityprovers.SignIdentityProver;
import eu.olympus.oidc.server.storage.InMemoryPestoDatabase;
import eu.olympus.server.AttributeIdentityProver;
import eu.olympus.server.OIDCPestoIdPImpl;
import eu.olympus.server.PasswordJWTIdP;
import eu.olympus.server.PestoIdPImpl;
import eu.olympus.server.SoftwareServerCryptoModule;
import eu.olympus.server.interfaces.IdentityProver;
import eu.olympus.server.interfaces.PestoDatabase;
import eu.olympus.server.interfaces.PestoIdP;
import eu.olympus.server.interfaces.UserPasswordDatabase;
import eu.olympus.server.storage.InMemoryUserPasswordDatabase;
import eu.olympus.util.Util;
import eu.olympus.util.multisign.MSverfKey;
import eu.olympus.verifier.PSPABCVerifier;
import eu.olympus.verifier.VerificationResult;
import eu.olympus.verifier.interfaces.Verifier;
import java.io.File;
import java.security.cert.CertificateEncodingException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

public class TestFlow {

//	private List<PABCConfigurationImpl> configurations = new LinkedList<>();
//	private final int serverCount = 3;
//
//	@Before
//	public void setupConfigurations() throws Exception {
//		ObjectMapper mapper = new ObjectMapper();
//		for(int i = 0; i< serverCount; i++) {
//			configurations.add(mapper.readValue(new File("src/test/resources/setup"+i+".json"), PABCConfigurationImpl.class));
//		}
//	}
//
//	@Ignore
//	@Test
//	public void testPasswordJWTDirect() throws CertificateEncodingException{
//		UserPasswordDatabase db = new InMemoryUserPasswordDatabase();
//		PasswordJWTIdP idp = null;
//		List<IdentityProver> identityProvers = new LinkedList<IdentityProver>();
//		identityProvers.add(new SignIdentityProver(db));
//		try {
//			idp = new PasswordJWTIdP(db, identityProvers, new HashMap<>());
//		} catch(Exception e) {
//			fail("Failed to start IdP");
//		}
//		List<PasswordJWTIdP> idps = new ArrayList<PasswordJWTIdP>();
//		idps.add(idp);
//		UserClient client = new PasswordJWTClient(idps);
//		try{
//			idp.setup(TestParameters.getRSAPrivateKey1(), TestParameters.getRSA1Cert());
//		} catch(Exception e) {
//			fail("Failed to generate key");
//		}
////		Verifier verifier = new JWTVerifier(idp.getPublicKey());
////		testCreateTwoStepFlow(client, verifier);
//	}
//
//	// This test should be compatible with the docker-compose deployment
//	// ie. after running docker-compose up, this test should be able to succeed
//	@Ignore
//	@Test
//	public void testPestoRunning() throws Exception{
//
//		String path = "src/test/resources/volatile/truststore.jks";
//		String PW = "OLYMPUS";
//		Properties systemProps = System.getProperties();
//
//		systemProps.put("javax.net.ssl.trustStore", path);
//		systemProps.put("javax.net.ssl.trustStorePassword", PW);
//		System.setProperties(systemProps);
//
//		List<PestoIdP> idps = new LinkedList<>();
//		idps.add(new PestoIdPRESTConnection("https://localhost:9933", "", 0,100));
//		idps.add(new PestoIdPRESTConnection("https://localhost:9934", "", 1, 100));
//		idps.add(new PestoIdPRESTConnection("https://localhost:9935", "", 2, 100));
//		ClientCryptoModule cryptoModule = new SoftwareClientCryptoModule(new Random(1), ((RSAPublicKey)idps.get(0).getCertificate().getPublicKey()).getModulus());
//		UserClient client = new PestoClient(idps, cryptoModule);
//
//
//		Verifier verifier = new JWTVerifier(idps.get(0).getCertificate().getPublicKey());
//		testSimple(client, verifier);
//	}
//
//	@Test
//	public void testPestoDirect() throws Exception{
//		List<PestoIdPImpl> idps = new ArrayList<PestoIdPImpl>();
//		for(int i = 0; i< serverCount; i++) {
//			PestoDatabase db = new InMemoryPestoDatabase();
//			OIDCPestoIdPImpl idp = null;
//			List<IdentityProver> identityProvers = new LinkedList<IdentityProver>();
//			identityProvers.add(new AttributeIdentityProver(db));
//			try {
//				idp = new OIDCPestoIdPImpl(db, identityProvers, new HashMap<>(), new SoftwareServerCryptoModule(new Random(1)), configurations.get(i).getIssuerId(), 100000);
//			} catch(Exception e) {
//				e.printStackTrace();
//				fail("Failed to start IdP");
//			}
//			idps.add(idp);
//		}
//
//		for(int i = 0; i< serverCount; i++) {
//			try {
//				PestoIdPImpl idp = idps.get(i);
//				List<PestoIdP> others = new ArrayList<PestoIdP>();
//				others.addAll(idps);
//				others.remove(idp);
//				idp.setup("setup", configurations.get(i), others);
//			} catch(Exception e) {
//				fail("Failed to start IdP");
//			}
//		}
//
//		ClientCryptoModule cryptoModule = new SoftwareClientCryptoModule(new Random(1), ((RSAPublicKey)idps.get(0).getCertificate().getPublicKey()).getModulus());
//		UserClient client = new PestoClient(idps, cryptoModule);
//
//		Verifier verifier = new JWTVerifier(idps.get(0).getCertificate().getPublicKey());
//		testSimple(client, verifier);
//	}
//
//
//
//	public void testSimple(UserClient client, Verifier verifier) throws CertificateEncodingException {
//		try{
//			client.createUser("test", "pw1");
//		} catch(UserCreationFailedException e) {
//			fail("Failed to create user");
//		}
//
//		Map<String, Attribute> attributes = new HashMap<>();
//		attributes.put("url:name", new Attribute("John Doe"));
//		attributes.put("url:studentid", new Attribute("Se"));
//		attributes.put("url:awardeddegree", new Attribute("master"));
//		attributes.put("url:dateofBirth",new Attribute(Util.fromRFC3339UTC("1998-01-05T00:00:00")));
//		AttributeIdentityProof proof = new AttributeIdentityProof(attributes);
//
//		try{
//			client.addAttributes("test", "pw1", proof, "", "NONE");
//		} catch(OperationFailedException e) {
//			e.printStackTrace();
//			fail("Failed to add user certificate: " + e);
//		}
//
//		try{
//			client.createUserAndAddAttributes("test2", "pw2", proof);
//		} catch (UserCreationFailedException e){
//			fail("Could not create user and add attributes at the same time");
//		}
//
//		//Create a policy to reveal
//
//		String token;
//		try {
//			List<Predicate> predicates = new ArrayList<>();
//			predicates.add(new Predicate("name", Operation.REVEAL, null));
//			predicates.add(new Predicate("studentid", Operation.REVEAL, null));
//			predicates.add(new Predicate("awardeddegree", Operation.REVEAL, null));
//
//			predicates.add(new Predicate("audience", Operation.REVEAL, new Attribute("Test-SP")));
//			Policy policy = new Policy(predicates, "c368dd0e2db04cd6ba4ae2d5809774aa");
//
//			token = client.authenticate("test", "pw1", policy, "", "NONE");
//			//Create proper verification
//			assertTrue(verifier.verify(token));
//		} catch (AuthenticationFailedException  e) {
//			e.printStackTrace();
//			// TODO Auto-generated catch block
//			fail();
//		}
//
//	}
//}
private static Logger logger = LoggerFactory.getLogger(TestFlow.class);
	private static final byte[] seed = "random value random value random value random value random".getBytes();

	@Ignore
	@Test
	public void testPestoRunning() throws Exception{
		logger.info("Starting testPestoRunning");
		// Need to set trust store so the user application trusts the self-signed example SSL certificate used by the IdPs
		Properties systemProps = System.getProperties();
		systemProps.put("javax.net.ssl.trustStore", TestParameters.TEST_TRUST_STORE_LOCATION);
		systemProps.put("javax.net.ssl.trustStorePassword", TestParameters.TEST_TRUST_STORE_PWD);
		System.setProperties(systemProps);
		List<PestoIdPRESTConnection> idps = new ArrayList<PestoIdPRESTConnection>();
		int serverCount = 3;
		String administratorCookie="eimLN2/sr73deAVV8D/3FXFUNbSRdu3d/FJtWLCXGhu9+i6fiHcS54MyIOG6MczVR7r941CI+H1dbgDIVi+xHQ==";
		int[] ports=new int[serverCount];
		int basePort=9090;
		for(int i=0;i<serverCount;i++)
			ports[i]=basePort+i;
		for(int i = 0; i< serverCount; i++) {
			PestoIdPRESTConnection rest = new PestoIdPRESTConnection("https://localhost:"+(ports[i]),
					administratorCookie, i, 100000);
			idps.add(rest);
		}
		ClientCryptoModule cryptoModule = new SoftwareClientCryptoModule(new Random(1), ((RSAPublicKey)idps.get(0).getCertificate().getPublicKey()).getModulus());
		UserClient client = new PestoClient(idps, cryptoModule);
		Verifier verifier = new JWTVerifier(idps.get(0).getCertificate().getPublicKey());
		testPestoFlow(client, verifier);
	}


	@Ignore
	@Test
	public void testPabcRunning() throws Exception{
		logger.info("Starting testPabcPestoRunning");
		// Need to set trust store so the user application trusts the self-signed example SSL certificate used by the IdPs
		Properties systemProps = System.getProperties();
		systemProps.put("javax.net.ssl.trustStore", TestParameters.TEST_TRUST_STORE_LOCATION);
		systemProps.put("javax.net.ssl.trustStorePassword", TestParameters.TEST_TRUST_STORE_PWD);
		System.setProperties(systemProps);
		List<PabcIdPRESTConnection> idps = new ArrayList<PabcIdPRESTConnection>();
		int serverCount = 3;
		String administratorCookie="eimLN2/sr73deAVV8D/3FXFUNbSRdu3d/FJtWLCXGhu9+i6fiHcS54MyIOG6MczVR7r941CI+H1dbgDIVi+xHQ==";
		int[] ports=new int[serverCount];
		int basePort=9090;
		for(int i=0;i<serverCount;i++)
			ports[i]=basePort+i;
		for(int i = 0; i< serverCount; i++) {
			PabcIdPRESTConnection rest = new PabcIdPRESTConnection("https://localhost:"+(ports[i]),
					administratorCookie, i, 100000);
			idps.add(rest);
		}
		Map<Integer, MSverfKey> publicKeys = new HashMap<>();
		for (Integer j = 0; j< serverCount; j++){
			publicKeys.put(j, idps.get(j).getPabcPublicKeyShare());
		}
		CredentialManagement credentialManagement=new PSCredentialManagement(true, new InMemoryCredentialStorage());
		((PSCredentialManagement)credentialManagement).setup(idps,seed);
		ClientCryptoModule cryptoModule = new SoftwareClientCryptoModule(new Random(1), ((RSAPublicKey)idps.get(0).getCertificate().getPublicKey()).getModulus());
		UserClient client = new PabcClient(idps, credentialManagement, cryptoModule);
		PSPABCVerifier verifier = new PSPABCVerifier();
		verifier.setup(idps,seed);
		testPabcFlow(client, verifier);
	}

	public void testPabcFlow(UserClient client, PSPABCVerifier verifier) throws AuthenticationFailedException, TokenGenerationException, OperationFailedException {
		try{
			client.createUser("user_1", "password");
		} catch(UserCreationFailedException e) {
			fail("Failed to create user");
		}
		Map<String, Attribute> attributes = new HashMap<>();
		attributes.put("url:name", new Attribute("John Doe"));
		attributes.put("url:university", new Attribute("Spanish"));
		attributes.put("url:awardeddegree",new Attribute(185));
		attributes.put("url:dateOfBirth",new Attribute(Util.fromRFC3339UTC("1998-01-05T00:00:00")));

		try {

			// 	Prove identity with cached key
			client.addAttributes(new SignIdentityProof("proof", attributes));
		} catch(OperationFailedException e) {
			fail("Failed to prove identity: " + e);
		}
		client.clearSession();
		String signedMessage="SignedMessage";
		List<Predicate> predicates = new ArrayList<>();
		Predicate predicate = new Predicate();
		predicate.setAttributeName("url:name");
		predicate.setOperation(Operation.REVEAL);
		predicates.add(predicate);
		predicate = new Predicate();
		predicate.setAttributeName("url:name");
		predicate.setOperation(Operation.GREATERTHANOREQUAL);
		predicate.setValue(new Attribute(150));
		predicate = new Predicate();
		predicate.setAttributeName("url:dateOfBirth");
		predicate.setOperation(Operation.INRANGE);
		predicate.setValue(new Attribute(Util.fromRFC3339UTC("1990-01-05T00:00:00")));
		predicate.setExtraValue(new Attribute(Util.fromRFC3339UTC("2000-01-05T00:00:00")));
		predicates.add(predicate);
		Policy policy = new Policy(predicates, signedMessage);
		Policy verifierPolicy = new Policy(policy.getPredicates(), signedMessage);
		try{
			client.authenticate("user_1", "wrong password", policy, null, "NONE");
			fail("Could authenticate with a bad password");
		}catch(AuthenticationFailedException e){
		}
		client.clearSession();
		String token = client.authenticate("user_1", "password", policy, null, "NONE");
		assertThat(verifier.verifyPresentationToken(token, verifierPolicy), is(VerificationResult.VALID));
		client.deleteAccount("username","password", null, "NONE");
	}


	public void testPestoFlow(UserClient client, Verifier verifier) throws AuthenticationFailedException, TokenGenerationException, OperationFailedException {
		try{
			client.createUser("user_1", "password");
		} catch(UserCreationFailedException e) {
			fail("Failed to create user");
		}
		Map<String, Attribute> attributes = new HashMap<>();
		attributes.put("https://olympus-project.eu/example/model/name", new Attribute("John Doe"));
		attributes.put("https://olympus-project.eu/example/model/nationality", new Attribute("Spanish"));
		attributes.put("https://olympus-project.eu/example/model/height",new Attribute(185));
		attributes.put("https://olympus-project.eu/example/model/dateOfBirth",new Attribute(Util.fromRFC3339UTC("1998-01-05T00:00:00")));

		try {

			// 	Prove identity with cached key
			client.addAttributes(new SignIdentityProof("proof", attributes));
		} catch(OperationFailedException e) {
			fail("Failed to prove identity: " + e);
		}
		client.clearSession();
		List<Predicate> predicates = new ArrayList<>();
		Predicate predicate = new Predicate();
		predicate.setAttributeName("https://olympus-project.eu/example/model/name");
		predicate.setOperation(Operation.REVEAL);
		predicates.add(predicate);
		predicate = new Predicate();
		predicate.setAttributeName("https://olympus-project.eu/example/model/height");
		predicate.setOperation(Operation.GREATERTHANOREQUAL);
		predicate.setValue(new Attribute(150));
		predicates.add(predicate);
		Policy policy = new Policy(predicates, "testPolicy");
		try{ //
			client.authenticate("user_1", "bad_password", policy, null, "NONE");
			fail("Could authenticate with a bad password");
		} catch(AuthenticationFailedException e) {
		}
		client.clearSession();
		String token = client.authenticate("user_1", "password", policy, null, "NONE");
		assertThat(verifier.verify(token), is(true));
		client.deleteAccount("user_1","password", null, "NONE");
	}

}
