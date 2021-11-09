package udp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class StopAndWaitServer {
    private static final int udpPort = 5555;
    private static final int bufferSize = 1024;

    public static void main(String args[] ) throws IOException {
        DatagramSocket receiverSocket = new DatagramSocket(udpPort);
        PrintWriter outPut = new PrintWriter(new FileWriter("OutPut.txt"));
        System.out.println("Host Address: " + InetAddress.getLocalHost() + "\n*****Waiting for Client******");
        while(true){
            // receive packet from client
            byte[] receiveBuffer = new byte[bufferSize];
            DatagramPacket inPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            receiverSocket.receive(inPacket);

            // get data to respond
            InetAddress IP = inPacket.getAddress();
            int clientPort = inPacket.getPort();
            String inData = new String(inPacket.getData());
            inData = inData.trim();
            String printData = inData.substring(1);
            if(inData.equals("end")){outPut.close();}
            outPut.println(printData);
            System.out.print("\nClient: " + inData);

            // respond with ACK (Ack is going to equal what the received data was).
            String outData = inData;
            System.out.println("\nServer: " + outData);
            byte[] sendBuffer = new byte[bufferSize];
            sendBuffer = outData.getBytes();
            DatagramPacket outPacket = new DatagramPacket(sendBuffer, sendBuffer.length, IP, clientPort);
            receiverSocket.send(outPacket);
        }
    }
}
