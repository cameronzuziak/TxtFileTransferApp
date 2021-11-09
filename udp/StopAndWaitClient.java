package udp;

import java.io.IOException;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class StopAndWaitClient {
    // Server side app will print IP Address out to server terminal. Enter that IP Address below.
    private static String serverAddress = "127.0.0.1";
    private static final int udpPort = 5555;
    private static String testFilePath = "src/testFile.txt";
    public static int totalDrops = 0;
    public static int totalSent = 0;
    private static int testDropRate = 10;
    private static long startTime;
    private static long endTime;
    public long totalTime;

    private static List<String> getFile(String path) throws IOException{
        FileInputStream file = new FileInputStream(path);
        Scanner sc = new Scanner(file);
        List<String> lines = new ArrayList<String>();
        while (sc.hasNextLine()) {
            String line = '.' + sc.nextLine();
            lines.add(line);
        }
        return lines;
    }

    private static void send(InetAddress IP, NoisyDatagramSocket senderSocket, String fileData) {
        boolean wait = true;
        while (wait) {
            try {
                // send data
                byte[] sendBuffer = new byte[1024];
                sendBuffer = fileData.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, IP, udpPort);
                senderSocket.send(sendPacket);
                totalSent++;
                String sentData = new String(sendPacket.getData());
                sentData = sentData.trim();
                //System.out.print("\nClient: " + sentData);

                //receive data
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                senderSocket.receive(receivePacket);
                String serverData = new String(receivePacket.getData());
                serverData = serverData.trim();
                //System.out.println("\nServer: " + serverData);
                if (serverData.equals(fileData.trim())) {
                    wait = false;
                }
            } catch (SocketTimeoutException ste) {
            	totalDrops++;
            	System.out.println(ste.toString());
            }catch (Exception e) {
                System.out.println(e.toString());
            }
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

        for(int i = 0; i < fileData.size(); i++) {
            send(IP,senderSocket,fileData.get(i));
        }

        String end = "end";
        byte[] endTransaction = end.getBytes();
        send(IP,senderSocket,end);
        senderSocket.close();
        endTime = System.nanoTime();
        totalTime = TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
    }
}
