package org.dotoozie;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;

public class DotOozie {

    private DirectedGraph<Vertex, Edge> graph;

    public DotOozie from(InputStream workflow) throws Exception {
        graph = new GraphGenerator().constructGraph(workflow);
        return this;
    }

    public DotOozie exportTo(String outputFile) throws Exception {
        try (Writer writer = new FileWriter(outputFile)) {
            DOTExporter<Vertex, Edge> exporter = new DOTExporter<>(new IntegerNameProvider<>(),
                                                                   new StringNameProvider<>(),
                                                                   new StringEdgeNameProvider<>(),
                                                                   new VertexAttributeProvider(),
                                                                   new EdgeAttributeProvider());
            exporter.export(writer, graph);
        }
        return this;
    }


    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Expected arguments: <Oozie workflow xml> <dot output file>");
        }

        DotOozie dotOozie = new DotOozie();
        dotOozie.from(new FileInputStream(args[0])).exportTo(args[1]);
    }

}
