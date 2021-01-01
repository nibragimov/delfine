import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.VCARD;

import java.io.InputStream;
import java.util.Iterator;

import static org.apache.jena.ontology.OntModelSpec.*;

public class OntologyClass {
    static final String SOURCE = "http://www.virtual-factory.de/owl/";
    //static final String NS = SOURCE + "#";

    static final String ontologyFile = "virtualfactory_rdf_transitive.owl";
    public static void main(String[] args){

        final String ns = "http://www.example.org/test#";

        OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

        //OntModel model = ModelFactory.createOntologyModel(OWL_MEM_MICRO_RULE_INF);
        //OntModel inf = ModelFactory.createOntologyModel(OWL_MEM_MICRO_RULE_INF, model);
        Model uncleValues = ModelFactory.createDefaultModel();
        // use the FileManager to find the input file
//        InputStream in = FileManager.getInternal().open(ontologyFile);
//        if (in == null) {
//            throw new IllegalArgumentException( "File: " + ontologyFile + " not found");
//        }
        //TASK 1
        model.read(ontologyFile);

        OntClass machine = model.getOntClass( SOURCE + "Machine" );

        OntClass cars = model.createClass( SOURCE + "Cars" );
        OntClass heavyVehicles = model.createClass( SOURCE + "Heavy-Vehicles" );

        machine.addSubClass(cars);
        machine.addSubClass(heavyVehicles);

//        for (Iterator<OntClass> i = machine.listSubClasses(); i.hasNext(); ) {
//            OntClass c = i.next();
//            System.out.println( c.getURI() );
//        }

        //TASK 2

        OntClass personnel = model.getOntClass(SOURCE + "Personnel");
        //personnel instances
        Individual personA = model.createIndividual(SOURCE + "personA", personnel);
        Individual personB = model.createIndividual(SOURCE + "personB", personnel);
        Individual personC = model.createIndividual(SOURCE + "personC", personnel);
        //vehicle instances
        Individual carA = model.createIndividual(SOURCE + "carA", cars);
        Individual craneA = model.createIndividual(SOURCE + "craneA", heavyVehicles);

        //not really necessary, can infer?
        personA.addOntClass(model.getOntClass(SOURCE + "HumanResource"));
        personA.addOntClass(model.getOntClass(SOURCE + "ManufacturingResource"));
        personA.addOntClass(model.getOntClass(SOURCE + "Operator"));

        personB.addOntClass(model.getOntClass(SOURCE + "HumanResource"));
        personB.addOntClass(model.getOntClass(SOURCE + "ManufacturingResource"));
        personB.addOntClass(model.getOntClass(SOURCE + "Operator"));

        personC.addOntClass(model.getOntClass(SOURCE + "HumanResource"));
        personC.addOntClass(model.getOntClass(SOURCE + "ManufacturingResource"));
        personC.addOntClass(model.getOntClass(SOURCE + "Operator"));

        carA.addOntClass(model.getOntClass(SOURCE + "ManufacturingResource"));
        craneA.addOntClass(model.getOntClass(SOURCE + "ManufacturingResource"));


        //TASK 3

        ObjectProperty drives = model.createObjectProperty( SOURCE + "drives" );

        drives.addDomain( model.getOntClass(SOURCE + "Personnel"));
        drives.addRange(model.getOntClass(SOURCE + "Machine"));
        drives.addLabel( "drives", "en" );

        //adding properties to individuals
        personA.addProperty(drives, carA);
        personB.addProperty(drives, carA);
        personB.addProperty(drives, craneA);
        //inverse property
        ObjectProperty drivenBy = model.createObjectProperty( SOURCE + "drivenBy" );

        drives.addDomain( model.getOntClass(SOURCE + "Machine"));
        drives.addRange(model.getOntClass(SOURCE + "Personnel"));
        drives.addLabel( "drivenBy", "en" );

        //adding properties to individuals
        carA.addProperty(drivenBy, personA);
        carA.addProperty(drivenBy, personB);
        craneA.addProperty(drivenBy, personB);

        //TASK 4
        ObjectProperty supervises = model.createObjectProperty(SOURCE + "supervises");
        supervises.addDomain(model.getOntClass(SOURCE + "Personnel"));
        supervises.addRange(model.getOntClass(SOURCE + "Personnel"));
        supervises.addLabel( "supervises (transitive property)", "en" );

        //transitive property
        personA.addProperty(supervises, personB);
        personB.addProperty(supervises, personC);

        TransitiveProperty supt = supervises.convertToTransitiveProperty();

//        //if we run it with inference enabled, model infers that personA supervises personC
        StmtIterator iter = model.listStatements(new SimpleSelector(null, drives, (RDFNode) null));
        if(iter.hasNext()){
            while(iter.hasNext()){
                System.out.println(iter.nextStatement());
            }
        }
        else{
            System.out.println("No such statements found");
        }

        String queryString = "PREFIX vfp: <http://www.virtual-factory.de/owl/>" +
                "SELECT ?subject\n" +
                "WHERE {\n" +
                "  ?subject vfp:drives vfp:craneA .\n" +
                "}\n" +
                "LIMIT 25 " ;

        String queryString2 = "PREFIX vfp: <http://www.virtual-factory.de/owl/>" +
                "SELECT ?subject\n" +
                "WHERE {\n" +
                "  ?subject vfp:supervises vfp:personC .\n" +
                "}\n" +
                "LIMIT 25 " ;


        Query query = QueryFactory.create(queryString2) ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out(results);

        }
        System.out.println("Is supervises transitive? "+ supt.isTransitiveProperty());



    }
}
