import com.example.task.ThreadPool;
import task.ConnectionProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static util.Constants.SERVER_PORT;

public class Server {
    private static boolean active = true;
    public static void main(){

        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            ThreadPool threadPool = new ThreadPool(8, 20)){

            threadPool.start();
            while(active){
                Socket socket = serverSocket.accept();
                try{
                    threadPool.submit(new ConnectionProcessor(socket));
                    //executor.execute(new ConnectionProcessor(socket));
                } catch (Exception e) {
                    try{
                        System.err.println("Task rejected "+socket);
                        socket.close();
                    } catch (IOException ex) {
                        System.err.println("Fail to close socket "+socket);
                    }
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void stop(){
        active = false;
    }
}
