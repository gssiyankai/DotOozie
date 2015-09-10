package oozieviz.workflow.graph.attribute;

import oozieviz.workflow.graph.Vertex;

import java.util.HashMap;
import java.util.Map;

public final class CompositeVertexAttributeProvider implements VertexAttributeProvider {

    private final VertexAttributeProvider[] providers;

    public CompositeVertexAttributeProvider(VertexAttributeProvider... providers) {
        this.providers = providers;
    }

    @Override
    public Map<String, String> getComponentAttributes(Vertex component) {
        Map<String, String> attributes = new HashMap<>();
        for (VertexAttributeProvider provider : providers) {
            attributes.putAll(provider.getComponentAttributes(component));
        }
        return attributes;
    }
}
