package org.dotoozie;

import org.jgrapht.ext.ComponentAttributeProvider;

import java.util.HashMap;
import java.util.Map;

class EdgeAttributeProvider implements ComponentAttributeProvider<Edge> {

    @Override
    public Map<String, String> getComponentAttributes(Edge edge) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("arrowsize", "0.5");
        attributes.put("fontsize", "7.0");
        attributes.put("style", edgeStyle(edge));
        return attributes;
    }

    private String edgeStyle(Edge edge) {
        switch (edge.type()) {
            case ERROR:
                return "dotted";
            default:
                return "solid";
        }
    }

}
