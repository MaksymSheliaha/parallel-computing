import task.Solution;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import static util.Constants.*;
import static util.Constants.CONNECT;
import static util.Util.*;


public class Client {

    static int rows = ROWS;
    static int cols = COLS;
    static int k = K;
    static int[][] matrixA = generateMatrix(rows, cols);
    static int[][] matrixB = generateMatrix(rows, cols);
    static int threadNum = THREAD_NUM;
    static DataInputStream inputStream;
    static DataOutputStream outputStream;
    static Solution solution = new Solution();


    public static void main(){

        try(Socket socket = new Socket(SERVER_HOST, SERVER_PORT)){
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            makeRequest();
        } catch (Exception e){
            e.printStackTrace();
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

        byte response = 0;

        do{
            outputStream.writeByte(ROW_FLAG);
            outputStream.writeInt(rows);
            System.out.println("Rows sent: "+rows);
            response = inputStream.readByte();
            if(response==DISCONNECT) return;
        } while (response != RECEIVED);

        do{
            outputStream.writeByte(COL_FLAG);
            outputStream.writeInt(cols);
            System.out.println("Cols sent: "+cols);
            response = inputStream.readByte();
            if(response==DISCONNECT) return;
        } while (response != RECEIVED);

        do{
            sendMatrix(matrixA, outputStream);
            System.out.println("Matrix A sent: ");
            response = inputStream.readByte();
            if(response==DISCONNECT) return;
        } while (response != RECEIVED);
        do{
            sendMatrix(matrixB, outputStream);
            System.out.println("Matrix B sent: ");
            response = inputStream.readByte();
            if(response==DISCONNECT) return;
        } while (response != RECEIVED);

        System.out.println("Both matrix sent: ");

        do{
            outputStream.writeByte(K_FLAG);
            outputStream.writeInt(k);
            response = inputStream.readByte();
            if(response==DISCONNECT) return;
        } while (response != RECEIVED);
        System.out.println("K sent: "+k);

        do{
            outputStream.writeByte(THREADS_FLAG);
            outputStream.writeInt(threadNum);
            response = inputStream.readByte();
            if(response==DISCONNECT) return;
        } while (response != RECEIVED);
        System.out.println("Thread num sent: "+threadNum);

        do{
            outputStream.writeByte(SUBMIT_TASK);
            response = inputStream.readByte();
            if(response==DISCONNECT) return;
            System.out.println("task submitted");
        } while (response != RECEIVED);

        int[][] selfResult = solution.executeParallel(matrixA, matrixB, k, threadNum);

        try{
            do{
                Thread.sleep(100);
                outputStream.writeByte(STATUS);
                response = inputStream.readByte();
                if(response==DISCONNECT) return;
            } while(!isReady(response));
        } catch (Exception e){
            System.out.println("Error caught while processing matrix");
            return;
        }
        do{
            outputStream.writeByte(GET_RESULT);
            response = inputStream.readByte();
            if(response==DISCONNECT) return;
        } while (response!=MATRIX_FLAG);
        int[][] result = readMatrix(rows, cols, inputStream, outputStream);
        System.out.println("Result received");
        System.out.println(compareResults(selfResult, result));
//        printResults(matrixA, matrixB, result);
    }

    private static boolean isReady(byte status){

        if(status == NOT_READY) {
            System.out.println("Result is not ready yet");
            return false;
        }
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
