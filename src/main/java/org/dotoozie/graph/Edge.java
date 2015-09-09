package org.dotoozie.graph;

import java.util.Objects;

public class Edge {

    private final String src;
    private final String dst;
    private final String label;
    private final EdgeType type;

    Edge(String src, String dst, String label, EdgeType type) {
        this.src = src;
        this.dst = dst;
        this.label = label;
        this.type = type;
    }

    String src() {
        return src;
    }

    String dst() {
        return dst;
    }

    public EdgeType type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(src, edge.src) &&
                Objects.equals(dst, edge.dst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dst);
    }

    @Override
    public String toString() {
        return label;
    }

}
