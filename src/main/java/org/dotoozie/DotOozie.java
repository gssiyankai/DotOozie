package org.dotoozie;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;

public class DotOozie {

    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private final XPathFactory xPathFactory = XPathFactory.newInstance();

    public void export(InputStream workflow, String outputFile) throws Exception {
        DirectedGraph<Node, DefaultEdge> g = constructGraph(workflow);

        try (Writer writer = new FileWriter(outputFile)) {
            DOTExporter exporter = new DOTExporter(new StringNameProvider<>(), null, null);
            exporter.export(writer, g);
        }
    }

    private DirectedGraph<Node, DefaultEdge> constructGraph(InputStream workflow) throws Exception {
        DirectedGraph<Node, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        Document doc = documentBuilderFactory.newDocumentBuilder().parse(workflow);

        addNodes(g, doc);

        return g;
    }

    private void addNodes(DirectedGraph<Node, DefaultEdge> g, Document doc) throws Exception {
        String next = addStartNode(g, doc);

        addNextNode(g, doc, next);
    }

    private String addStartNode(DirectedGraph<Node, DefaultEdge> g, Document doc) throws Exception {
        XPathExpression expr = nodeToXPathExpression("start");
        String next = expr.evaluate(doc);

        addEdge(g, "start", next);
        return next;
    }

    private void addNextNode(DirectedGraph<Node, DefaultEdge> g, Document doc, String node) throws Exception {
        boolean success = addKillNode(g, doc, node);
        if (!success) {
            success = addEndNode(g, doc, node);
        }
        if (!success) {
            success = addActionNode(g, doc, node);
        }
        if (!success) {
            success = addDecisionNode(g, doc, node);
        }
        if (!success) {
            throw new RuntimeException(String.format("Failed to add node '%s'", node));
        }
    }

    private boolean addEndNode(DirectedGraph<Node, DefaultEdge> g, Document doc, String node) throws Exception {
        XPathExpression expr = nodeNameXPathExpression("end");
        String name = expr.evaluate(doc);

        if (!name.isEmpty() && name.equals(node)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean addKillNode(DirectedGraph<Node, DefaultEdge> g, Document doc, String node) throws Exception {
        XPathExpression expr = nodeXPathExpression("kill[@name='" + node + "']");
        org.w3c.dom.Node kill = (org.w3c.dom.Node) expr.evaluate(doc, XPathConstants.NODE);

        if (kill != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean addActionNode(DirectedGraph<Node, DefaultEdge> g, Document doc, String node) throws Exception {
        XPathExpression expr = nodeToXPathExpression("action[@name='" + node + "']/ok");
        String next = expr.evaluate(doc);

        if (!next.isEmpty()) {
            addEdge(g, node, next);
            addNextNode(g, doc, next);

            expr = nodeToXPathExpression("action[@name='" + node + "']/error");
            next = expr.evaluate(doc);

            if (!next.isEmpty()) {
                addEdge(g, node, next);
                addNextNode(g, doc, next);
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean addDecisionNode(DirectedGraph<Node, DefaultEdge> g, Document doc, String node) throws Exception {
        XPathExpression expr = nodeXPathExpression("decision[@name='" + node + "']/switch");
        org.w3c.dom.Node next = (org.w3c.dom.Node) expr.evaluate(doc, XPathConstants.NODE);

        if (next != null) {
            for (int i = 0; i < next.getChildNodes().getLength(); ++i) {
                expr = nodeToXPathExpression("decision[@name='" + node + "']/switch/case[" + i + "]");
                String nextNode = expr.evaluate(doc);

                if (!nextNode.isEmpty()) {
                    addEdge(g, node, nextNode);
                    addNextNode(g, doc, nextNode);
                }
            }

            expr = nodeToXPathExpression("decision[@name='" + node + "']/switch/default");
            String nextNode = expr.evaluate(doc);

            if (!nextNode.isEmpty()) {
                addEdge(g, node, nextNode);
                addNextNode(g, doc, nextNode);
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

    private void addEdge(DirectedGraph<Node, DefaultEdge> g, String node1, String node2) {
        Node startNode = new Node(node1);
        Node toNode = new Node(node2);
        g.addVertex(startNode);
        g.addVertex(toNode);
        g.addEdge(startNode, toNode);
    }


    public static final void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Expected arguments: <Oozie workflow xml> <dot output file>");
        }

        DotOozie dotOozie = new DotOozie();
        dotOozie.export(new FileInputStream(args[0]), args[1]);
    }

}
