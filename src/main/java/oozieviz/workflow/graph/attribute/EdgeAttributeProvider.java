package oozieviz.workflow.graph.attribute;

import oozieviz.workflow.graph.Edge;
import org.jgrapht.ext.ComponentAttributeProvider;

import java.io.IOException;
import java.util.Map;

public final class EdgeAttributeProvider extends AttributeProvider implements ComponentAttributeProvider<Edge> {

    private static final String EDGE =        "edge";
    private static final String ARROWSIZE =   "arrowsize";
    private static final String FONTSIZE =    "fontsize";
    private static final String STYLE =       "style";

    public EdgeAttributeProvider() throws IOException {
        super();
    }

    @Override
    public Map<String, String> getComponentAttributes(Edge edge) {
        return attributes(EDGE, edge.type().name(), ARROWSIZE, FONTSIZE, STYLE);
    }

}
