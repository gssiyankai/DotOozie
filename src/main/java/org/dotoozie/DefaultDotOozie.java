package org.dotoozie;

import org.dotoozie.dot.DotExecutable;
import org.dotoozie.graph.Edge;
import org.dotoozie.graph.GraphGenerator;
import org.dotoozie.graph.Vertex;
import org.dotoozie.graph.attribute.EdgeAttributeProvider;
import org.dotoozie.graph.attribute.VertexAttributeProvider;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;

import static org.dotoozie.Constants.DOT;
import static org.dotoozie.Constants.WORKFLOW;

final class DefaultDotOozie implements DotOozie {

    private File dot;
    private File workflow;
    private DirectedGraph<Vertex, Edge> graph;

    @Override
    public DefaultDotOozie givenDot(File dot) {
        this.dot = dot;
        return this;
    }

    @Override
    public DefaultDotOozie fromWorkflow(File workflow) throws Exception {
        this.workflow = workflow;
        this.graph = new GraphGenerator().constructGraph(new FileInputStream(workflow));
        return this;
    }

    DefaultDotOozie exportToDot(File dotFile) throws Exception {
        try (Writer writer = new FileWriter(dotFile)) {
            DOTExporter<Vertex, Edge> exporter = new DOTExporter<>(new IntegerNameProvider<>(),
                                                                   new StringNameProvider<>(),
                                                                   new StringEdgeNameProvider<>(),
                                                                   new VertexAttributeProvider(),
                                                                   new EdgeAttributeProvider());
            exporter.export(writer, graph);
        }
        return this;
    }

    @Override
    public DefaultDotOozie exportTo(String format) throws Exception {
        File output = new File(workflow.getParent(), WORKFLOW + "." + format);
        File dotFile = File.createTempFile(WORKFLOW, DOT);
        exportToDot(dotFile);
        DotExecutable dotExecutable = new DotExecutable(dot);
        dotExecutable.withArguments("-T" + format,
                                    dotFile.getAbsolutePath(),
                                    "-o" + output.getAbsolutePath())
                     .run();
        return this;
    }

}
