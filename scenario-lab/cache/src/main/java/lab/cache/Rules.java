package lab.cache;
public final class Rules {
    private Rules() {
    }
    public static boolean versionValid(long version) {
        return version>0;
    }
}
