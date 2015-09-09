package org.dotoozie;

public final class DotOozieFactory {

    private DotOozieFactory() {
    }

    public static DotOozie newDotOozie(boolean recursive) {
        return recursive ? new RecursiveDotOozie() : new DefaultDotOozie();
    }

}
