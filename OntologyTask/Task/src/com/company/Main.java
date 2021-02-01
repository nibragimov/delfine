package com.company;

import de.derivo.sparqldlapi.Query;
import de.derivo.sparqldlapi.QueryEngine;
import de.derivo.sparqldlapi.QueryResult;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class Main {
    private static QueryEngine engine;


    public static void main(String[] args) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        File file = new File("/home/tuan/Desktop/Work/onthologies/virtualfactory_rdf_transitive.owl");
        OWLOntology o = man.loadOntologyFromOntologyDocument(file);

        //Task1: define two new classes Cars andHeavy-Vehicles as sub classes of the existing class Machine.
        OWLDataFactory df = o.getOWLOntologyManager().getOWLDataFactory();
        IRI IOR = IRI.create("http://www.virtual-factory.de/owl/"); //general IRI
        OWLClass machine = df.getOWLClass(IOR + "Machine");
        OWLClass car = df.getOWLClass(IOR +"Car");
        OWLClass heavyVehicle = df.getOWLClass(IOR +"HeavyVehicle");
        OWLSubClassOfAxiom c_sub_m = df.getOWLSubClassOfAxiom(car, machine);
        OWLSubClassOfAxiom hv_sub_m = df.getOWLSubClassOfAxiom(heavyVehicle, machine);
        o.add(c_sub_m);
        o.add(hv_sub_m);




        //Task 2: define some new instances of the classes in our ontology
        OWLClass person = df.getOWLClass(IOR+"Personnel");
        OWLNamedIndividual personA = df.getOWLNamedIndividual(IOR + "PersonA");
        OWLNamedIndividual personB = df.getOWLNamedIndividual(IOR + "PersonB");
        OWLNamedIndividual personC = df.getOWLNamedIndividual(IOR + "PersonC");
        OWLNamedIndividual carA = df.getOWLNamedIndividual(IOR + "CarA");
        OWLNamedIndividual craneA = df.getOWLNamedIndividual(IOR + "CraneA");

        // Create a ClassAssertion to specify that
        Set<OWLAxiom> instanceAxioms = new HashSet<>();
        instanceAxioms.add(df.getOWLClassAssertionAxiom(person, personA));
        instanceAxioms.add(df.getOWLClassAssertionAxiom(person, personB));
        instanceAxioms.add(df.getOWLClassAssertionAxiom(person, personC));
        instanceAxioms.add(df.getOWLClassAssertionAxiom(car, carA));
        instanceAxioms.add(df.getOWLClassAssertionAxiom(heavyVehicle, craneA));
        o.add(instanceAxioms);



        //Task3:
        //Personal A drives Car A
        OWLObjectProperty drives = df.getOWLObjectProperty(IOR+"drives");
        Set<OWLAxiom> driveAxioms = new HashSet<>();
        driveAxioms.add(df.getOWLObjectPropertyAssertionAxiom(drives, personA, carA));
        driveAxioms.add(df.getOWLObjectPropertyAssertionAxiom(drives, personB, carA));
        driveAxioms.add(df.getOWLObjectPropertyAssertionAxiom(drives, personB, craneA));
        o.add(driveAxioms);
        //At this point, we have an ontology
        //containing facts about several individuals. We now want to specify
        // more information about the various properties that we have used.
        Set<OWLAxiom> domainsAndRanges = new HashSet<>();
        domainsAndRanges.add(df.getOWLObjectPropertyDomainAxiom(drives, person));
        domainsAndRanges.add(df.getOWLObjectPropertyRangeAxiom(drives, machine));
        o.add(domainsAndRanges);

        o.logicalAxioms().forEach(System.out::println);
        System.out.println("====================================");

        //Cardinality restriction
//        OWLDataExactCardinality hasAgeRestriction = factory.getOWLDataExactCardinality(1, hasAge);

        //Inverse characteristic
//        OWLObjectProperty isDriven = df.getOWLObjectProperty(IOR+"isDriven");
//        o.addAxiom(df.getOWLInverseObjectPropertiesAxiom(drives, isDriven));



        //Task 4
        System.out.println("Task 4:");
        OWLReasonerFactory rf = new ReasonerFactory();
        OWLReasoner r = rf.createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        //query with the reasoner: obtain all subclasses of a class
        //only direct -> true, also indirect: false
        r.getSubClasses(machine, false).forEach(System.out::println);
        r.precomputeInferences(InferenceType.CLASS_ASSERTIONS,InferenceType.OBJECT_PROPERTY_ASSERTIONS);
        //To run the Sparql -> Need to add sparql-dl-api!!!

        // Create an instance of the SPARQL-DL query engine
        engine = QueryEngine.create(man, r, true);

        processQuery(
                "SELECT * WHERE {\n" +
                        "Type(?x, <http://www.virtual-factory.de/owl/HumanResource>)" +
                        "}"
        );

        //save new ontology
        File fileOut = new File("/home/tuan/Desktop/Work/OntologyTask/Output/virtualfactory_rdf_transitive.owl");
        man.saveOntology(o, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileOut));
    }


    public static void processQuery(String q)
    {
        try {
            long startTime = System.currentTimeMillis();

            // Create a query object from it's string representation
            Query query = Query.create(q);

            System.out.println("Execute the query:");
            System.out.println(q);
            System.out.println("-------------------------------------------------");

            // Execute the query and generate the result set
            QueryResult result = engine.execute(query);

            if(query.isAsk()) {
                System.out.print("Result: ");
                if(result.ask()) {
                    System.out.println("yes");
                }
                else {
                    System.out.println("no");
                }
            }
            else {
                if(!result.ask()) {
                    System.out.println("Query has no solution.\n");
                }
                else {
                    System.out.println("Results:");
                    System.out.print(result);
                    System.out.println("-------------------------------------------------");
                    System.out.println("Size of result set: " + result.size());
                }
            }

            System.out.println("-------------------------------------------------");
            System.out.println("Finished in " + (System.currentTimeMillis() - startTime) / 1000.0 + "s\n");
        }
        catch(QueryParserException e) {
            System.out.println("Query parser error: " + e);
        }
        catch(QueryEngineException e) {
            System.out.println("Query engine error: " + e);
        }
    }
}
