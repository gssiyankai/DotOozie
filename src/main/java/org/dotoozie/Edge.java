package org.dotoozie;

import java.util.Objects;

class Edge {

    private final Vertex src;
    private final Vertex dst;
    private final String label;

    Edge(Vertex src, Vertex dst, String label) {
        this.src = src;
        this.dst = dst;
        this.label = label;
    }

    Vertex src() {
        return src;
    }

    Vertex dst() {
        return dst;
    }

    String label() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(src, edge.src) &&
                Objects.equals(dst, edge.dst) &&
                Objects.equals(label, edge.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dst, label);
    }

    @Override
    public String toString() {
        return label;
    }

}
