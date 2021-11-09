package udp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoBackNServer {

	private static final int udpPort = 5555;
    private static final int bufferSize = 1024;
    private static int clientPort;
    private static InetAddress IP;
    
    public static void main(String args[] ) throws IOException {
        DatagramSocket receiverSocket = new DatagramSocket(udpPort);
        PrintWriter outPut = new PrintWriter(new FileWriter("OutPut.txt"));
        String next;
        int lastIndex = 0;
        
        System.out.println("Host Address: " + InetAddress.getLocalHost() + "\n*****Waiting for Client******");
        
        while(true) {
        	byte[] receiveBuffer = new byte[bufferSize];
            DatagramPacket inPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        	try {
        		receiverSocket.receive(inPacket);
                IP = inPacket.getAddress();
                clientPort = inPacket.getPort();
                next = new String(inPacket.getData(), inPacket.getOffset(), inPacket.getLength()).trim();
                System.out.println("Next: " + next);
                if(retrieveIndex(next) == lastIndex) {
                	sendAck(receiverSocket, next);
                	System.out.println(retrieveString(next));
                	if((retrieveString(next)).equals("end")) {
                		outPut.close();
                	}else {
                		outPut.println(retrieveString(next));
                	}
                	lastIndex++;
                }else {
                	sendAck(receiverSocket, ((lastIndex - 1) + ".temp"));
                }
        	}catch(Exception e) {
        		System.out.println(e.toString());
        	}
        }
    }
    
    public static void sendAck(DatagramSocket receiverSocket, String inData) throws IOException {
    	String outData = retrieveIndex(inData) + "";
    	System.out.println("\nServer: " + outData);
        byte[] sendBuffer = new byte[bufferSize];
        sendBuffer = outData.getBytes();
        DatagramPacket outPacket = new DatagramPacket(sendBuffer, sendBuffer.length, IP, clientPort);
        receiverSocket.send(outPacket);
    }
    
    public static String receiveNext(DatagramSocket receiverSocket) {
    	byte[] receiveBuffer = new byte[bufferSize];
        DatagramPacket inPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        String next = "";
    	try {
    		receiverSocket.receive(inPacket);
            IP = inPacket.getAddress();
            clientPort = inPacket.getPort();
            next = new String(inPacket.getData(), inPacket.getOffset(), inPacket.getLength()).trim();
            System.out.println("Next: " + next);
    	}catch(Exception e) {
    		System.out.println(e.toString());
    	}
        return next;
    }
    
    private static int retrieveIndex(String str) {
    	Matcher matcher = Pattern.compile("\\d+").matcher(str);
    	matcher.find();
    	return Integer.valueOf(matcher.group());
    }
    
    private static String retrieveString(String str) {
    	return str.split("\\.", 2)[1];
    }
}
