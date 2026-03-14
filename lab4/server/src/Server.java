import task.ConnectionProcessor;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static util.Constants.SERVER_PORT;

public class Server {
    private static boolean active = true;
    public static void main(){

        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()){

            while(active){
                Socket socket = serverSocket.accept();
               executor.execute(new ConnectionProcessor(socket));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void stop(){
        active = false;
    }
}
