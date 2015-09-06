package org.dotoozie;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.*;

class GraphGenerator {

    static final String WORKFLOW_APP = "workflow-app";
    static final String START = "start";
    static final String END = "end";
    static final String AT = "@";
    static final String NAME = "name";
    static final String TO = "to";
    static final String KILL = "kill";
    static final String FORK = "fork";
    static final String PATH = "path";
    static final String JOIN = "join";
    static final String ACTION = "action";
    static final String OK = "ok";
    static final String ERROR = "error";
    static final String DECISION = "decision";
    static final String SWITCH = "switch";
    static final String CASE = "case";
    static final String DEFAULT = "default";

    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private final XPathFactory xPathFactory = XPathFactory.newInstance();
    private final Map<String, Vertex> vertices = new LinkedHashMap<>();
    private final Map<Vertex, List<Vertex>> edges = new LinkedHashMap<>();
    private Document doc;

    public DirectedGraph<Vertex, DefaultEdge> constructGraph(InputStream workflow) throws Exception {
        doc = documentBuilderFactory.newDocumentBuilder().parse(workflow);

        parseNodes();

        DirectedGraph<Vertex, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        for (Vertex vertex : vertices.values()) {
            graph.addVertex(vertex);
        }
        for (Map.Entry<Vertex, List<Vertex>> edge : edges.entrySet()) {
            for (Vertex vertex : edge.getValue()) {
                graph.addEdge(edge.getKey(), vertex);
            }
        }
        return graph;
    }

    private void parseNodes() throws Exception {
        String next = parseStartNode();

        parseNextNode(next);
    }

    private String parseStartNode() throws Exception {
        String workflow = workflowNameValue();
        String next = startNodeToValue();
        addVerticesAndEdge(workflow, VertexType.START, next);
        return next;
    }

    private void parseNextNode(String node) throws Exception {
        boolean success = addKillNode(node);
        if (!success) {
            success = addEndNode(node);
        }
        if (!success) {
            success = addForkNode(node);
        }
        if (!success) {
            success = addJoinNode(node);
        }
        if (!success) {
            success = addActionNode(node);
        }
        if (!success) {
            success = addDecisionNode(node);
        }
        if (!success) {
            throw new RuntimeException(String.format("Failed to add node '%s'", node));
        }
    }

    private boolean addKillNode(String node) throws Exception {
        Node kill = killNode(node);
        if (kill != null) {
            Vertex v = addVertex(node, VertexType.KILL);
            return true;
        } else {
            return false;
        }
    }

