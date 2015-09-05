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
        it_exports_workflow("empty");
    }

    @Test
    public void it_exports_action_workflow() throws Exception {
        it_exports_workflow("action");
    }

    @Test
    public void it_exports_decision_workflow() throws Exception {
        it_exports_workflow("decision");
    }

    @Test
    public void it_exports_simple_workflow() throws Exception {
        it_exports_workflow("simple");
    }

    @Test
    public void it_exports_forkjoin_workflow() throws Exception {
        it_exports_workflow("forkjoin");
    }

    private void it_exports_workflow(String prefix) throws Exception {
        File dot = File.createTempFile("worflow", "dot");
        dotOozie.from(getClass().getResourceAsStream("/" + prefix + "_workflow.xml"))
                .exportTo(dot.getAbsolutePath());
        assertEquals(IOUtils.toString(getClass().getResourceAsStream("/" + prefix + "_workflow.dot")),
                     IOUtils.toString(new FileReader(dot.getAbsolutePath())));
    }

}
