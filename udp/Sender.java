package udp;
import java.net.*;
import java.io.*;
//import javafx.*;
import java.util.*;

public class Sender {
    private static final int udpPort = 5555;

    public static void main(String args[]) throws SocketException, IOException{
        Scanner scanner = new Scanner(System.in);
        String serverAddress = "127.0.0.1";
        InetAddress IP = InetAddress.getByName(serverAddress);
        DatagramSocket senderSocket = new DatagramSocket();

        while(true) {
            byte[] sendBuffer = new byte[1024];
            byte[] receiveBuffer = new byte[1024];
            System.out.print("\nClient: ");
            String clientData = scanner.nextLine();
            sendBuffer = clientData.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, IP, udpPort);
            senderSocket.send(sendPacket);
            if(clientData.equalsIgnoreCase("quit")){
                break;
            }
            //receive data
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            senderSocket.receive(receivePacket);
            String serverData = new String(receivePacket.getData());

            serverData = serverData.trim();
            System.out.println("\nServer: " + serverData);
        }
        senderSocket.close();
    }
}
