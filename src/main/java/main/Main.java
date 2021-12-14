package main;

import de.uzl.itcr.mimic2fhir.work.Config;
import de.uzl.itcr.mimic2fhir.Mimic2Fhir;
import de.uzl.itcr.mimic2fhir.OutputMode;

public class Main {
	public static void main(String[] args) {
		Config configuration = new Config();
		
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
		
		//App configuration
		Mimic2Fhir app = new Mimic2Fhir();
		app.setConfig(configuration);
		app.setOutputMode(OutputMode.PRINT_FILE);
		app.setTopPatients(10);
		app.start();	
		 
       
    }
}
