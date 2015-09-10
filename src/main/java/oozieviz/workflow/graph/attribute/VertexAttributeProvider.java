package oozieviz.workflow.graph.attribute;

import oozieviz.workflow.graph.Vertex;
import org.jgrapht.ext.ComponentAttributeProvider;

import java.io.IOException;
import java.util.Map;

public final class VertexAttributeProvider extends AttributeProvider implements ComponentAttributeProvider<Vertex> {

    private static final String VERTEX =      "vertex";
    private static final String COLOR =       "color";
    private static final String FILLCOLOR =   "fillcolor";
    private static final String FONTCOLOR =   "fontcolor";
    private static final String SHAPE =       "shape";
    private static final String STYLE =       "style";

    public VertexAttributeProvider() throws IOException {
        super();
    }

    @Override
    public Map<String, String> getComponentAttributes(Vertex vertex) {
        return attributes(VERTEX, vertex.type().name(), SHAPE, STYLE, FILLCOLOR, FONTCOLOR, COLOR);
    }

}
