package org.dotoozie;

import java.io.File;

public interface DotOozie {

    DotOozie givenDot(File dot);

    DotOozie fromWorkflow(File workflow) throws Exception;

    DotOozie exportTo(String format) throws Exception;

}
