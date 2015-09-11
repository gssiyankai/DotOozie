package oozieviz;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static oozieviz.Constants.WORKFLOW_DOT;
import static oozieviz.Constants.WORKFLOW_XML;
import static oozieviz.ResourceHelper.resourceFile;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(Parameterized.class)
public class OozieVizTest {

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][] {
                        { "empty" },
                        { "action" },
                        { "decision" },
                        { "simple" },
                        { "forkjoin" }
                });
    }

    @Parameter
    public String usecase;


    private OozieViz oozieViz;

    @Before
    public void setup() {
        oozieViz = new OozieViz();
    }


    @Test
    public void it_exports_workflow_to_dot() throws Exception {
        File workflow = resourceFile(usecase + "_" + WORKFLOW_XML);
        File dot = new File(workflow.getParent(), WORKFLOW_DOT);

        oozieViz.fromWorkflow(workflow)
                .exportToDot();

        assertThat(dot).hasSameContentAs(resourceFile(usecase + "_" + WORKFLOW_DOT));
    }

}
