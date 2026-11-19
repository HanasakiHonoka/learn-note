package lab.qrlogin;
public final class Rules {
    private Rules() {
    }
    public static String hash(String secret) {
        try {
            return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        }
        catch(java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
