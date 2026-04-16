package task;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static util.Constants.*;

public class ConnectionProcessor implements Runnable{

    private final Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    public ConnectionProcessor(Socket socket){
        this.socket=socket;
    }

    @Override
    public void run() {
        System.out.printf("Server accept connection from %s with port %d\n", socket.getInetAddress(), socket.getPort());

        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
        String requestLine = input.readLine();
        if (requestLine == null) {
            sendNotFound();
            return;
        }

        // Parse the path (e.g., "GET /home HTTP/1.1" -> "/home")
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        if(!Objects.equals(method, "GET")) {
            sendNotFound();
            return;
        }
        String path = parts[1];

        if(path.equals("/") || path.isBlank()) {
            path = "/index.html";
        }
        path = RESOURCES + (path.endsWith(".html") ? path : path + ".html");
        try{
            String responseBody = Files.readString(Paths.get(path));
            sendOk(responseBody);
        } catch (InvalidPathException | NoSuchFileException e) {
            sendNotFound();
        } catch (IOException e) {
            e.printStackTrace();
            sendError();
        }
    }

    private void sendNotFound() {
        try {
            LocalDateTime now = LocalDateTime.now();
            String body = Files.readString(Paths.get(NOT_FOUND_PATH));
            output.write(NOT_FOUND_STATUS);
            output.write("Date: " + now + "\r\n");
            output.write(CONTENT_TYPE);
            output.write("Content-Length: " + body.length() + "\r\n");
            output.write("\r\n");
            output.write(body);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendError() {
        try {
            output.write(ERROR_STATUS);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendOk(String body) {
        LocalDateTime now = LocalDateTime.now();
        try {
            output.write(OK_STATUS);
            output.write("Date: " + now + "\r\n");
            output.write(CONTENT_TYPE);
            output.write("Content-Length: " + body.length() + "\r\n");
            output.write("\r\n");
            output.write(body);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
