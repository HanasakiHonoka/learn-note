package lab.shortlink;
public final class Rules {
    private Rules() {
    }
    public static boolean safe(String value) {
        try {
            var u=java.net.URI.create(value);
            return ("https".equalsIgnoreCase(u.getScheme())||"http".equalsIgnoreCase(u.getScheme()))&&u.getHost()!=null;
        }
        catch(IllegalArgumentException e) {
            return false;
        }
    }
}
