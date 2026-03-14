package util;

public class Constants {
    public static final int SERVER_PORT = 1488;
    public static final String SERVER_HOST = "localhost";

    public static final byte DISCONNECT = 0x0;
    public static final byte CONNECT = 0x1;
    public static final byte RECEIVED = 0x2;
    public static final byte STATUS = 0x3;
    public static final byte READY = 0x4;
    public static final byte NOT_READY = 0x5;
    public static final byte ERROR = 0xf;
}
