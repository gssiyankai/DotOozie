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
        return attributes;
    }

}
