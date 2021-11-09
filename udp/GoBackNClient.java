package udp;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GoBackNClient {

	private static final int udpPort = 5555;
    private static String serverAddress = "127.0.0.1";
    public static int totalSent = 0;
    public static int totalDrops = 0;
    private static long startTime;
    private static long endTime;
    public static long totalTime;
    private static int windowSize = 4;

    private static List<String> getFile(String path) throws IOException{
        int i = 0;
    	FileInputStream file = new FileInputStream(path);
        Scanner sc = new Scanner(file);
        List<String> lines = new ArrayList<String>();
        while (sc.hasNextLine()) {
            String line = i + "." + sc.nextLine();
            lines.add(line);
            i++;
        }
        sc.close();
        lines.add(i + ".end");
        return lines;
    }
    
    private static void send(InetAddress IP, NoisyDatagramSocket senderSocket, List<String> fileData) {
        
    	int base = 0;
    	int nextSeq = 0;
    	
    	while(true) {
    		if(fileData.size() == base) {
    			break;
    		}
    		while(nextSeq - base < windowSize && fileData.size() > nextSeq) {
    			sendNext(IP, senderSocket, fileData.get(nextSeq));
    			nextSeq++;
    		}
    		base = recieveAck(IP, senderSocket, fileData, base, nextSeq);
    	}
    }
    
    private static int recieveAck(InetAddress IP, NoisyDatagramSocket senderSocket, List<String> fileData, int base, int nextSeq) {
    	try {
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            senderSocket.receive(receivePacket);
            String serverData = new String(receivePacket.getData());
            serverData = serverData.trim();
            //System.out.println("\nServer: " + serverData);
            if (Integer.parseInt(serverData) == base) {
                base++;
            }
        } catch (SocketTimeoutException ste) {
        	System.out.println(ste.toString());
        	for(int i = base; i < nextSeq; i++) {
        		sendNext(IP, senderSocket, fileData.get(i));
        	}
        }catch (Exception e) {
            System.out.println(e.toString());
        }
    	return base;
    }
    
    private static void sendNext(InetAddress IP, NoisyDatagramSocket senderSocket, String fileData) {
    	try {
            byte[] sendBuffer = new byte[1024];
            sendBuffer = fileData.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, IP, udpPort);
            senderSocket.send(sendPacket);
            totalSent++;
            String sentData = new String(sendPacket.getData());
            sentData = sentData.trim();
            //System.out.print("\nClient: " + sentData);
        }catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void init(String filePath, int dropRate) throws SocketException, IOException {
        InetAddress IP = InetAddress.getByName(serverAddress);
        NoisyDatagramSocket senderSocket = new NoisyDatagramSocket();
        senderSocket.setDropRate(dropRate);
        senderSocket.setSoTimeout(500);
        List<String> fileData = getFile(filePath);
        System.out.println(fileData.size());
        startTime = System.nanoTime();

        send(IP,senderSocket,fileData);

        senderSocket.close();
        endTime = System.nanoTime();
        totalTime = TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
        totalDrops = senderSocket.getDrops();
        //System.out.println("\nSent packets: " + totalSent + "\nDropped packets: " + totalDrops + "\nTime: " + TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS) + " seconds");
    }

}
