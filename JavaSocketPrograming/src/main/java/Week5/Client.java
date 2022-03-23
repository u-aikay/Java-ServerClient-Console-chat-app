package Week5;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try(Socket socket = new Socket("localHost", 8110)) {

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            String  echo;
            String response;

            do{
                System.out.println("User: ");
                echo = scanner.nextLine();
                output.println(echo);
                if(!echo.equals("/quit")){
                    response = input.readLine();
                    System.out.println(response);
                }

            }while (!echo.equals("/quit"));
        } catch (IOException e) {
            System.out.println("Client error" + e.getMessage());
            e.printStackTrace();
        }
    }
}
