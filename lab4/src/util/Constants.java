package util;

public class Constants {
    public static final int SERVER_PORT = 8841;
    public static final String SERVER_HOST = "fe80::5fdc:12f6:3534:6b02%17";

    public static int ROWS = 5;
    public static int COLS = 5;
    public static int K = 1;
    public static int THREAD_NUM = 8;
    public static int MAX_ERRORS = 5;

    public static final byte DISCONNECT = 0x0;
    public static final byte CONNECT = 0x1;
    public static final byte RECEIVED = 0x2;
    public static final byte STATUS = 0x3;
    public static final byte READY = 0x4;
    public static final byte NOT_READY = 0x5;
    public static final byte ROW_FLAG = 0x6;
    public static final byte COL_FLAG = 0x7;
    public static final byte K_FLAG = 0xc;
    public static final byte MATRIX_FLAG = 0x8;
    public static final byte THREADS_FLAG = 0x9;
    public static final byte SUBMIT_TASK = 0xa;
    public static final byte GET_RESULT = 0xb;
    public static final byte ERROR = 0xf;
}
