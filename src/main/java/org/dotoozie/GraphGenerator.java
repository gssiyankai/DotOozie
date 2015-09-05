package org.dotoozie;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

class GraphGenerator {

    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private final XPathFactory xPathFactory = XPathFactory.newInstance();
    private DirectedGraph<Node, DefaultEdge> graph;
    private Document doc;

    public DirectedGraph<Node, DefaultEdge> constructGraph(InputStream workflow) throws Exception {
        graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        doc = documentBuilderFactory.newDocumentBuilder().parse(workflow);

        addNodes();

        return graph;
    }

    private void addNodes() throws Exception {
        String next = addStartNode();

        addNextNode(next);
    }

    private String addStartNode() throws Exception {
        XPathExpression expr = nodeToXPathExpression("start");
        String next = expr.evaluate(doc);

        addEdge("start", next);
        return next;
    }

    private void addNextNode(String node) throws Exception {
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
        XPathExpression expr = nodeXPathExpression("kill[@name='" + node + "']");
        org.w3c.dom.Node kill = (org.w3c.dom.Node) expr.evaluate(doc, XPathConstants.NODE);

        if (kill != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean addEndNode(String node) throws Exception {
        XPathExpression expr = nodeNameXPathExpression("end");
        String name = expr.evaluate(doc);

        if (!name.isEmpty() && name.equals(node)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean addForkNode(String node) throws Exception {
        XPathExpression expr = nodeXPathExpression("fork[@name='" + node + "']");
        org.w3c.dom.Node next = (org.w3c.dom.Node) expr.evaluate(doc, XPathConstants.NODE);

        if (next != null) {
            for (int i = 0; i < next.getChildNodes().getLength(); ++i) {
                expr = nodeXPathExpression("fork[@name='" + node + "']/path[" + i + "]/@start");
                String nextNode = expr.evaluate(doc);

                if (!nextNode.isEmpty()) {
                    addEdge(node, nextNode);
                    addNextNode(nextNode);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean addJoinNode(String node) throws Exception {
        XPathExpression expr = nodeToXPathExpression("join[@name='" + node + "']");
        String next = expr.evaluate(doc);

        if (!next.isEmpty()) {
            addEdge(node, next);
            addNextNode(next);

            return true;
        } else {
            return false;
        }
    }

    private boolean addActionNode(String node) throws Exception {
        XPathExpression expr = nodeToXPathExpression("action[@name='" + node + "']/ok");
        String next = expr.evaluate(doc);

        if (!next.isEmpty()) {
            addEdge(node, next);
            addNextNode(next);

            expr = nodeToXPathExpression("action[@name='" + node + "']/error");
            next = expr.evaluate(doc);

            if (!next.isEmpty()) {
                addEdge(node, next);
                addNextNode(next);
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean addDecisionNode(String node) throws Exception {
        XPathExpression expr = nodeXPathExpression("decision[@name='" + node + "']/switch");
        org.w3c.dom.Node next = (org.w3c.dom.Node) expr.evaluate(doc, XPathConstants.NODE);

        if (next != null) {
            for (int i = 0; i < next.getChildNodes().getLength(); ++i) {
                expr = nodeToXPathExpression("decision[@name='" + node + "']/switch/case[" + i + "]");
                String nextNode = expr.evaluate(doc);

                if (!nextNode.isEmpty()) {
                    addEdge(node, nextNode);
                    addNextNode(nextNode);
                }
            }

            expr = nodeToXPathExpression("decision[@name='" + node + "']/switch/default");
            String nextNode = expr.evaluate(doc);

            if (!nextNode.isEmpty()) {
                addEdge(node, nextNode);
                addNextNode(nextNode);
            }

            return true;
        } else {
            return false;
        }
    }

    private XPathExpression nodeToXPathExpression(String node) throws Exception {
        return nodeXPathExpression(node + "/@to");
    }

    private XPathExpression nodeNameXPathExpression(String node) throws Exception {
        return nodeXPathExpression(node + "/@name");
    }

    private XPathExpression nodeXPathExpression(String node) throws Exception {
        return xPathFactory.newXPath().compile("/workflow-app/" + node);
    }

    private void addEdge(String node1, String node2) {
        Node startNode = new Node(node1);
        Node toNode = new Node(node2);
        graph.addVertex(startNode);
        graph.addVertex(toNode);
        graph.addEdge(startNode, toNode);
    }

}
