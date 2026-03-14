package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static util.Constants.ERROR;
import static util.Constants.RECEIVED;

public class Util {
    public static int readInt(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        try{
            int result = inputStream.readInt();
            outputStream.writeByte(RECEIVED);
            return result;
        } catch (IOException e){
            System.err.println("Error reading int. Try again");
            outputStream.writeByte(ERROR);
            return readInt(inputStream, outputStream);
        }
    }

    public static int[][] readMatrix(int rows, int cols, DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        try{
            int[][] buffer = new int[rows][cols];
            for(int i = 0; i<rows; i++){
                readRow(buffer, i, cols, inputStream);
            }
            outputStream.writeByte(RECEIVED);
            return buffer;
        } catch (IOException e){
            System.err.println("Error reading matrix. Try again");
            inputStream.readAllBytes();
            outputStream.writeByte(ERROR);
            return readMatrix(rows, cols, inputStream, outputStream);
        }
    }

    public static void readRow(int[][] buffer, int row, int length, DataInputStream inputStream) throws IOException {
        try{
            for(int i = 0; i<length; i++){
                buffer[row][i] = inputStream.readInt();
            }
        } catch (IOException e){
            System.err.printf("Error reading row %d. Try again\n", row);
            throw e;
        }
    }

    public static void sendMatrix(int[][] matrix, DataOutputStream outputStream) throws IOException {
        for(int[] row: matrix){
            for(int el: row){
                outputStream.writeInt(el);
            }
        }
    }
}
