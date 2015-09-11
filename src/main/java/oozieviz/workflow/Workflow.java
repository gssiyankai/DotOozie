package oozieviz.workflow;

import oozieviz.workflow.graph.Edge;
import oozieviz.workflow.graph.GraphGenerator;
import oozieviz.workflow.graph.Vertex;
import oozieviz.workflow.job.PathResolver;
import org.jgrapht.DirectedGraph;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import static oozieviz.Constants.WORKFLOW_XML;

public final class Workflow {

    private final File path;
    private final DirectedGraph<Vertex, Edge> graph;
    private final Collection<Workflow> subWfs;

    private Workflow(File path, DirectedGraph<Vertex, Edge> graph, Collection<Workflow> subWfs) {
        this.path = path;
        this.graph = graph;
        this.subWfs = subWfs;
    }

    public File path() {
        return path;
    }

    public DirectedGraph<Vertex, Edge> graph() {
        return graph;
    }

    public Collection<Workflow> subWfs() {
        return subWfs;
    }

    public static Workflow newWorkFlow(File workflowXml, PathResolver pathResolver) throws Exception {
        DirectedGraph<Vertex, Edge> graph = new GraphGenerator().constructGraph(workflowXml);

        Collection<Workflow> subWfs = new LinkedList<>();
        for (Vertex vertex : graph.vertexSet()) {
            Optional<String> subWfPath = vertex.subWfPath();
            if (subWfPath.isPresent()) {
                String resolvedSubWfPath = pathResolver.resolvePath(subWfPath.get());
                File subWfXml = new File(resolvedSubWfPath, WORKFLOW_XML);
                Workflow subWf = newWorkFlow(subWfXml, pathResolver);
                subWfs.add(subWf);
            }
        }

        return new Workflow(workflowXml, graph, subWfs);
    }

}
