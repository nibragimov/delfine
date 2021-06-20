import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.impl.method.StringDistAlignment;
import fr.inrialpes.exmo.align.impl.renderer.OWLAxiomsRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import org.semanticweb.owl.align.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


public class Main {

    public static void main(String[] args) throws AlignmentException {
        final String ontology1 = "file:/home/nodir/Delfine/sosa.owl";
        final String ontology2 = "file:/home/nodir/Delfine/OntoWind.owl";
        URI onto1;
        URI onto2;
        Properties params = new Properties();
        int question = 1;

        try {
            // Loading ontologies
            onto1 = new URI(ontology1);
            onto2 = new URI(ontology2);

            // Run two different alignment methods (e.g., ngram distance and smoa)
            AlignmentProcess a1 = new StringDistAlignment();
            params.setProperty("stringFunction","smoaDistance");
            a1.init ( onto1, onto2 );
            a1.align( (Alignment)null, params );

            // Merge the two results.
            Alignment result = a1;

            // Displays it as OWL Rules
            PrintWriter writer = new PrintWriter (
                    new BufferedWriter(
                            new OutputStreamWriter( System.out, StandardCharsets.UTF_8.name() )), true);
            AlignmentVisitor renderer = new OWLAxiomsRendererVisitor(writer);
            result.render(renderer);
            writer.flush();
            writer.close();

        } catch (Exception e) { e.printStackTrace(); };

    }
}
