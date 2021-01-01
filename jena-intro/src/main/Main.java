package main;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.*;

public class Main {

    static String personURI = "http://somewhere/JohnSmith";
    static String fullName = "John Smith";
    static String givenName = "John";
    static String familyName = "Smith";

    public static void main(String[] args) {

        //create model
        Model model = ModelFactory.createDefaultModel();

        // create the resource
        Resource johnSmith = model.createResource(personURI)
                .addProperty(VCARD.FN, fullName)
                .addProperty(VCARD.N,
                        model.createResource()
                        .addProperty(VCARD.Given, givenName)
                        .addProperty(VCARD.Family, familyName));

        //list statements in graph
        StmtIterator it = model.listStatements();
        while(it.hasNext()){
            Statement stmt = it.nextStatement();
            Resource subject   = stmt.getSubject();     // get the subject
            Property predicate = stmt.getPredicate();   // get the predicate

            //RDFNode - superclass of Resource and Literal
            RDFNode object = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource){
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");


        }
        //naive RDF writer
        model.write(System.out);
        //pretty RDF writer
        RDFDataMgr.write(System.out, model, Lang.RDFXML);
        //Write in triples(subject precedent object)
        RDFDataMgr.write(System.out, model, Lang.NTRIPLES);




    }
}
