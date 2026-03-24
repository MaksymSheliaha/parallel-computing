import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Client {

    private static final byte PLAIN_TEXT_REQUEST = 0x1;
    private static final byte ENCRYPTED_REQUEST = 0x2;
    private static final byte PLAIN_TEXT_RESPONSE = 0x3;

    public static void main(String... args){
        switch (args.length){
            case 0 ->{
                System.err.println("Host IP-address not provided");
                return;
            }
            case 1 ->{
                System.err.println("Host port not provided");
                return;
            }
            default -> {
                String host = args[0];
                String port = args[1];
                String name = "[MY_REALLY_COOL_NAME_WOW ;)]";
                if(args.length >= 3){
                    name = args[2];
                }
                new Client().run(host, port, name);
            }
        }
    }

    private void run(String host, String port, String name){
        try(Socket socket = new Socket(host, Integer.parseInt(port))){
            communicateWithServer(socket.getInputStream(), socket.getOutputStream(), name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void communicateWithServer(InputStream in, OutputStream out, String name) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        DataInputStream inputStream = new DataInputStream(in);
        DataOutputStream outputStream = new DataOutputStream(out);

        outputStream.writeByte(PLAIN_TEXT_REQUEST);
        byte[] nameBytes = name.getBytes(StandardCharsets.US_ASCII);
        outputStream.writeByte(nameBytes.length);
        outputStream.write(nameBytes);
        System.out.println("Not encrypted request sent: " + Arrays.toString(nameBytes));

        readResponse(inputStream);

        outputStream.writeByte(ENCRYPTED_REQUEST);
        byte[] encrypted = Encryptor.encrypt(name);
        outputStream.writeByte(encrypted.length);
        outputStream.write(encrypted);
        System.out.println("Encrypted request sent: " + Arrays.toString(encrypted));

        readResponse(inputStream);
    }

    private void readResponse(DataInputStream inputStream) throws IOException {
        byte tag = inputStream.readByte();
        byte length = 0;
        String response;
        if(tag==PLAIN_TEXT_RESPONSE){
            System.out.println("Response tag received");
        } else {
            System.err.println("Unexpected tag received: "+tag);
            response = new String(inputStream.readAllBytes(), StandardCharsets.US_ASCII);
            System.err.println("ERROR RESPONSE: " + response);
            return;
        }

        length = inputStream.readByte();
        byte[] responseBytes = inputStream.readNBytes(length);
        response = new String(responseBytes, StandardCharsets.US_ASCII);

        System.out.println("SERVER RESPONSE: " + response);
    }


    static class Encryptor{

        static String algorithm = "AES";
        static String mode = "CBC";
        static String padding = "PKCS5Padding";
        static String key = "1234567890123456";
        static byte[] iv = new byte[16];

        static byte[] encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            var key = new SecretKeySpec(Encryptor.key.getBytes(StandardCharsets.US_ASCII), "AES");
            var iv = new IvParameterSpec(Encryptor.iv);

            Cipher cipher = Cipher.getInstance(algorithm+"/"+mode+"/"+padding);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipher.doFinal(text.getBytes(StandardCharsets.US_ASCII));
        }
    }
}
