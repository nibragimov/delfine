package main;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.*;

import java.io.InputStream;


public class RDFReader {
    static final String inputFile = "vc-db-1.rdf";
    static final String inputFile2 = "openadr.rdf";
    public static void main(String[] args){
        Model model = ModelFactory.createDefaultModel();
        //wrapper for file descriptor of input
        InputStream in = FileManager.getInternal().open(inputFile2);
        if(in == null){
            throw new IllegalArgumentException("File: "+inputFile2+" not found");
        }
        //read from file descriptor
        model.read(in, null);
        //write to stdout
        model.write(System.out);

    }

}