    private boolean addEndNode(String node) throws Exception {
        String name = nodeEndName();
        if (!name.isEmpty() && name.equals(node)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean addForkNode(String node) throws Exception {
        Node next = forkNode(node);

        if (next != null) {
            for (int i = 0; i < next.getChildNodes().getLength(); ++i) {
                String nextNode = forkPathStartValue(node, i);

                if (!nextNode.isEmpty()) {
                    addVerticesAndEdge(node, VertexType.FORK, nextNode);
                    parseNextNode(nextNode);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean addJoinNode(String node) throws Exception {
        String next = joinNodeToValue(node);

        if (!next.isEmpty()) {
            addVerticesAndEdge(node, VertexType.JOIN, next);
            parseNextNode(next);
            return true;
        } else {
            return false;
        }
    }

    private boolean addActionNode(String node) throws Exception {
        String next = actionOkNodeToValue(node);

        if (!next.isEmpty()) {
            addVerticesAndEdge(node, VertexType.ACTION, next);
            parseNextNode(next);

            next = actionErrorNodeToValue(node);
            if (!next.isEmpty()) {
                addVerticesAndEdge(node, VertexType.ACTION, next);
                parseNextNode(next);
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean addDecisionNode(String node) throws Exception {
        Node next = decisionSwitchNode(node);

        if (next != null) {
            for (int i = 0; i < next.getChildNodes().getLength(); ++i) {
                String nextNode = decisionSwitchCaseToValue(node, i);

                if (!nextNode.isEmpty()) {
                    addVerticesAndEdge(node, VertexType.DECISION, nextNode);
                    parseNextNode(nextNode);
                }
            }

            String nextNode = decisionSwitchDefaultToValue(node);

            if (!nextNode.isEmpty()) {
                addVerticesAndEdge(node, VertexType.DECISION, nextNode);
                parseNextNode(nextNode);
            }

            return true;
        } else {
            return false;
        }
    }

    private String workflowNameValue() throws Exception {
        return nodeNameValue("");
    }

    private String startNodeToValue() throws Exception {
        return nodeToValue(START);
    }

    private String forkPathStartValue(String node, int i) throws Exception {
        return nodeStartXPathExpression(nodeXPath(FORK, node) + "/" + PATH + "[" + i + "]").evaluate(doc);
    }

    private String joinNodeToValue(String node) throws Exception {
        return nodeToValue(nodeXPath(JOIN, node));
    }

    private String actionOkNodeToValue(String node) throws Exception {
        return nodeToValue(nodeXPath(ACTION, node) + "/" + OK);
    }

    private String actionErrorNodeToValue(String node) throws Exception {
        return nodeToValue(nodeXPath(ACTION, node) + "/" + ERROR);
    }

    private String nodeToValue(String node) throws Exception {
        return nodeToXPathExpression(node).evaluate(doc);
    }

    private String nodeEndName() throws Exception {
        return nodeNameValue(END);
    }

    private String nodeNameValue(String node) throws Exception {
        return nodeNameXPathExpression(node).evaluate(doc);
    }

    private String decisionSwitchCaseToValue(String node, int i) throws Exception {
        return nodeToValue(decisionSwitchNodeXPath(node) + "/" + CASE + "[" + i + "]");
    }

    private String decisionSwitchDefaultToValue(String node) throws Exception {
        return nodeToValue(decisionSwitchNodeXPath(node) + "/" + DEFAULT);
    }

    private Node forkNode(String node) throws Exception {
        return node(FORK, node);
    }

    private Node killNode(String node) throws Exception {
        return node(KILL, node);
    }

    private Node decisionSwitchNode(String node) throws Exception {
        return (Node) nodeXPathExpression(decisionSwitchNodeXPath(node)).evaluate(doc, XPathConstants.NODE);
    }

    private String decisionSwitchNodeXPath(String node) {
        return nodeXPath(DECISION, node) + "/" + SWITCH;
    }

    private Node node(String node, String name) throws Exception {
        return (Node) nodeXPathExpression(nodeXPath(node, name)).evaluate(doc, XPathConstants.NODE);
    }

    private String nodeXPath(String node, String name) {
        return node + "[" + AT + NAME + "='" + name + "']";
    }

    private XPathExpression nodeToXPathExpression(String node) throws Exception {
        return nodeAttributeXPathExpression(node, TO);
    }

    private XPathExpression nodeStartXPathExpression(String node) throws Exception {
        return nodeAttributeXPathExpression(node, START);
    }

    private XPathExpression nodeNameXPathExpression(String node) throws Exception {
        return nodeAttributeXPathExpression(node, NAME);
    }

    private XPathExpression nodeAttributeXPathExpression(String node, String attribute) throws Exception {
        return nodeXPathExpression(node + "/" + AT + attribute);
    }

    private XPathExpression nodeXPathExpression(String node) throws Exception {
        return xPathFactory.newXPath().compile("/" + WORKFLOW_APP + "/" + node);
    }

    private void addVerticesAndEdge(String node1, VertexType type1, String node2) {
        Vertex v1 = addVertex(node1, type1);
        Vertex v2 = addVertex(node2, VertexType.END);

        List<Vertex> connectedVertices = edges.get(v1);
        if (connectedVertices == null) {
            connectedVertices = new ArrayList<>();
            edges.put(v1, connectedVertices);
        }
        connectedVertices.add(v2);
    }

    private Vertex addVertex(String node, VertexType type) {
        Vertex v = vertices.get(node);
        if (v == null) {
            v = new Vertex(node);
            vertices.put(node, v);
        }
        v.type(type);
        return v;
    }

}
