package oozieviz.workflow.graph.attribute;

import oozieviz.workflow.graph.Vertex;
import oozieviz.workflow.job.JobProperties;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static oozieviz.Constants.WORKFLOW_SVG;

public final class UrlVertexAttributeProvider implements VertexAttributeProvider {

    private static final String URL = "URL";

    private final File workflow;
    private final JobProperties jobProperties;

    public UrlVertexAttributeProvider(File workflow, JobProperties jobProperties) {
        this.workflow = workflow;
        this.jobProperties = jobProperties;
    }

    @Override
    public Map<String, String> getComponentAttributes(Vertex vertex) {
        Map<String, String> attributes = new HashMap<>();
        Optional<String> subWfPath = vertex.subWfPath();
        if (subWfPath.isPresent()) {
            String expandedSubWfPath = jobProperties.replaceTokens(subWfPath.get());
            File subWfSvg = new File(expandedSubWfPath, WORKFLOW_SVG);
            String url = Paths.get(workflow.getParentFile().toURI()).relativize(Paths.get(subWfSvg.toURI()))
                              .toString()
                              .replace("\\", "/");
            attributes.put(URL, url);
        }
        return attributes;
    }

}
