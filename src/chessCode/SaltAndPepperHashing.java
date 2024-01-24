package chessCode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SaltAndPepperHashing {

    private static final String PEPPER = "someRandomPepper"; 

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedAndPepperedPassword = salt + password + PEPPER;
            byte[] hashedPassword = md.digest(saltedAndPepperedPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    public static void main(String[] args) {
        String password = "sample";
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        System.out.println("Salt: " + salt);
        System.out.println("Hashed Password: " + hashedPassword);
    }
}