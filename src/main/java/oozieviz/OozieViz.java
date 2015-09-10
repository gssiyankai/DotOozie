package oozieviz;

import oozieviz.utils.graphviz.DotExecutable;
import oozieviz.workflow.Workflow;
import oozieviz.workflow.graph.Edge;
import oozieviz.workflow.graph.Vertex;
import oozieviz.workflow.graph.attribute.EdgeAttributeProvider;
import oozieviz.workflow.graph.attribute.VertexAttributeProvider;
import oozieviz.workflow.job.JobProperties;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

import static oozieviz.Constants.DOT;
import static oozieviz.Constants.SVG;
import static oozieviz.Constants.WORKFLOW;
import static oozieviz.workflow.Workflow.newWorkFlow;
import static oozieviz.workflow.job.JobProperties.newJobProperties;

public final class OozieViz {

    private DotExecutable dotExe;
    private JobProperties jobProperties;
    private Workflow workflow;

    public OozieViz givenDot(File dot) {
        this.dotExe = new DotExecutable(dot);
        return this;
    }

    public OozieViz givenOptionalJobProperties(Optional<File> props) throws Exception {
        this.jobProperties = newJobProperties(props);
        return this;
    }

    public OozieViz fromWorkflow(File workflowXml) throws Exception {
        this.workflow = newWorkFlow(workflowXml, jobProperties);
        return this;
    }

    public OozieViz exportToDot() throws Exception {
        return exportWorkflowToDot(workflow);
    }

    private OozieViz exportWorkflowToDot(Workflow workflow) throws IOException {
        File dotFile = workflowOutputFile(workflow, DOT);
        try (Writer writer = new FileWriter(dotFile)) {
            DOTExporter<Vertex, Edge> exporter = new DOTExporter<>(new IntegerNameProvider<>(),
                                                                   new StringNameProvider<>(),
                                                                   new StringEdgeNameProvider<>(),
                                                                   new VertexAttributeProvider(),
                                                                   new EdgeAttributeProvider());
            exporter.export(writer, workflow.graph());
        }

        for (Workflow subWf : workflow.subWfs()) {
            exportWorkflowToDot(subWf);
        }

        return this;
    }

    public OozieViz exportToSvg() throws Exception {
        exportWorkflowToSvg(workflow);
        return this;
    }

    private void exportWorkflowToSvg(Workflow workflow) throws Exception {
        File outputFile = workflowOutputFile(workflow, SVG);
        File dotFile = workflowOutputFile(workflow, DOT);

        exportWorkflowToDot(workflow);

        dotExe.withArguments("-T" + SVG,
                             dotFile.getAbsolutePath(),
                             "-o" + outputFile.getAbsolutePath())
              .run();

        for (Workflow subWf : workflow.subWfs()) {
            exportWorkflowToSvg(subWf);
        }
    }

    private static File workflowOutputFile(Workflow workflow, String extension) {
        return new File(workflow.path().getParent(), WORKFLOW + "." + extension);
    }

}
