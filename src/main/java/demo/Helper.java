package demo;


import java.security.SecureRandom;
import java.util.Base64;

public class Helper {
	
	public static String generateBse64Random() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        
        secureRandom.nextBytes(randomBytes);
        String base64Encoded = Base64.getEncoder().encodeToString(randomBytes);

        return base64Encoded;
    }


}
