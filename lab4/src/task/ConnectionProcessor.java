package task;

import util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static util.Constants.*;
import static util.Util.readInt;
import static util.Util.sendMatrix;

public class ConnectionProcessor implements Runnable{

    private final Socket socket;
    private final Data data;
    private int errorCount = 0;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ConnectionProcessor(Socket socket){
        this.socket=socket;
        this.data = new Data();
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

    private void processRequest() throws IOException, ExecutionException, InterruptedException {

        byte connection = inputStream.readByte();
        if(connection == DISCONNECT){
            return;
        }
        if(connection != CONNECT) {
            throw new IllegalArgumentException();
        }
        outputStream.writeByte(CONNECT);
        System.out.println("Connection established");

        while(processData()){}
        outputStream.writeByte(DISCONNECT);
    }

    private boolean processData() throws IOException, ExecutionException, InterruptedException {
        byte flag = inputStream.readByte();
        switch (flag){
            case DISCONNECT, RECEIVED -> {
                return false;
            }
            case ROW_FLAG -> {
                Integer rows = readInt(inputStream, outputStream);
                if(rows == null || data.matrixA!=null){
                    errorCount++;
                } else {
                    data.rows=rows;
                    System.out.println("Row number received");
                }
            }
            case COL_FLAG -> {
                Integer cols = readInt(inputStream, outputStream);
                if(cols == null || data.matrixA!=null){
                    errorCount++;
                } else {
                    data.cols=cols;
                    System.out.println("Col number received");
                }
            }
            case K_FLAG -> {
                Integer k = readInt(inputStream, outputStream);
                if(k == null){
                    errorCount++;
                } else {
                    data.k=k;
                    System.out.println("K received");
                }
            }
            case THREADS_FLAG -> {
                Integer threads = readInt(inputStream, outputStream);
                if(threads == null){
                    errorCount++;
                } else {
                    data.threads=threads;
                    System.out.println("Thread num received");
                }
            }
            case MATRIX_FLAG -> readMatrix();
            case SUBMIT_TASK -> submitTask();
            case STATUS -> statusRequest();
            case GET_RESULT -> returnResult();
            case ERROR ->{
                System.err.println("Error from client received");
                errorCount++;
            }
            default -> {
                inputStream.readAllBytes();
                errorCount++;
            }
        }

        return errorCount<MAX_ERRORS;
    }

    private void statusRequest() throws IOException {
        if(data.taskFuture == null){
            errorCount++;
            return;
        } else if(!data.taskFuture.isDone()){
            outputStream.writeByte(NOT_READY);
        } else{
            outputStream.writeByte(READY);
        }
        System.out.println("Status request processed");
    }

    private void readMatrix() throws IOException {
        if(data.rows == 0 || data.cols == 0){
            errorCount++;
            return;
        }

        var matrix = Util.readMatrix(data.rows, data.cols, inputStream, outputStream);
        if(matrix == null) {
            errorCount++;
            return;
        }
        System.out.println("Matrix read successfully");
        if(data.matrixA == null){
            data.matrixA = matrix;
        } else if (data.matrixB == null) {
            data.matrixB = matrix;
        } else{
            System.out.println("Received matrix is ignored");
        }
    }


    private void submitTask() throws IOException {
        if(data.matrixA==null || data.matrixB==null){
            errorCount++;
            return;
        }

        Future<int[][]> result;
        if(data.threads>1){
            result = CompletableFuture.supplyAsync(()->new Solution().executeParallel(data.matrixA, data.matrixB, data.k, data.threads));
        } else{
            result = CompletableFuture.supplyAsync(()->new Solution().executeSequentially(data.matrixA, data.matrixB, data.k));
        }
        data.taskFuture = result;
        System.out.println("Task submitted");
        outputStream.writeByte(RECEIVED);
    }

    private void returnResult() throws ExecutionException, InterruptedException, IOException {
        var result = data.taskFuture.get();
        sendMatrix(result, outputStream);
        System.out.println("Result matrix sent");
//        printResults(data.matrixA, data.matrixB, result);
    }

    private void printResults(int[][] a, int[][] b, int[][] result){
        printMatrix(a);
        System.out.println("+");
        System.out.println(data.k+" * ");
        printMatrix(b);
        System.out.println("=");
        printMatrix(result);

    }

    private static void printMatrix(int[][] matrix){
        for(var row: matrix){
            System.out.println(Arrays.toString(row));
        }
    }

    private static class Data{
        int rows;
        int cols;
        int k = 1;
        int threads;
        int[][] matrixA;
        int[][] matrixB;
        Future<int[][]> taskFuture;
    }
}
