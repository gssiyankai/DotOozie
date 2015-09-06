package org.dotoozie;

import java.util.Objects;

class Vertex {

    private final String id;
    private VertexType type;

    Vertex(String id) {
        this.id = id;
    }

    void type(VertexType type) {
        this.type = type;
    }

    VertexType type() {
        return type;
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
