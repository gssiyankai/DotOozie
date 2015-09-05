package org.dotoozie;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringNameProvider;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;

public class DotOozie {

    private DirectedGraph<Node, DefaultEdge> graph;

    public DotOozie from(InputStream workflow) throws Exception {
        graph = new GraphGenerator().constructGraph(workflow);
        return this;
    }

    public DotOozie exportTo(String outputFile) throws Exception {
        try (Writer writer = new FileWriter(outputFile)) {
            DOTExporter exporter = new DOTExporter(new StringNameProvider<>(), null, null);
            exporter.export(writer, graph);
        }
        return this;
    }


    public static final void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Expected arguments: <Oozie workflow xml> <dot output file>");
        }

        DotOozie dotOozie = new DotOozie();
        dotOozie.from(new FileInputStream(args[0])).exportTo(args[1]);
    }

}
