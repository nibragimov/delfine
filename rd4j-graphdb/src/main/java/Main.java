import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.impl.TreeModelFactory;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigSchema;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
//        Repository rep = new SailRepository(new MemoryStore());
//        Namespace ex = Values.namespace("ex", "http://example.org/");
//        IRI john = Values.iri(ex, "john");
//
//        try (RepositoryConnection conn = rep.getConnection()) {
//            conn.add(john, RDF.TYPE, FOAF.PERSON);
//            conn.add(john, RDFS.LABEL, Values.literal("John"));
//            RepositoryResult<Statement> statements = conn.getStatements(null, null, null);
//            Model model = QueryResults.asModel(statements);
//            model.setNamespace(RDF.NS);
//            model.setNamespace(RDFS.NS);
//            model.setNamespace(FOAF.NS);
//            model.setNamespace(ex);
//            Rio.write(model, System.out, RDFFormat.TURTLE);
//
//
//        }

        String path = "/home/nodir/.graphdb/data/repositories";
        RepositoryManager repositoryManager = new LocalRepositoryManager(new File(path));
        repositoryManager.init();

// Instantiate a repository graph model
        TreeModel graph = new TreeModel();

// Read repository configuration file
        InputStream config = EmbeddedGraphDB.class.getResourceAsStream("./repo-defaults.ttl");
        RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
        rdfParser.setRDFHandler(new StatementCollector(graph));
        try{
            rdfParser.parse(config, RepositoryConfigSchema.NAMESPACE);
            config.close();
        }
        catch (java.io.IOException e){
            System.out.println("IOException occurred");
        }


// Retrieve the repository node as a resource
        Model model = graph.filter(null, RDF.TYPE, RepositoryConfigSchema.REPOSITORY);

        Iterator<Statement> iterator = model.iterator();
        if (!iterator.hasNext())
            throw new RuntimeException("Oops, no <http://www.openrdf.org/config/repository#> subject found!");
        Statement statement = iterator.next();
        Resource repositoryNode =  statement.getSubject();

        //Resource repositoryNode = GraphUtil.getUniqueSubject(graph, RDF.TYPE, RepositoryConfigSchema.REPOSITORY);

// Create a repository configuration object and add it to the repositoryManager
        RepositoryConfig repositoryConfig = RepositoryConfig.create(graph, repositoryNode);
        repositoryManager.addRepositoryConfig(repositoryConfig);

// Get the repository from repository manager, note the repository id set in configuration .ttl file
        Repository repository = repositoryManager.getRepository("graphdb-repo");

// Open a connection to this repository
        RepositoryConnection conn = repository.getConnection();

// ... use the repository

        Namespace ex = Values.namespace("ex", "http://example.org/");
        IRI john = Values.iri(ex, "john");

        conn.add(john, RDF.TYPE, FOAF.PERSON);
        conn.add(john, RDFS.LABEL, Values.literal("John"));
        RepositoryResult<Statement> statements = conn.getStatements(null, null, null);
        Model m = QueryResults.asModel(statements);
        m.setNamespace(RDF.NS);
        m.setNamespace(RDFS.NS);
        m.setNamespace(FOAF.NS);
        m.setNamespace(ex);
        Rio.write(m, System.out, RDFFormat.TURTLE);




// Shutdown connection, repository and manager
        conn.close();
        repository.shutDown();
        repositoryManager.shutDown();


    }
}
