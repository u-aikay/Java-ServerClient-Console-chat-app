package Week5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ChatServer implements Runnable {
    private ArrayList<ConnectionHandler> connection;
    private ServerSocket serverSocket;
    private boolean done;
    private ExecutorService threadpool;

    public ChatServer(){
        connection = new ArrayList<>();
        done = false;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(7117);
            threadpool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = serverSocket.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connection.add(handler);
                threadpool.execute(handler);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void broadCast(String message){
        for(ConnectionHandler ch : connection){
            if(ch != null){
                ch.sendMessage(message);
            }
        }
    }

    public  void shutdown(){
        try {
            done = true;
            threadpool.shutdown();
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
            for(ConnectionHandler ch : connection){
                ch.shutdown();
            }
        }catch(IOException e){
            //ignore
        }
    }

    class ConnectionHandler implements Runnable{
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ConnectionHandler(Socket client){
            this.client = client;
        }

        @Override
        public void run() {

            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream())) ;
                out.println("Please enter a nickname: ");
                nickname = in.readLine();
                System.out.println(nickname + " connected.");
                broadCast(nickname + " joined the chat group");
                String message;
                while((message = in.readLine()) != null){
                    if(message.startsWith("/nick")){
                        String[] messageSplit = message.split("\\s", 2);
                        if(messageSplit.length == 2){
                            broadCast(nickname + " changed nickname to " + messageSplit[1]);
                            System.out.println((nickname + " changed nickname to " + messageSplit[1]));
                            nickname = messageSplit[1];
                            out.println("Successfully changed nickname to  " + nickname);
                        }else{
                            out.println(("no nicname was provided"));
                        }
                    }else if(message.startsWith("/quit")){
                        //handle quits
                        broadCast(nickname + " left the group chat");
                        shutdown();
                    }else{
                        broadCast(nickname + ": " + message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }

        }

        public void sendMessage(String message){
            out.println(message);
        }


        public void shutdown(){
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            }catch(IOException e){
                //ignore
            }
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.run();
    }
}