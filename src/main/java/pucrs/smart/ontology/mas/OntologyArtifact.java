package pucrs.smart.ontology.mas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

//import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import jason.asSyntax.Literal;
import pucrs.smart.ontology.OwlOntoLayer;

public class OntologyArtifact extends Artifact {
	private Logger logger = Logger.getLogger(OntologyArtifact.class.getName());
	
	private OwlOntoLayer onto = null;
	private OntoQueryLayerLiteral queryEngine;
	
	void init(String ontologyPath) {
		logger.info("Importinga ontology from " + ontologyPath);
		try {
			this.onto = new OwlOntoLayer(ontologyPath);
			OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();			
			this.onto.setReasoner(reasonerFactory.createReasoner(this.onto.getOntology()));
			
			queryEngine = new OntoQueryLayerLiteral(this.onto);
			logger.info("Ontology ready!");
		} catch (OWLOntologyCreationException e) {
			logger.info("An error occurred when loading the ontology. Error: "+e.getMessage());
		} catch (Exception e) {
			logger.info("An unexpected error occurred: "+e.getMessage());
		}
	}
	
	/**
	 * @param instanceName Name of the new instance.
	 * @param conceptName Name of the concept which the new instance instances.
	 */
	@OPERATION
	void addInstance(String instanceName, String conceptName) {
		queryEngine.getQuery().addInstance(instanceName, conceptName);
	}
	
	/**
	 * @param instanceName Name of the instance.
	 * @param conceptName Name of the concept.
	 * @return true if the <code>instanceName</code> instances <code>conceptName</code>.
	 */
	@OPERATION
	void isInstanceOf(String instanceName, String conceptName, OpFeedbackParam<Boolean> isInstance) {
		isInstance.set(queryEngine.getQuery().isInstanceOf(instanceName, conceptName));
	}
	
	/**
	 * @param conceptName Name of the concept.
	 * @param instances A free variable to receive the list of instances in the form of instances(concept,instance)
	 */
	@OPERATION
	void getInstances(String conceptName, OpFeedbackParam<Literal[]> instances){
		List<Object> individuals = queryEngine.getIndividualNames(conceptName);
		
		instances.set(individuals.toArray(new Literal[individuals.size()]));
	}
	
	/**
	 * @param domainName Name of the instance ({@link OWLNamedIndividual}} which represent the property <i>domain</i>.
	 * @param propertyName Name of the new property.
	 * @param rangeName Name of the instance ({@link OWLNamedIndividual}} which represent the property <i>range</i>.
	 */
	@OPERATION
	void addProperty(String domainName, String propertyName, String rangeName) {
		queryEngine.getQuery().addProperty(domainName, propertyName, rangeName);
	}
	
	/**
	 * @param domainName Name of the instance which represents the domain of the property.
	 * @param propertyName Name of the property.
	 * @param rangeName Name of the instance which represents the range of the property.
	 * @return true if a instance of the property was found, false otherwise.
	 */
	@OPERATION
	void isRelated(String domainName, String propertyName, String rangeName, OpFeedbackParam<Boolean> isRelated) {
		isRelated.set(queryEngine.getQuery().isRelated(domainName, propertyName, rangeName));
	}
	
	/**
	 * @param domain The name of the instance which corresponds to the domain of the property.
	 * @param propertyName Name of the property
	 * @return A list of ({@link OWLNamedIndividual}).
	 */
	@OPERATION
	void getInstances(String domain, String propertyName, OpFeedbackParam<String> instances) {
		List<String> individuals = new ArrayList<String>();
		for(OWLNamedIndividual individual : queryEngine.getQuery().getInstances(domain, propertyName)) {
			individuals.add(individual.getIRI().getFragment().replaceAll("-","_"));
		}
		instances.set(individuals.toString());
	}
	
	/**
	 * @param conceptName Name of the new concept.
	 */
	@OPERATION
	void addConcept(String conceptName) {
		queryEngine.getQuery().addConcept(conceptName);
	}
	
	/**
	 * @param subConceptName Name of the supposed sub-concept.
	 * @param superConceptName Name of the concept to be tested as the super-concept.
	 * @return true if <code>subConceptName</code> is a sub-concept of <code>sueperConceptName</code>, false
	 * otherwise.
	 */
	@OPERATION
	void isSubConcept(String subConceptName, String superConceptName, OpFeedbackParam<Boolean> isSubConcept) {
		isSubConcept.set(queryEngine.getQuery().isSubConceptOf(subConceptName, superConceptName));
	}
	
	/**
	 * @return A list of concepts.
	 */
	@OPERATION
	void getConcepts() {
		List<OWLClass> concepts = new ArrayList<OWLClass>();
		for (OWLClass concept : queryEngine.getQuery().getConcepts()) {
			concepts.add(concept);
		}
	}
	
	/**
	 * @param outputFile Path to the new file in the structure of directories.
	 * @throws OWLOntologyStorageException
	 */
	@OPERATION
	void saveOntotogy(String outputFile) {
		try {
			queryEngine.getQuery().saveOntology(outputFile);
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}
	
	@OPERATION
	void test(String concept, Object[] list) {
		
		Date date = new Date(System.currentTimeMillis());
		System.out.println("Initial time "+date);
		
		for (int i = 0; i < list.length; i++) {
			isInstanceOf(list[i].toString(), concept);
		}
		
		date = new Date(System.currentTimeMillis());
		System.out.println("Final time "+date);
		
	}
	
	@OPERATION
	void isInstanceOf(String instanceName, String conceptName) {
		queryEngine.getQuery().isInstanceOf(instanceName, conceptName);
	}
	
}



















