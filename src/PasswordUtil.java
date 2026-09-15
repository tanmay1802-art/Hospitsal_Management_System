package hms;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA-256 password hashing so users.txt never stores a plaintext password.
 *
 * Every screen that lets someone change a password (Admin's user-management
 * form, and each dashboard's own "My Profile" tab) pre-fills the password
 * field with whatever is already on the User object so the field isn't
 * empty. Once passwords are hashed, that pre-filled value is the *hash*, not
 * the real password - so if we just re-hashed whatever's in the field on
 * every save, an unchanged password would get hashed again on every edit and
 * the account would be locked out. resolvePassword() below is the fix: it
 * only hashes the field's contents when they differ from the hash that was
 * already there, i.e. the person actually typed something new.
 */
public final class PasswordUtil {

    private static final java.util.regex.Pattern SHA256_HEX = java.util.regex.Pattern.compile("^[0-9a-f]{64}$");

    private PasswordUtil() { } // no instances, just static helpers

    /** One-way hash of a plaintext password. */
    public static String hash(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 ships with every JVM, so this is not expected to happen in practice.
            throw new DataAccessException("SHA-256 is not available on this JVM", e);
        }
    }

    /** True if the given string already looks like one of our SHA-256 hashes (64 hex chars). */
    public static boolean isHashed(String value) {
        return value != null && SHA256_HEX.matcher(value).matches();
    }

    /** Compares a plaintext password typed at login against the stored hash. */
    public static boolean matches(String plainTextAttempt, String storedHash) {
        if (plainTextAttempt == null || storedHash == null) return false;
        return hash(plainTextAttempt).equals(storedHash);
    }

    /**
     * Used whenever a form field was pre-filled with a user's current password hash
     * and might, or might not, have been changed before saving. If the field still
     * holds exactly that hash, nothing changed - keep it. Otherwise, whatever is in
     * the field is a brand-new plaintext password, so hash it before it gets saved.
     */
    public static String resolvePassword(String currentStoredHash, String fieldValue) {
        if (currentStoredHash != null && currentStoredHash.equals(fieldValue)) {
            return currentStoredHash;
        }
        return hash(fieldValue == null ? "" : fieldValue);
    }
}
