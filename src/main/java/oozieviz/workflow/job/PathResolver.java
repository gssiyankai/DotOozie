package oozieviz.workflow.job;

import java.nio.file.Paths;

public final class PathResolver {

    private final JobProperties properties;

    public PathResolver(JobProperties properties) {
        this.properties = properties;
    }

    public String resolvePath(String path) {
        return properties.replaceTokens(path);
    }

    public String relativize(String src, String dst) {
        return Paths.get(src)
                .relativize(Paths.get(dst))
                .toFile()
                .getPath();
    }

}
