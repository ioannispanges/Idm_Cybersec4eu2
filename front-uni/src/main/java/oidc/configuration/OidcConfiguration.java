package oidc.configuration;

import com.nimbusds.jose.jwk.RSAKey;
import eu.olympus.client.*;
import eu.olympus.client.interfaces.ClientCryptoModule;
import eu.olympus.client.interfaces.CredentialManagement;
import eu.olympus.client.interfaces.UserClient;
import eu.olympus.client.storage.InMemoryCredentialStorage;
import eu.olympus.model.Operation;
import eu.olympus.model.PabcPublicParameters;
import eu.olympus.model.Policy;
import eu.olympus.model.Predicate;
import eu.olympus.util.multisign.MSverfKey;
import eu.olympus.verifier.PSPABCVerifier;
import oidc.model.DiscoveryLoader;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import javax.net.ssl.HostnameVerifier;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Configuration
public class OidcConfiguration {
    private static final byte[] seed = "random value random value random value random value random".getBytes();

    @Value("${pesto.servers.http}")
    private String servers;

    /**
     * Initiates the user client. Requires that all pesto IDPs are running.
     *
     * @return user client
     */

    @Bean
//    public UserClient createUserClient() throws Exception {
//        String[] serverArray = servers.split(",");
//        List<PestoIdPRESTConnection> idps = new ArrayList<PestoIdPRESTConnection>();
//        UserClient client = null;
//        Properties systemProps = System.getProperties();
//        systemProps.put("javax.net.ssl.trustStore", "src/test/resources/truststore.jks");
//        systemProps.put("javax.net.ssl.trustStorePassword", "OLYMPUS");
//        // Ensure that there is a certificate in the trust store for the webserver connecting
//        HostnameVerifier verifier = new DefaultHostnameVerifier();
//        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(verifier);
//
//        for (int i = 0; i < serverArray.length; i++) {
//            System.out.println("Server " + i + 1 + ": " + serverArray[i]);
//            PestoIdPRESTConnection idp = new PestoIdPRESTConnection(serverArray[i], "", i, 100000);
//            idps.add(idp);
//        }
//        CredentialManagement credentialManagement=new PSCredentialManagement(true, new InMemoryCredentialStorage());
//        ClientCryptoModule cryptoModule = new SoftwareClientCryptoModule(new Random(1), ((RSAPublicKey)idps.get(0).getCertificate().getPublicKey()).getModulus());
//        client = new PestoClient(idps, cryptoModule);
//
//        return client;
//    }
//pabc
    public UserClient createUserClient() throws Exception{
        String[] serverArray = servers.split(",");

        List<PabcIdPRESTConnection> idps = new ArrayList<>();
        UserClient client = null;
        Properties systemProps = System.getProperties();
        systemProps.put("javax.net.ssl.trustStore", "src/test/resources/truststore.jks");
        systemProps.put("javax.net.ssl.trustStorePassword", "OLYMPUS");
        for (int i = 0; i < serverArray.length; i++) {
            System.out.println("Server " + i + 1 + ": " + serverArray[i]);
            PabcIdPRESTConnection idp = new PabcIdPRESTConnection(serverArray[i], "", i, 100000);
            idps.add(idp);
        }
        Map<Integer, MSverfKey> publicKeys = new HashMap<>();
        for (int i = 0; i < serverArray.length; i++) {
            publicKeys.put(i, idps.get(i).getPabcPublicKeyShare());
        }
        PabcPublicParameters publicParam = idps.get(0).getPabcPublicParam();
        CredentialManagement credentialManagement = new PSCredentialManagement(true, new InMemoryCredentialStorage());
        ((PSCredentialManagement) credentialManagement).setup(publicParam, publicKeys, seed);

        ClientCryptoModule cryptoModule = new SoftwareClientCryptoModule(new Random(1), ((RSAPublicKey) idps.get(0).getCertificate().getPublicKey()).getModulus());

         client = new PabcClient(idps, credentialManagement, cryptoModule);
        PSPABCVerifier verifier = new PSPABCVerifier();
        verifier.setup(idps, seed);
        return client;
    }

    /**
     * The policy used when authenticating a login request.
     *
     * @return policy
     */

    @Bean
    public Policy policy() {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(new Predicate("name", Operation.REVEAL, null));
        // predicates.add(new Predicate("username", Operation.REVEAL, null));

//        predicates.add(new Predicate("birthdate", Operation.REVEAL, null));
//        predicates.add(new Predicate("course", Operation.REVEAL, null));
//        predicates.add(new Predicate("university", Operation.REVEAL, null));
      //  predicates.add(new Predicate("studentid", Operation.REVEAL, null));
      //predicates.add(new Predicate("awardeddegree", Operation.REVEAL, null));



        Policy policy = new Policy();
        policy.setPredicates(predicates);
        return policy;
    }

    @Bean
    public RSAKey certs() throws Exception {
        String[] serverArray = servers.split(",");
        PabcIdPRESTConnection idp = new PabcIdPRESTConnection(serverArray[0], "", 0, 100000);
        return new RSAKey.Builder((RSAPublicKey) idp.getCertificate().getPublicKey()).build();
    }

    @Bean
    public DiscoveryLoader discoveryLoader() {
        return new DiscoveryLoader("src/main/resources/openid-configuration-discovery");
    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

}
