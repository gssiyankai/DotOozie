package org.dotoozie;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class DotOozieTest {

    private DotOozie dotOozie;

    @Before
    public void setup() {
        dotOozie = new DotOozie();
    }

    @Test
    public void it_exports_empty_workflow() throws Exception {
        it_exports_workflow("empty_workflow.xml", "empty_workflow.dot");
    }

    @Test
    public void it_exports_action_workflow() throws Exception {
        it_exports_workflow("action_workflow.xml", "action_workflow.dot");
    }

    @Test
    public void it_exports_decision_workflow() throws Exception {
        it_exports_workflow("decision_workflow.xml", "decision_workflow.dot");
    }

    @Test
    public void it_exports_simple_workflow() throws Exception {
        it_exports_workflow("simple_workflow.xml", "simple_workflow.dot");
    }

    @Test
    public void it_exports_forkjoin_workflow() throws Exception {
        it_exports_workflow("forkjoin_workflow.xml", "forkjoin_workflow.dot");
    }

    private void it_exports_workflow(String workflowFile, String expectedDotFile) throws Exception {
        File dot = File.createTempFile("worflow", "dot");
        dotOozie.export(getClass().getResourceAsStream("/" + workflowFile), dot.getAbsolutePath());
        assertEquals(IOUtils.toString(getClass().getResourceAsStream("/" + expectedDotFile)),
                IOUtils.toString(new FileReader(dot.getAbsolutePath())));
    }

}
