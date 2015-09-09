package org.dotoozie;

import org.jgrapht.ext.ComponentAttributeProvider;

import java.io.IOException;
import java.util.Map;

class EdgeAttributeProvider extends AttributeProvider implements ComponentAttributeProvider<Edge> {

    private static final String EDGE =        "edge";
    private static final String ARROWSIZE =   "arrowsize";
    private static final String FONTSIZE =    "fontsize";
    private static final String STYLE =       "style";

    EdgeAttributeProvider() throws IOException {
        super();
    }

    @Override
    public Map<String, String> getComponentAttributes(Edge edge) {
        return attributes(EDGE, edge.type().name(), ARROWSIZE, FONTSIZE, STYLE);
    }

}
