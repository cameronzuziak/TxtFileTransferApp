/**
 * UDP Server/Receiver
 */

package udp;
//import frontEnd.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Receiver {
    private static final int udpPort = 5555;
    private static final int bufferSize = 1024;
    //
    public static void main(String args[] ) throws  IOException{
        DatagramSocket receiverSocket = new DatagramSocket(udpPort);
        Scanner scan = new Scanner(System.in);

        while(true){
            byte[] receiveBuffer = new byte[bufferSize];
            System.out.println("Host Address: " + InetAddress.getLocalHost() + "\n*****Waiting for Client******");
            DatagramPacket inPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            receiverSocket.receive(inPacket);

            // create a packet to receive data
            InetAddress IP = inPacket.getAddress();
            int clientPort = inPacket.getPort();
            String inData = new String(inPacket.getData());
            inData = inData.trim();
            //int dataLength = inData.getBytes().length;
            System.out.println("\nClient: " + inData);
            System.out.print("\nServer: ");
            String outData = scan.nextLine();
            byte[] sendBuffer = new byte[bufferSize];
            sendBuffer = outData.getBytes();
            DatagramPacket outPacket = new DatagramPacket(sendBuffer, sendBuffer.length, IP, clientPort);
            receiverSocket.send(outPacket);

        }
    }

    private void send(){
        //
    }

}
