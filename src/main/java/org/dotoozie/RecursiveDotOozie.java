package org.dotoozie;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Collection;

import static org.dotoozie.Constants.WORKFLOW_XML;

final class RecursiveDotOozie implements DotOozie {

    private File dot;
    private File workflow;

    @Override
    public RecursiveDotOozie givenDot(File dot) {
        this.dot = dot;
        return this;
    }

    @Override
    public RecursiveDotOozie fromWorkflow(File workflow) throws Exception {
        this.workflow = workflow;
        return this;
    }

    @Override
    public RecursiveDotOozie exportTo(String format) throws Exception {
        Collection<File> workflows = FileUtils.listFiles(workflow.getParentFile(),
                                                         new NameFileFilter(WORKFLOW_XML),
                                                         TrueFileFilter.INSTANCE);
        for (File workflow : workflows) {
            DotOozie dotOozie = new DefaultDotOozie();
            dotOozie.givenDot(dot)
                    .fromWorkflow(workflow)
                    .exportTo(format);
        }
        return this;
    }

}
