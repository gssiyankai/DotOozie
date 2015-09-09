package org.dotoozie.launch;

import org.apache.commons.lang.SystemUtils;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileNotFoundException;

public final class LauncherOptions {

    private static final String WORKFLOW_XML = "workflow.xml";
    private static final String DOT = "dot";
    private static final String DOT_EXTENSION = SystemUtils.IS_OS_WINDOWS ? ".exe" : "";

    public File workflow;
    @Option(name = "-workflow", aliases = {"-w"}, usage = "To set the worflow.xml location", required = true)
    public void setWorkflow(File f) throws FileNotFoundException {
        File workflow = new File(f, WORKFLOW_XML);
        if (!workflow.exists()) {
            workflow = f;
        }
        if (!workflow.exists()) {
            throw new FileNotFoundException(String.format("workflow.xnl not found at %s", workflow.getAbsolutePath()));
        }
        this.workflow = workflow;
    }

    @Option(name = "-recursive", aliases = {"-r"}, usage = "To process workflow and sub-workflows")
    public boolean recursive = true;

    public File dot;
    @Option(name = "-dot", aliases = {"-d"}, usage = "To set the location of the dot executable", required = true)
    public void setDot(File f) throws FileNotFoundException {
        File dot = new File(f, DOT + DOT_EXTENSION);
        if (!dot.exists()) {
            dot = new File(f.getAbsolutePath() + DOT_EXTENSION);
            if (!dot.exists()) {
                dot = f;
            }
        }
        if (!dot.exists()) {
            throw new FileNotFoundException(String.format("dot executable not found at %s", dot.getAbsolutePath()));
        }
        this.dot = dot;
    }

    @Option(name = "-output-format", aliases = "-f", usage = "To set the format of the output produced by the dot executable")
    public String format = "svg";

}
