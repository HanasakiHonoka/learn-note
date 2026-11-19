package lab.im;
public final class Rules {
    private Rules() {
    }
    public static boolean validBody(String body) {
        return body!=null&&!body.isBlank()&&body.length()<=1000;
    }
}
