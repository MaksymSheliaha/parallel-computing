import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientV2 {
    private static final byte AUTH_REQ = 0x1;
    private static final byte AUTH_RES = 0x2;
    private static final byte POST_MSG = 0x3;
    private static final byte GET_MSGS = 0x4;
    private static final byte GET_MSGS_RES = 0x5;
    private static final byte ERROR_RES = 0x6;
    private static final byte SUCCESS_RES = 0x7;

    public static void main(String... args){
//        switch (args.length){
//            case 0 ->{
//                System.err.println("Host IP-address not provided");
//                return;
//            }
//            case 1 ->{
//                System.err.println("Host port not provided");
//                return;
//            }
//            default -> {
//                String host = args[0];
//                String port = args[1];
//                String name = "[MY_REALLY_COOL_NAME_WOW ;)]";
//                if(args.length >= 3){
//                    name = args[2];
//                }
//                new ClientV2().run(host, port, name);
//            }
//        }
        String host = "18.153.198.123";
        String port = "10313";
        String name = "Maks";
        if(args.length >= 3){
            name = args[2];
        }
        new ClientV2().run(host, port, name);
    }
    private void run(String host, String port, String name){
        try(Socket socket = new Socket(host, Integer.parseInt(port))){
            communicateWithServer(socket.getInputStream(), socket.getOutputStream(), name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void communicateWithServer(InputStream in, OutputStream out, String name) throws IOException {
        DataInputStream inputStream = new DataInputStream(in);
        DataOutputStream outputStream = new DataOutputStream(out);
        ByteArrayOutputStream payloadBytes = new ByteArrayOutputStream();
        DataOutputStream payloadStream = new DataOutputStream(payloadBytes);
        //sendRequest(outputStream, AUTH_REQ, (short) name.length(), name.getBytes(StandardCharsets.US_ASCII));
        String token = "TWFrczpjMDIzMzBiZA==";// (String)readResponse(inputStream);
        byte tokenLength = (byte) token.length();
        String message = "Message to server from unknown users";
//
//        String payload = token + message;
//
//
//        outputStream.writeByte(POST_MSG);
//        outputStream.flush();
//        System.out.println("flag sent");
//
//        outputStream.writeShort(payload.length());
//        outputStream.flush();
//        System.out.println("len sent");
//        outputStream.writeByte(tokenLength);
//        outputStream.flush();
//        System.out.println("token len sent");
//
//        outputStream.write(payload.getBytes(StandardCharsets.US_ASCII));
//        System.out.println("payload sent");
//
        int revision = 0;//(int)readResponse(inputStream);

        payloadBytes.reset();
        payloadBytes.write(token.length());
        payloadBytes.write(token.getBytes(StandardCharsets.US_ASCII));
        payloadStream.writeInt(revision);
        payloadStream.flush();

        sendRequest(outputStream, GET_MSGS , (short) (token.length()+5),  payloadBytes.toByteArray());
        readResponse(inputStream);
    }

    private void sendRequest(DataOutputStream outputStream, byte tag, short length, byte[] payload) throws IOException {
        outputStream.writeByte(tag);
        outputStream.writeShort(length);
        outputStream.write(payload);
    }

    private Object readResponse(DataInputStream inputStream) throws IOException {
        byte respTag = inputStream.readByte();
        var resp = switch (respTag) {
            case AUTH_RES -> readAuthResponse(inputStream);
            case GET_MSGS_RES -> readGetResponse(inputStream);
            case ERROR_RES -> readErrorResponse(inputStream);
            case SUCCESS_RES -> readSuccessResponse(inputStream);
            default -> throw new IllegalStateException("Unexpected value: " + respTag);
        };

        return resp;
    }

    private Object readSuccessResponse(DataInputStream inputStream) throws IOException {
        int respLen = inputStream.readShort();
        int revision = inputStream.readInt();
        System.out.println("Received revision: " + revision);
        return revision;
    }

    private Object readErrorResponse(DataInputStream inputStream) {
        System.err.println("Received error from server");
        return null;
    }

    private Object readGetResponse(DataInputStream inputStream) throws IOException {
        int respLen = inputStream.readShort();
        int revision = inputStream.readInt();
        System.out.println("Received revision: " + revision);
        String message = new String(inputStream.readNBytes(respLen - 4), StandardCharsets.US_ASCII);
        System.out.println("Received message: " + message);
        return message;
    }

    private Object readAuthResponse(DataInputStream inputStream) throws IOException {
        int respLen = inputStream.readShort();
        String token = new String(inputStream.readNBytes(respLen), StandardCharsets.US_ASCII);
        System.out.println("Received token: " + token);
        return token;
    }


}
