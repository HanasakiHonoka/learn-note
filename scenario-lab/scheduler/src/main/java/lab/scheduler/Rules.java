package lab.scheduler;
public final class Rules {
    private Rules() {
    }
    public static boolean leaseValid(long now,long until) {
        return until>now;
    }
}
