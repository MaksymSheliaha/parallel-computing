package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static util.Constants.*;

public class Util {
    public static Integer readInt(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        try{
            int result = inputStream.readInt();
            outputStream.writeByte(RECEIVED);
            return result;
        } catch (IOException e){
            System.err.println("Error reading int. Try again");
            outputStream.writeByte(ERROR);
            return null;
        }
    }

    public static int[][] readMatrix(int rows, int cols, DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        try{
            byte[] bytes = inputStream.readNBytes(rows * cols * Integer.BYTES);

            ByteBuffer bb = ByteBuffer.wrap(bytes);

            int[][] matrix = new int[rows][cols];

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    matrix[r][c] = bb.getInt();
                }
            }

            outputStream.writeByte(RECEIVED);
            return matrix;
        } catch (IOException e){
            System.err.println("Error reading matrix. Try again");
            inputStream.readAllBytes();
            outputStream.writeByte(ERROR);
            return null;
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

    public static void sendMatrixOld(int[][] matrix, DataOutputStream outputStream) throws IOException {
        for(int[] row: matrix){
            for(int el: row){
                outputStream.writeInt(el);
            }
        }
    }

    public static void sendMatrix(int[][] matrix, DataOutputStream out) throws IOException {
        out.writeByte(MATRIX_FLAG);
        int rows = matrix.length;
        int cols = matrix[0].length;

        ByteBuffer buffer = ByteBuffer.allocate(rows * cols * Integer.BYTES);

        for (int[] row : matrix) {
            for (int value : row) {
                buffer.putInt(value);
            }
        }

        out.write(buffer.array());
    }

    public static boolean compareResults(int[][] result1, int[][] result2){
        for (int i = 0; i < result1.length; i++) {
            for (int j = 0; j < result1[i].length; j++) {
                if(result1[i][j]!=result2[i][j]){
                    return false;
                }
            }
        }

        System.out.println("Results are equal");
        return true;
    }
}
