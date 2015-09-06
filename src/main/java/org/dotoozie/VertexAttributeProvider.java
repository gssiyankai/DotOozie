package org.dotoozie;

import org.jgrapht.ext.ComponentAttributeProvider;

import java.util.HashMap;
import java.util.Map;

public class VertexAttributeProvider implements ComponentAttributeProvider<Vertex> {

    @Override
    public Map<String, String> getComponentAttributes(Vertex vertex) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("shape", "box");
        attributes.put("style", "rounded,filled");
        attributes.put("fillcolor", vertexFillColor(vertex));
        attributes.put("fontcolor", vertexFontColor(vertex));
        attributes.put("color", vertexColor(vertex));
        return attributes;
    }

    private String vertexColor(Vertex vertex) {
        switch (vertex.type()) {
            case START:
            case END:
                return "#1A5998";
            case FORK:
            case JOIN:
                return "#004080";
            case KILL:
                return "#004080";
            default:
                return "#1A5490";
        }
    }

    private String vertexFontColor(Vertex vertex) {
        switch (vertex.type()) {
            case START:
            case END:
                return "#1A5998";
            case FORK:
            case JOIN:
                return "#FFFFFF";
            case KILL:
                return "#FFFFFF";
            default:
                return "#1A5490";
        }
    }

    private String vertexFillColor(Vertex vertex) {
        switch (vertex.type()) {
            case START:
            case END:
                return "#68A3DF";
            case FORK:
            case JOIN:
                return "#BF1E1B";
            case KILL:
                return "#BF1E1B";
            default:
                return "#D3DFFF";
        }
    }

}
