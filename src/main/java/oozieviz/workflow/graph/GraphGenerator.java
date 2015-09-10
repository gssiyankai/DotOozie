package oozieviz.workflow.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static javax.xml.xpath.XPathConstants.NODE;

public class GraphGenerator {

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
    static final String SUB_WORKFLOW = "sub-workflow";
    static final String APP_PATH = "app-path";
    static final String OK = "ok";
    static final String ERROR = "error";
    static final String DECISION = "decision";
    static final String SWITCH = "switch";
    static final String CASE = "case";
    static final String DEFAULT = "default";
    static final String TEXT = "text()";

    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private final XPathFactory xPathFactory = XPathFactory.newInstance();
    private final Map<String, Vertex> vertices = new LinkedHashMap<>();
    private final List<Edge> edges = new ArrayList<>();
    private Document doc;

    public DirectedGraph<Vertex, Edge> constructGraph(File workflow) throws Exception {
        doc = documentBuilderFactory.newDocumentBuilder()
                                    .parse(new FileInputStream(workflow));

        parseNodes();

        DirectedGraph<Vertex, Edge> graph = new DefaultDirectedGraph<>(Edge.class);
        for (Vertex vertex : vertices.values()) {
            graph.addVertex(vertex);
        }
        for (Edge edge : edges) {
            Vertex src = vertices.get(edge.src());
            Vertex dst = vertices.get(edge.dst());
            graph.addEdge(src, dst, edge);
        }
        return graph;
    }

    private void parseNodes() throws Exception {
        String next = parseStartNode();

        parseNextNode(next);
    }

    private String parseStartNode() throws Exception {
        String workflow = workflowNameValue();
        addVertex(workflow, VertexType.START);

        String next = startNodeToValue();

        addEdge(workflow, next);

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
            addVertex(node, VertexType.KILL);
            return true;
        } else {
            return false;
        }
    }

    private boolean addEndNode(String node) throws Exception {
        Node end = endNode(node);
        if (end != null) {
            addVertex(node, VertexType.END);
            return true;
        } else {
            return false;
        }
    }

    private boolean addForkNode(String node) throws Exception {
        Node fork = forkNode(node);

        if (fork != null) {
            for (int i = 0; i < fork.getChildNodes().getLength(); ++i) {
                String nextStart = forkPathStartValue(node, i);

                if (!nextStart.isEmpty()) {
                    addVertex(node, VertexType.FORK);
                    addEdge(node, nextStart);
                    parseNextNode(nextStart);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean addJoinNode(String node) throws Exception {
        Node join = joinNode(node);

        if (join != null) {
            addVertex(node, VertexType.JOIN);

            String next = joinNodeToValue(node);
            addEdge(node, next);

            parseNextNode(next);

            return true;
        } else {
            return false;
        }
    }

    private boolean addActionNode(String node) throws Exception {
        Node action = actionNode(node);

        if (action != null) {
            Optional<String> subWfAppPath = Optional.empty();
            Node subWfAppPathNode = actionSubWfAppPathNode(node);
            if (subWfAppPathNode != null) {
                subWfAppPath = Optional.of(actionSubWfAppPathText(node));
            }
            addVertex(node, VertexType.ACTION, subWfAppPath);

            String next = actionOkNodeToValue(node);
            addEdge(node, next);

            parseNextNode(next);

            next = actionErrorNodeToValue(node);
            if (!next.isEmpty()) {
                addEdge(node, next, "", EdgeType.ERROR);
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
                String nextCaseTo = decisionSwitchCaseToValue(node, i);

                if (!nextCaseTo.isEmpty()) {
                    String txt = decisionSwitchCaseText(node, i);
                    addVertex(node, VertexType.DECISION);
                    addEdge(node, nextCaseTo, txt, EdgeType.DEFAULT);
                    parseNextNode(nextCaseTo);
                }
            }

            String nextDefault = decisionSwitchDefaultToValue(node);

            if (!nextDefault.isEmpty()) {
                addVertex(node, VertexType.DECISION);
                addEdge(node, nextDefault, DEFAULT, EdgeType.DEFAULT);
                parseNextNode(nextDefault);
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

    private String actionSubWfAppPathText(String node) throws Exception {
        return nodeText(actionSubWfAppPathNodeXPath(node));
    }

    private Node actionSubWfAppPathNode(String node) throws Exception {
        return (Node) nodeXPathExpression(actionSubWfAppPathNodeXPath(node)).evaluate(doc, NODE);
    }

    private String actionSubWfAppPathNodeXPath(String node) {
        return nodeXPath(ACTION, node) + "/" + SUB_WORKFLOW + "/" + APP_PATH;
    }

    private String actionErrorNodeToValue(String node) throws Exception {
        return nodeToValue(nodeXPath(ACTION, node) + "/" + ERROR);
    }

    private String decisionSwitchCaseToValue(String node, int i) throws Exception {
        return nodeToValue(decisionSwitchCaseNodeXPath(node, i));
    }

    private String decisionSwitchCaseText(String node, int i) throws Exception {
        return nodeText(decisionSwitchCaseNodeXPath(node, i));
    }

    private String decisionSwitchCaseNodeXPath(String node, int i) {
        return decisionSwitchNodeXPath(node) + "/" + CASE + "[" + i + "]";
    }

    private String decisionSwitchDefaultToValue(String node) throws Exception {
        return nodeToValue(decisionSwitchNodeXPath(node) + "/" + DEFAULT);
    }

    private String nodeToValue(String node) throws Exception {
        return nodeToXPathExpression(node).evaluate(doc);
    }

    private Node actionNode(String node) throws Exception {
        return node(ACTION, node);
    }

    private Node joinNode(String node) throws Exception {
        return node(JOIN, node);
    }

    private Node endNode(String node) throws Exception {
        return node(END, node);
    }

    private Node forkNode(String node) throws Exception {
        return node(FORK, node);
    }

    private Node killNode(String node) throws Exception {
        return node(KILL, node);
    }

    private Node decisionSwitchNode(String node) throws Exception {
        return (Node) nodeXPathExpression(decisionSwitchNodeXPath(node)).evaluate(doc, NODE);
    }

    private String decisionSwitchNodeXPath(String node) {
        return nodeXPath(DECISION, node) + "/" + SWITCH;
    }

    private String nodeNameValue(String node) throws Exception {
        return nodeNameXPathExpression(node).evaluate(doc);
    }

    private String nodeText(String node) throws Exception {
        return nodeXPathExpression(node + "/" + TEXT).evaluate(doc).trim();
    }

    private Node node(String node, String name) throws Exception {
        return (Node) nodeXPathExpression(nodeXPath(node, name)).evaluate(doc, NODE);
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

    private void addVertex(String node, VertexType type) {
        addVertex(node, type, Optional.empty());
    }

    private void addVertex(String node, VertexType type, Optional<String> subWfPath) {
        Vertex v = new Vertex(node, type, subWfPath);
        vertices.put(node, v);
    }

    private void addEdge(String src, String dst) {
        addEdge(src, dst, "", EdgeType.DEFAULT);
    }

    private void addEdge(String src, String dst, String label, EdgeType type) {
        Edge edge = new Edge(src, dst, label, type);
        edges.add(edge);
    }

}
