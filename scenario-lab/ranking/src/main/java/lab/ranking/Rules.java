package lab.ranking;
public final class Rules {
    private Rules() {
    }
    public static boolean newer(long incoming,long stored) {
        return incoming>stored;
    }
}
