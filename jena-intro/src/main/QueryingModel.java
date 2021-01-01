package main;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.*;

import java.io.InputStream;

//selecting VCARD resources
public class QueryingModel {

    static final String inputFileName = "vc-db-1.rdf";

    public static void main (String args[]) {
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // use the FileManager to find the input file
        InputStream in = FileManager.getInternal().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }

        // read the RDF/XML file
        model.read( in, "");

        //1) select all the resources with a VCARD.FN property
        //ResIterator iter = model.listResourcesWithProperty(VCARD.FN);
//        if (iter.hasNext()) {
//            System.out.println("The database contains vcards for:");
//            while (iter.hasNext()) {
//                System.out.println("  " + iter.nextResource()
//                        .getRequiredProperty(VCARD.FN)
//                        .getString() );
//            }
//        } else {
//            System.out.println("No vcards were found in the database");
//        }

        //2) select all statements that end with Smith (using Selector)
        StmtIterator iter = model.listStatements(new SimpleSelector(null, VCARD.FN, (RDFNode) null){
            @Override
            public boolean selects(Statement s) {
                return s.getString().endsWith("Smith");
            }
        });
        if(iter.hasNext()){
            while(iter.hasNext()){
                System.out.println(iter.nextStatement().getString());
            }
        }
        else{
            System.out.println("No such statements found");
        }



    }
}
