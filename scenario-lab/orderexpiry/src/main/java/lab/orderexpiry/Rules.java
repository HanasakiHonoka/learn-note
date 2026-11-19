package lab.orderexpiry;
public final class Rules {
    private Rules() {
    }
    public static boolean due(long now,long expires) {
        return now>=expires;
    }
}
