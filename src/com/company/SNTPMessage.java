package com.company;

public class SNTPMessage {
    private byte leapIndicator = 0;
    private byte versionNumber = 4;
    private byte mode = 0;
    private short stratum = 0;
    private short pollInterval = 0;
    private byte precision = 0;
    private double rootDelay = 0;
    private double rootDispersion = 0;
    private byte[] referenceIdentifier = {0,0,0,0};
    private double referenceTimestamp = 0;
    private double originateTimestamp = 0;
    private double receiveTimestamp = 0;
    private double transmitTimestamp = 0;

    public SNTPMessage(byte[] buf){
        byte b = buf[0];
        leapIndicator = (byte) ((b >>6) & 0x3);
        versionNumber = (byte) ((b >>3) & 0x7);
        mode = (byte) (b & 0x7);
        stratum = unsignedByteToShort(buf[1]);
        pollInterval = unsignedByteToShort(buf[2]);
        precision = buf[3];
        rootDelay = (buf[4] * (0xff+1.0)) + unsignedByteToShort(buf[5]) +
                (unsignedByteToShort(buf[6])/ (0xff+1.0)) + (unsignedByteToShort(buf[7])/(0xffff+1.0));
        rootDispersion = (buf[8] * (0xff+1.0)) + unsignedByteToShort(buf[9]) +
                (unsignedByteToShort(buf[10])/ (0xff+1.0)) + (unsignedByteToShort(buf[11])/(0xffff+1.0));
        referenceIdentifier[0] = buf[12];
        referenceIdentifier[1] = buf[13];
        referenceIdentifier[2] = buf[14];
        referenceIdentifier[3] = buf[15];
        referenceTimestamp = byteArrayToDouble(buf, 16);
        originateTimestamp = byteArrayToDouble(buf, 24);
        receiveTimestamp = byteArrayToDouble(buf, 32);
        transmitTimestamp = byteArrayToDouble(buf, 40);
    }

    public SNTPMessage(){
        mode = 3;
        transmitTimestamp = (System.currentTimeMillis()/1000.0) + 2208988800.0;
    }

    private double byteArrayToDouble(byte[] buf, int index) {
        double result = 0.0;
        for (int i = 0; i< 8; i++){
            result += unsignedByteToShort(buf[index + i]) * Math.pow(2, (3-i)*8);
        }
        return result;
    }

    private short unsignedByteToShort(byte b) {
        if ((b & 0x80) == 0x80){
            return (short) (128 + (b & 0x7f));
        }
        return (short) b;
    }

    public byte[] toByteArray(){
        byte[] array = new byte[48];
        array[0] = (byte) (leapIndicator << 6 | versionNumber << 3 | mode);
        array[1] = (byte) stratum;
        array[2] = (byte) pollInterval;
        array[4] = precision;

        int data = (int)(rootDelay * (0xff+1));
        array[4] = (byte) ((data>> 24)& 0xff);
        array[5] = (byte) ((data>> 16)& 0xff);
        array[6] = (byte) ((data>> 8)& 0xff);
        array[7] = (byte) (data& 0xff);

        int rd = (int)(rootDispersion * (0xff+1));
        array[8] = (byte) ((rd>> 24)& 0xff);
        array[9] = (byte) ((rd>> 16)& 0xff);
        array[10] = (byte) ((rd>> 8)& 0xff);
        array[11] = (byte) (rd& 0xff);

        array[12] = referenceIdentifier[0];
        array[13] = referenceIdentifier[1];
        array[14] = referenceIdentifier[2];
        array[15] = referenceIdentifier[3];

        doubleToByteArray(array, 16, receiveTimestamp);
        doubleToByteArray(array, 24, originateTimestamp);
        doubleToByteArray(array, 32, receiveTimestamp);
        doubleToByteArray(array, 40, transmitTimestamp);

        return array;
    }

    private void doubleToByteArray(byte[] array, int index, double data) {
        for (int i = 0; i < 8; i++){
            array[index+i] = (byte) (data /Math.pow(2,(3-i)*8));
            data -= (double) (unsignedByteToShort(array[index +i]) *Math.pow(2,(3-i)*8));
        }
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("leapIndicator: ").append(leapIndicator).append("\n");
        msg.append("versionNumber: ").append(versionNumber).append("\n");
        msg.append("mode: ").append(mode).append("\n");
        msg.append("stratum: ").append(stratum).append("\n");
        msg.append("pollInterval: ").append(pollInterval).append("\n");
        msg.append("precision: ").append(precision).append("\n");
        msg.append("rootDelay: ").append(rootDelay).append("\n");
        msg.append("rootDispersion: ").append(rootDispersion).append("\n");
        msg.append("ReferenceIdentifier: ").append(referenceIdentifierToString()).append("\n");
        msg.append("referenceTimestamp: ").append(referenceTimestamp).append("\n");
        msg.append("originateTimestamp: ").append(originateTimestamp).append("\n");
        msg.append("receiveTimestamp: ").append(receiveTimestamp).append("\n");
        msg.insert(msg.length(), "transmitTimestamp: " + transmitTimestamp + "\n");
        return msg.toString();
    }

    public String referenceIdentifierToString()
    {
        char c = (char) referenceIdentifier[0];
        char c2 = (char) referenceIdentifier[1];
        char c3 = (char) referenceIdentifier[2];
        char c4 = (char) referenceIdentifier[3];

        System.out.println(c);
        String msg = String.valueOf(c) +
                c2 +
                c3 +
                c4;
        return msg;
    }

    public double getReceiveTimeStamp() {
        return receiveTimestamp;
    }

    public double getOriginateTimeStamp() {
        return originateTimestamp;
    }

    public double getTransmitTimeStamp() {
        return transmitTimestamp;
    }
}