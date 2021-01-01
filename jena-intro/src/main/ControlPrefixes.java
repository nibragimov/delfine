package main;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class ControlPrefixes {
    public static void main(String[] args){
        Model m = ModelFactory.createDefaultModel();
        String nsA = "http://somewhere/else#";
        String nsB = "http://nowhere/else#";
        Resource root = m.createResource( nsA + "root" );
        Property P = m.createProperty( nsA + "P" );
        Property Q = m.createProperty( nsB + "Q" );
        Resource x = m.createResource( nsA + "x" );
        Resource y = m.createResource( nsA + "y" );
        Resource z = m.createResource( nsA + "z" );
        m.add( root, P, x ).add( root, P, y ).add( y, Q, z );
        System.out.println( "# -- no special prefixes defined" );
        m.write( System.out );
        System.out.println( "# -- nsA defined" );
        //set prefix nsA explicitly
        m.setNsPrefix( "nsA", nsA );
        m.write( System.out );
        System.out.println( "# -- nsA and cat defined" );
        //set prefix cat explicitly
        m.setNsPrefix( "cat", nsB );
        m.write( System.out );

    }
}
