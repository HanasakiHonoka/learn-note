package lab.redpacket;
public final class Rules {
    private Rules() {
    }
    public static long share(long remaining,int pieces) {
        if(pieces<=0||remaining<pieces)throw new IllegalArgumentException("insufficient cents");
        return pieces==1?remaining:remaining/pieces;
    }
}
