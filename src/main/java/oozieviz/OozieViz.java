package oozieviz;

import oozieviz.utils.graphviz.DotExecutable;
import oozieviz.workflow.Workflow;
import oozieviz.workflow.graph.Edge;
import oozieviz.workflow.graph.Vertex;
import oozieviz.workflow.graph.attribute.*;
import oozieviz.workflow.job.JobProperties;
import org.jgrapht.ext.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Optional;

import static oozieviz.Constants.*;
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

    public OozieViz givenJobProperties(Optional<File> props) throws Exception {
        this.jobProperties = newJobProperties(props);
        return this;
    }

    public OozieViz fromWorkflow(File workflowXml) throws Exception {
        this.workflow = newWorkFlow(workflowXml, jobProperties);
        return this;
    }

    public OozieViz exportToDot() throws Exception {
        return exportWorkflowToDot(workflow, new DefaultVertexAttributeProvider());
    }

    private OozieViz exportWorkflowToDot(Workflow workflow, VertexAttributeProvider vertexAttributeProvider) throws Exception {
        File dotFile = workflowOutputFile(workflow, DOT);
        try (Writer writer = new FileWriter(dotFile)) {
            DOTExporter<Vertex, Edge> exporter = new DOTExporter<>(new IntegerNameProvider<>(),
                                                                   new StringNameProvider<>(),
                                                                   new StringEdgeNameProvider<>(),
                                                                   vertexAttributeProvider,
                                                                   new EdgeAttributeProvider());
            exporter.export(writer, workflow.graph());
        }

        for (Workflow subWf : workflow.subWfs()) {
            exportWorkflowToDot(subWf, vertexAttributeProvider);
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

        exportWorkflowToDot(workflow,
                            new CompositeVertexAttributeProvider(
                                    new DefaultVertexAttributeProvider(),
                                    new UrlVertexAttributeProvider(workflow.path(), jobProperties)));

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
