package mimic2fhir;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.rest.client.apache.GZipContentInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import de.uzl.itcr.mimic2fhir.work.Config;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;

public class ConnectionTest {
	private final static String QUEUE_NAME = "BundleQ";
	private Channel channel;
	private Connection rbConnection;
	
	private java.sql.Connection dbConnection;
	
	private FhirContext ctx;
	private IGenericClient client;
	private Bundle transactionBundle;
	
	private Config configuration;
	
	@Before
	public void setup() {
		configuration = new Config();
    	
    	//Postgres
		configuration.setPassPostgres("postgres");
		configuration.setPortPostgres("5432");
		configuration.setUserPostgres("postgres");
    	configuration.setPostgresServer("localhost");
    	configuration.setDbnamePostgres("mimic_iv");
    	configuration.setSchemaPostgres("mimiciii");
    	
    	//FHIR
    	configuration.setFhirServer("http://localhost:8080/fhir");
		configuration.setFhirxmlFilePath("/home/alex/MIMIC_Output/");
	}
	
	@Test
	public void testRabbit() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {
			
			rbConnection = factory.newConnection();
			channel = rbConnection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			assertTrue(true);
		} catch (Exception e) {
			fail("Exception: " + e);
		} 
	}
	
	@Test
	public void testPostgresDriver() {
		try {
			Class.forName("org.postgresql.Driver");
			assertTrue(true);

		} catch (ClassNotFoundException e) {
			fail("Exception " + e);			
		}
	}
	
	@Test
	public void testPostgres() {
		this.dbConnection = null;
		
		//Schema-Construction, if necessary:
		String schema = "";
		if(configuration.getSchemaPostgres() != null && configuration.getSchemaPostgres().length() > 0) {
			schema = "?currentSchema=" + configuration.getSchemaPostgres();
		}

		try {
			dbConnection = DriverManager.getConnection(
			    	   "jdbc:postgresql://" + configuration.getPostgresServer() + ":" 
			    			   				+ configuration.getPortPostgres() + "/" 
			    			   				+ configuration.getDbnamePostgres() + schema,
			    			   				configuration.getUserPostgres(), 
			    			   				configuration.getPassPostgres());
			assertTrue(true); // Connected!		

		} catch (SQLException e) {
			fail("Failed to connect to postgres DB: " + e);
		}
	
	}
	
	@Test
	public void testHapi() {		
		transactionBundle = new Bundle();
		transactionBundle.setType(BundleType.TRANSACTION);
		
			
		try {
			ctx = FhirContext.forDstu3();
			
			// Use the narrative generator
			ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());
			client = ctx.newRestfulGenericClient(configuration.getFhirServer());	
			
			if(configuration.isAuthRequired())
			{			 
				//Authorization
				BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(configuration.getToken());
				client.registerInterceptor(authInterceptor);
			}
				 
			// Set how long to block for individual read/write operations (in ms)
			ctx.getRestfulClientFactory().setSocketTimeout(1500 * 1000);
			
			//Gzip output content
			client.registerInterceptor(new GZipContentInterceptor());
			client.transaction().withBundle(transactionBundle).execute();
			assertTrue(true); // server connected
		} catch (Exception e){
			fail("Failed connection to HAPI FHIR Server: " + e);
		}
		
	}


}
