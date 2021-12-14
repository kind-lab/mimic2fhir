package de.uzl.itcr.mimic2fhir.model;

import java.util.Date;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;

public class MMicrobiologyevent {
	// Date
	private Date chartTime;
	
	// Value
	private int specItemId;
	private int orgItemId;
	private String orgName;
	private int abItemId;
	private String abName;
	private String interpretation;
	
	public Date getChartTime() {
		return chartTime;
	}

	public void setChartTime(Date chartTime) {
		this.chartTime = chartTime;
	}
	
	public int getSpecItemId() {
		return specItemId;
	}

	public void setSpecItemId(int specItemId) {
		this.specItemId = specItemId;
	}
	
	public int getOrgItemId() {
		return orgItemId;
	}

	public void setOrgItemId(int orgItemId) {
		this.orgItemId = orgItemId;
	}
	
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public int getAbItemId() {
		return abItemId;
	}

	public void setAbItemId(int abItemId) {
		this.abItemId = abItemId;
	}
	
	public String getAbName() {
		return abName;
	}

	public void setaAbName(String abName) {
		this.abName = abName;
	}
	
	public String getInterpretation() {
		return interpretation;
	}

	public void setInterpretation(String interpretation) {
		this.interpretation = interpretation;
	}
	
	/**
	 * Create FHIR-"Observation" resources for microbiology organism data
		 * @param patId Patient-FHIR-Resource-Id
		 * @param encId Encounter-FHIR-Resource-Id
	 * @return FHIR-Observation
	 */
	public Observation getFhirObservationOrg(String patId, String encId) {
		Observation observation = new Observation();
		
		observation.setStatus(ObservationStatus.FINAL);
		
		//all laboratory
		observation.addCategory().addCoding().setSystem("http://hl7.org/fhir/observation-category").setCode("microbiology").setDisplay("Microbiology");		
		
		
		CodeableConcept cc = new CodeableConcept();
		
		//Type of Observation
		cc.addCoding().setSystem("http://fhir.mimic.com/codesystem/organism").setCode(Integer.toString(this.getOrgItemId()));
		cc.setText(this.getOrgName());		
		observation.setCode(cc);
		
		
		//Pat-Reference
		observation.setSubject(new Reference(patId));
		
		//Enc-Reference
		observation.setContext(new Reference(encId));
		
		//Record-Date
		observation.setEffective(new DateTimeType(this.getChartTime()));
		
		// reference the susc here, but 'Hasmember' is not part of DSTU3, new in R4
		
		return observation;
	}
	
	/**
	 * Create FHIR-"Observation" resources for microbiology susceptibility data
		 * @param patId Patient-FHIR-Resource-Id
		 * @param encId Encounter-FHIR-Resource-Id
	 * @return FHIR-Observation
	 */
	public Observation getFhirObservationSusc(String patId, String encId) {
		Observation observation = new Observation();
		
		observation.setStatus(ObservationStatus.FINAL);
		
		//all laboratory
		observation.addCategory().addCoding().setSystem("http://hl7.org/fhir/observation-category").setCode("microbiology").setDisplay("Microbiology");		
		
		
		CodeableConcept cc = new CodeableConcept();
		
		//Type of Observation
		cc.addCoding().setSystem("http://fhir.mimic.com/codesystem/susceptibility").setCode(Integer.toString(this.getAbItemId()));
		cc.setText(this.getAbName());		
		observation.setCode(cc);
		
		
		//Pat-Reference
		observation.setSubject(new Reference(patId));
		
		//Enc-Reference
		observation.setContext(new Reference(encId));
		
		//Record-Date
		observation.setEffective(new DateTimeType(this.getChartTime()));
		
		//Interpretation 
		if(this.interpretation != null) {
			cc = new CodeableConcept();
			cc.addCoding().setSystem("http://fhir.mimic.com/valueset/micro-interpretation").setCode(this.getInterpretation());
			observation.setInterpretation(cc);
		}
		
		return observation;
	}
	
}
