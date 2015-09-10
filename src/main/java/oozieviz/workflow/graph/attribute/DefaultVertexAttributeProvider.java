package oozieviz.workflow.graph.attribute;

import oozieviz.workflow.graph.Vertex;

import java.io.IOException;
import java.util.Map;

public final class DefaultVertexAttributeProvider extends AttributeProvider implements VertexAttributeProvider {

    private static final String VERTEX =      "vertex";
    private static final String COLOR =       "color";
    private static final String FILLCOLOR =   "fillcolor";
    private static final String FONTCOLOR =   "fontcolor";
    private static final String SHAPE =       "shape";
    private static final String STYLE =       "style";

    public DefaultVertexAttributeProvider() throws IOException {
        super();
    }

    @Override
    public Map<String, String> getComponentAttributes(Vertex vertex) {
        return attributes(VERTEX, vertex.type().name(), SHAPE, STYLE, FILLCOLOR, FONTCOLOR, COLOR);
    }

}
