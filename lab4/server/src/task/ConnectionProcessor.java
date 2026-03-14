package task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static util.Constants.*;
import static util.Util.*;

public class ConnectionProcessor implements Runnable{

    private final Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ConnectionProcessor(Socket socket){
        this.socket=socket;
    }

    @Override
    public void run() {
        System.out.printf("Server accept connection from %s with port %d\n", socket.getInetAddress(), socket.getPort());

        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while processing request from " + socket);
        } finally {
            try {
                System.out.println("Closing connection with "+socket);
                socket.close();
            } catch (IOException e) {
                System.err.println("Error with closing socket " + socket);
            }
        }
    }

    private void processRequest() throws IOException {

        byte connection = inputStream.readByte();
        if(connection == DISCONNECT){
            return;
        }
        if(connection != CONNECT) {
            throw new IllegalArgumentException();
        }
        outputStream.writeByte(CONNECT);
        System.out.println("Connection established");

        int rows = readInt(inputStream, outputStream);
        System.out.println("Num of rows in matrix: " +rows);
        int cols = readInt(inputStream, outputStream);
        System.out.println("Num of cols in matrix: " +cols);

        int[][] matrixA = readMatrix(rows, cols, inputStream, outputStream);
        System.out.println("Matrix A received");

        int[][] matrixB = readMatrix(rows, cols, inputStream, outputStream);
        System.out.println("Matrix B received");

        int k = readInt(inputStream, outputStream);
        System.out.println("Coefficient k received: " +k);

        int threadNum = readInt(inputStream, outputStream);
        System.out.println("Num of threads received: " +threadNum);

        Future<int[][]> taskFuture = submitTask(matrixA, matrixB, k, threadNum);
        System.out.printf("Task from socket %s submitted\n", socket);

        waitResult(taskFuture);
    }

    private Future<int[][]> submitTask(int[][] a, int[][] b, int k, int threadNum){
        Solution solution = new Solution();
        return CompletableFuture.supplyAsync(()->solution.executeParallel(a, b, k, threadNum));
    }

    private void waitResult(Future<int[][]> taskFuture) throws IOException {
        while(true) {
            byte readByte = inputStream.readByte();
            if (readByte == ERROR || readByte == DISCONNECT) {
                taskFuture.cancel(true);
                return;
            }
            if (readByte != STATUS) {
                outputStream.writeByte(ERROR);
            }

            if(!taskFuture.isDone()){
                outputStream.writeByte(NOT_READY);
            } else{
                outputStream.writeByte(READY);
                break;
            }
        }

        do{
            sendMatrix(taskFuture.resultNow(), outputStream);
            System.out.println("Result matrix sent");
        } while (inputStream.readByte() != RECEIVED);

    }
}
