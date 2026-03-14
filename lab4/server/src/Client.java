import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import static util.Constants.*;
import static util.Constants.CONNECT;
import static util.Util.readMatrix;
import static util.Util.sendMatrix;


public class Client {

    static int rows = 5000;
    static int cols = 5000;
    static int k = 1;
    static int[][] matrixA = generateMatrix(rows, cols);
    static int[][] matrixB = generateMatrix(rows, cols);
    static int threadNum = 8;
    static DataInputStream inputStream;
    static DataOutputStream outputStream;


    public static void main(){

        try(Socket socket = new Socket(SERVER_HOST, SERVER_PORT)){
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            makeRequest();
        } catch (Exception e){
            throw new RuntimeException();
        }
    }


    public static int[][] generateMatrix(int rows, int cols){
        Random random = new Random();
        var matrix = new int[rows][cols];

        for(int[] row: matrix){
            for(int i = 0; i<row.length;i++){
                row[i] = random.nextInt(100);
            }
        }
        return matrix;
    }

    private static void makeRequest() throws IOException, InterruptedException {
        outputStream.writeByte(CONNECT);
        byte connection = inputStream.readByte();
        if(connection == DISCONNECT){
            return;
        }
        if(connection != CONNECT) {
            throw new IllegalArgumentException();
        }
        System.out.println("Connection established");

        do{
            outputStream.writeInt(rows);
            System.out.println("Rows sent: "+rows);
        } while (inputStream.readByte() != RECEIVED);

        do{
            outputStream.writeInt(cols);
            System.out.println("Cols sent: "+cols);
        } while (inputStream.readByte() != RECEIVED);

        do{
            sendMatrix(matrixA, outputStream);
            System.out.println("Matrix A sent: ");
        } while (inputStream.readByte() != RECEIVED);
        do{
            sendMatrix(matrixB, outputStream);
            System.out.println("Matrix B sent: ");
        } while (inputStream.readByte() != RECEIVED);

        System.out.println("Both matrix sent: ");

        do{
            outputStream.writeInt(k);
        } while (inputStream.readByte() != RECEIVED);
        System.out.println("K sent: "+k);

        do{
            outputStream.writeInt(threadNum);
        } while (inputStream.readByte() != RECEIVED);
        System.out.println("Thread num sent: "+threadNum);

        byte status = 0;

        try{
            do{
                Thread.sleep(500);
                outputStream.writeByte(STATUS);
                status = inputStream.readByte();
            } while(!isReady(status));
        } catch (Exception e){
            System.out.println("Error caught while processing matrix");
            return;
        }

        int[][] result = readMatrix(rows, cols, inputStream, outputStream);
        printResults(matrixA, matrixB, result);
    }

    private static boolean isReady(byte status){

        if(status == NOT_READY) return false;
        if(status == READY) return true;

        throw new IllegalStateException();
    }

    private static void printResults(int[][] a, int[][] b, int[][] result){
        printMatrix(a);
        System.out.println("+");
        System.out.println(k+" * ");
        printMatrix(b);
        System.out.println("=");
        printMatrix(result);

    }

    private static void printMatrix(int[][] matrix){
        for(var row: matrix){
            System.out.println(Arrays.toString(row));
        }
    }
}
