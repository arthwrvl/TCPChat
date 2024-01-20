// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.io.*;
import java.net.ServerSocket;
import  java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{

    private ArrayList<ClientConnection> clientsConnected;
    private ServerSocket server;
    private boolean done;
    private ExecutorService threadPool;

    public Server() {
        clientsConnected = new ArrayList<>();
        done = false;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
    @Override
    public void run() {
        try{
            server = new ServerSocket(8000);
            threadPool = Executors.newCachedThreadPool();
            System.out.println("Aguardando conexões");
            while(!done){
                Socket client = server.accept();
                ClientConnection connection = new ClientConnection(client);
                clientsConnected.add(connection);
                threadPool.execute(connection);
            }
        } catch (IOException e) {
            turnOff();
        }

    }
    public void broadcast(String message){
        for(ClientConnection cn: clientsConnected){
            if(cn != null){
                cn.SendMessage(message);
            }
        }
    }

    public void turnOff(){
        try{
            done = true;
            threadPool.shutdown();
            if(!server.isClosed()){
                server.close();
            }
            for(ClientConnection cn : clientsConnected){
                cn.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    class ClientConnection implements Runnable {

        private Socket client;
        private BufferedReader input;
        private PrintWriter output;

        private String username;
        public ClientConnection(Socket client){
            this.client = client;
        }
        @Override
        public void run() {
            try{
                output = new PrintWriter(client.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                output.println("digite seu nome: ");
                username = input.readLine();
                System.out.println("Bem vindo: " + username);
                broadcast(username + " está entre nós!");
                String message = "";
                while(message != null){
                    message = input.readLine();
                    if(message.startsWith("/quit")){
                        broadcast(username + " saiu");
                    System.out.println("Até logo: " + username);
                        close();
                    }else{
                        broadcast(username + ": " + message);
                    }

                }

            } catch (IOException e) {
                close();
            }
        }
        public void close(){
            try{
                input.close();
                output.close();
                if(!client.isClosed()){
                    client.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        public void SendMessage(String message){
            output.println(message);
        }
    }
}