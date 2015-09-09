package org.dotoozie;

import org.apache.commons.lang.SystemUtils;

public final class Constants {

    private Constants() {
    }

    public static final String DOT = "dot";
    public static final String DOT_EXTENSION = SystemUtils.IS_OS_WINDOWS ? ".exe" : "";
    public static final String WORKFLOW = "workflow";
    public static final String XML = "xml";
    public static final String WORKFLOW_XML = WORKFLOW + "." + XML;

}
