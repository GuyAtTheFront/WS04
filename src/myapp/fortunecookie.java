package myapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class fortunecookie {

    static String mode;
    static int port;
    static String host;
    static String fname = "cookie_file.txt";

    public static void main(String[] args) {

        // For debugging
        //   - Comment out one of lines to choose mode to run
        //   - so you dont have to compile + run form console to pass in args
        // String[] myArgs = {"fc.Server", "12345", "cookie_file.txt"};
        // String[] myArgs = {"fc.Client", "localhost:12345"};
        // args = myArgs;
        
        //------------------------
        //  Args Validation Start
        //------------------------
        
        // If arg 1 not "fc.server" or "fc.client" --> Invalid Argument
        if (!(args[0].equals("fc.Server") || args[0].equals("fc.Client"))) {
            System.out.println("Invalid Argument");
            return;
        }

        mode = args[0];

        // If arg1 is "fc.Server"
        if (mode.equals("fc.Server")) {
            // If not exactly 3 args --> Invalid Argument
            if (args.length != 3) {
                System.out.println("Invalid Argument");
                return;
            }

            // if arg1 is not int --> Invalid Argument
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid argument");
                return;
            }

            // if arg3 does not contain .txt --> Invalid Argument
            if(!args[2].contains(".txt")) {
                System.out.println("Invalid argument");
                return;
            }

            fname = args[2];

        }

        // If arg1 is "fc.Client"
        if (mode.equals("fc.Client")) {
            
            // if not exactly 2 args --> Invalid Argument
            if (args.length != 2) {
                System.out.println("Invalid Argument");
                return;
            }

            // If arg2 does not contain ':' --> Invalid Argument
            if (!args[1].contains(":")) {
                System.out.println("Invalid Argument");
                return;
            }

            String[] arg2 = args[1].split(":");

            // No additional validation required
            // Invalid host will throw exception when socket is instantiated
            host = arg2[0];

            // If arg2[1] is not int --> invalid argument
            try {
                port = Integer.parseInt(arg2[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid argument");
                return;
            }
        }
        //------------------------
        //  Args Validation End
        //------------------------

        //----------------
        //  Script Start
        //----------------

        if (mode.equals("fc.Server")) {
            makeServer(port);
        } else {
            makeClient(host, port);
        }
        
    }

    public static void makeServer(int port) {
        System.out.println("Server Started");
        Cookie cookie = new Cookie(fname);
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket = serverSocket.accept();

            // output stream
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            // input stream
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            
            while(true) {
                String fromClient = dis.readUTF();

                if(fromClient.equalsIgnoreCase("exit")) {
                    break;
                }

                if(fromClient.equalsIgnoreCase("get-cookie")) {
                    System.out.println("Server received command :" + fromClient);
                    dos.writeUTF("cookie-text " + cookie.getRandomCookie());
                    dos.flush();
                    // TODO
                } else {
                    System.out.println("Server received command: " + fromClient);
                    dos.writeUTF("Invalid command received: " + fromClient);
                    dos.flush();
                }
            }
            socket.close();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void makeClient(String host, int port) {
        System.out.println("Client Started");
        try(Socket socket = new Socket(host, port)) {

            // output stream
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            // input stream
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            // scanner
            Scanner scanner = new Scanner(System.in);

            String fromUser;
            String fromServer;
            while(true) {

                fromUser = scanner.nextLine();
                if(fromUser.equalsIgnoreCase("exit")) {
                    dos.writeUTF(fromUser);
                    dos.flush();
                    socket.close();
                    scanner.close();
                    break;
                }

                dos.writeUTF(fromUser);
                dos.flush();

                fromServer = dis.readUTF();
                    if (fromServer.contains("cookie-text")){
                        System.out.println(fromServer.replace("cookie-text", "").trim());
                    } else {
                        System.out.println(fromServer);
                    };
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

