package lab.payment;
public final class Rules {
    private Rules() {
    }
    public static boolean matches(long expected,long received) {
        return received>0 && expected==received;
    }
}
