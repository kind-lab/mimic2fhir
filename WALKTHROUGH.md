# mimic2fhir Walkthrough
In this walkthrough we will accomplish the following:
1. Set up a local RabbitMQ Server
2. Set up a local HAPI FHIR Server
3. Connect to a local Postgres DB
4. Test connections directly in mimic2fhir
5. Create 10 patient bundles

## 1. Local RabbitMQ Server
The RabbitMQ server is used to set up a queue for jobs getting sent to the FHIR Server. To set up locally:
1. Install docker
    - Linux: `sudo apt install docker.io` or [Ubuntu Docker Guide](https://docs.docker.com/engine/install/ubuntu/)
    - Windows: [Windows Docker Guide](https://docs.docker.com/desktop/windows/install/)
    - Mac: `brew cask install docker` or [Mac Docker Guide](https://docs.docker.com/desktop/mac/install/)
2. Start RabbitMQ server by running the docker command: `docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.9-management`

## 2. Local HAPI FHIR Server
The HAPI FHIR server is used to process the FHIR resources and bundles. To set up locally:
1. Git clone the [hapi-fhir-jpaserver-start](https://github.com/hapifhir/hapi-fhir-jpaserver-starter) 
2. Update the src/main/resources/application.yaml from R4 to DSTU3:
    - Hapi:fhir:version --> DSTU3
    - Hapi:fhir:tester:home:fhir_version --> DSTU3
3. Start the HAPIi FHIR server by running: `mvn jetty:run`

### 2.1 Customize HAPI FHIR Server
The HAPI FHIR server can be customized in a couple ways:
1. Add reference implementation guides:
    - Update the src/main/resources/application.yaml, add desired implementation guide:
    - Example yaml update for implementation guide:     
      ```yaml
      hapi:
        fhir:
          implementationguides:
            us-core:
              name: hl7.fhir.us.core
              version: 4.0.0
     ```
    - Now the FHIR server can validate against the new implementation guide
2. Update FHIR version:
    - The FHIR version can be one of DSTU2, DSTU3, R4 or R5
    - Update the src/main/resources/application.yaml
        - Hapi:fhir:version
        - Hapi:fhir:tester:home:fhir_version
        - Example yaml for R4:
        ```yaml
          hapi:
            fhir:
              version: R4
              tester:
                home:
                  fhir_version: R4             
         ```

## 3. Connect to Postgres DB
The Postgres DB must be set up locally for mimic2fhir to work. The steps to get a MIMIC-III Postgres DB setup are found in the [mimic-code](https://github.com/MIT-LCP/mimic-code/tree/main/mimic-iii/buildmimic/postgres) repo.

Once set up the main function in mimic2fhir needs to point to the correct postgres instance:
```java
configuration.setPassPostgres("postgres");
configuration.setPortPostgres("5432");
configuration.setUserPostgres("postgres");
configuration.setPostgresServer("localhost");
configuration.setDbnamePostgres("mimic");
configuration.setSchemaPostgres("mimiciii");
```

Update any credentials, ports, or naming that defers on your local machine (this is the default set up though)

## 4. Test connections
A couple JUnit tests have been added to validate the connectivitiy of the servers/db to mimic2fhir. Tests added:
- Connect to RabbitMQ server
- Connect to HAPI FHIR server
- Connect to Postgres DB

Tests are stored in src/test/java and the only file currently is ConnectionTest.java

## 5. Create 10 patient bundles
Now its time to create those FHIR resources from MIMIC!

Using the main class Main.java, we can create an example with 10 patient bundles. This example is based directly on the main mimic2fhir documentation:
```java
public static void main(String[] args) {
	Config configuration = new Config();
		
	//Postgres
	configuration.setPassPostgres("postgres");
	configuration.setPortPostgres("5432");
	configuration.setUserPostgres("postgres");
    	configuration.setPostgresServer("localhost");
    	configuration.setDbnamePostgres("mimic");
    	configuration.setSchemaPostgres("mimiciii");
    	
    	//FHIR
    	configuration.setFhirServer("http://localhost:8080/fhir");
	configuration.setFhirxmlFilePath("/MIMIC_Output/");
		
	//App configuration
	Mimic2Fhir app = new Mimic2Fhir();
	app.setConfig(configuration);
	app.setOutputMode(OutputMode.PRINT_FILE);
	app.setTopPatients(10);
	app.start();			 
       
    }
```

Running this locally will take around 10-15 minutes. The patient bundles will be exported as XML files. 

At this point we have a working local version of mimic2fhir. You are now free to set FHIR to MIMIC!
