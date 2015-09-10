package oozieviz;

import org.apache.commons.lang.SystemUtils;

public final class Constants {

    private Constants() {
    }

    public static final String DOT = "dot";
    public static final String DOT_EXE = DOT + (SystemUtils.IS_OS_WINDOWS ? ".exe" : "");

    public static final String SVG = "svg";

    public static final String WORKFLOW = "workflow";
    public static final String WORKFLOW_XML = WORKFLOW + ".xml";
    public static final String WORKFLOW_DOT = WORKFLOW + ".dot";

    public static final String JOB_PROPERTIES = "job.properties";

}
