package oozieviz.workflow.graph;

import java.util.Objects;
import java.util.Optional;

public class Vertex {

    private final String id;
    private final VertexType type;
    private final Optional<String> subWfPath;

    Vertex(String id, VertexType type, Optional<String> subWfPath) {
        this.id = id;
        this.type = type;
        this.subWfPath = subWfPath;
    }

    public VertexType type() {
        return type;
    }

    public Optional<String> subWfPath() {
        return subWfPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(id, vertex.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

}
