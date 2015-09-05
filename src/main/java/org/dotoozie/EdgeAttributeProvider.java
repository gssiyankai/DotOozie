package org.dotoozie;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.ext.ComponentAttributeProvider;

import java.util.HashMap;
import java.util.Map;

public class EdgeAttributeProvider implements ComponentAttributeProvider<DefaultEdge> {

    @Override
    public Map<String, String> getComponentAttributes(DefaultEdge edge) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("arrowsize", "0.5");
        return attributes;
    }

}
