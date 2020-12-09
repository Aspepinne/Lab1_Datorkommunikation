package com.company;

import java.io.IOException;
import java.net.*;

public class Main {

    static int port = 123;
    static String[] serverName = {"gbg1.ntp.se", "gbg2.ntp.se", "mmo1.ntp.se", "mmo2.ntp.se",
            "sth1.ntp.se", "sth2.ntp.se", "svl1.ntp.se", "svl2.ntp.se"};

    public static void main(String[] args){
        double timestamp;
        double clockOffset;

        try {
            int i = 0;
            DatagramSocket socket = new DatagramSocket();
            SNTPMessage msg = new SNTPMessage();
            DatagramPacket packet = datagramPacket();
            socket.send(packet);
            System.out.println("Sent request");
            socket.receive(packet);
            SNTPMessage response = new SNTPMessage(packet.getData());
            System.out.println("Got reply");
            socket.close();
            System.out.println(msg.toString());

            timestamp =
                    (System.currentTimeMillis() / 1000.0) + 2208988800.0;
            clockOffset =
                    ((response.getReceiveTimeStamp() - response.getOriginateTimeStamp()) +
                            (response.getTransmitTimeStamp() - timestamp)) * 1000 / 2;

            System.out.println("Offset for local clock: " + clockOffset + " ms");


        } catch (IOException e){
            e.printStackTrace();
        }

    }

    static DatagramPacket datagramPacket() {
        DatagramPacket packet = null;
        for(String server : serverName) {
            try {
                InetAddress adress = InetAddress.getByName(server);
                SNTPMessage msg = new SNTPMessage();
                byte[] buf = msg.toByteArray();
                packet = new DatagramPacket(buf, buf.length, adress, port);
                System.out.println("Connected to " + server);
                break;
            } catch(Exception e) {
                System.out.println("Unable to connect to " + server);
            }
        }
        return packet;
    }
}
