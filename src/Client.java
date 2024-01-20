import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class Client implements Runnable {
    private Socket client;
    private BufferedReader input;
    private PrintWriter output;
    private boolean done;

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
    @Override
    public void run() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            client = new Socket(host, 8000);
            output = new PrintWriter(client.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread thread = new Thread(inputHandler);
            thread.start();
            String inputMessage = "";
            while (inputMessage != null){
                inputMessage = input.readLine();
                System.out.println(inputMessage);
            }
        } catch (IOException e) {
            exit();
        }
    }
    public void exit(){
        done = true;

        try {
            input.close();
            output.close();
            if(!client.isClosed()){
                client.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
                while(!done){
                    String message = inputReader.readLine();
                    if(message.equals("/quit")){
                        output.println(message);
                        inputReader.close();
                        exit();
                    }else{
                        output.println(message);
                    }
                }
            }
            catch (IOException e){
                exit();
            }
        }
    }
}
