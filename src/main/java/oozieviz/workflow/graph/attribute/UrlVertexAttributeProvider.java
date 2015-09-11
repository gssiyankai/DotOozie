package oozieviz.workflow.graph.attribute;

import oozieviz.workflow.graph.Vertex;
import oozieviz.workflow.job.PathResolver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static oozieviz.Constants.WORKFLOW_SVG;

public final class UrlVertexAttributeProvider implements VertexAttributeProvider {

    private static final String URL = "URL";

    private final File workflow;
    private final PathResolver pathResolver;

    public UrlVertexAttributeProvider(File workflow, PathResolver pathResolver) {
        this.workflow = workflow;
        this.pathResolver = pathResolver;
    }

    @Override
    public Map<String, String> getComponentAttributes(Vertex vertex) {
        Map<String, String> attributes = new HashMap<>();
        Optional<String> subWfPath = vertex.subWfPath();
        if (subWfPath.isPresent()) {
            String resolvedSubWfPath = pathResolver.resolvePath(subWfPath.get());
            File subWfSvg = new File(resolvedSubWfPath, WORKFLOW_SVG);
            String url = pathResolver.relativize(workflow.getParent(), subWfSvg.getPath());
            attributes.put(URL, url);
        }
        return attributes;
    }

}
