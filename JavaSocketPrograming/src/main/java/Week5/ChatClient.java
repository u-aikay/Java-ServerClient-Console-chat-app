package Week5;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try{
            client = new Socket("localhost", 7117);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            InputHandler inputHandler = new InputHandler();
            Thread t1 = new Thread(inputHandler);
            t1.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null){
                System.out.println(inMessage);
            }
        }catch(IOException e){
            //handle
            shutdown();
        }
    }

    public void shutdown(){
        done = true;
        try{
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        }catch(IOException e){
            //ignore
        }
    }

    class InputHandler implements Runnable{

        @Override
        public void run() {
            try{
                BufferedReader inreader = new BufferedReader(new InputStreamReader(System.in));
                while(!done){
                    String message = inreader.readLine();
                    if(message.equals("/quit")){
                        out.println(message );
                        inreader.close();
                        shutdown();
                    }else{
                        out.println(message);
                    }
                }
            }catch (IOException e){
                //handle
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.run();
    }
}