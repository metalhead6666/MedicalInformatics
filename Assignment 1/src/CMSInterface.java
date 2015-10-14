import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Simple program to open communications ports and connect to Agilent Monitor
 * Agilent Communication Interface - TO BE IMPLEMENTED
 * @version 1.2 - 30 Set 2003
 * @author Francisco Cardoso (fmcc@student.dei.uc.pt)
 * @author Ricardo Sal (ricsal@student.dei.uc.pt)
 */

public class CMSInterface{
    private static byte START_MESSAGE = 0x1b;
    private final static int DEFAULT_LENGTH = 6; /* LENGTH + SRC_ID + DST_ID */
    private final static int DEFAULT_SIZE = 2;
    private static byte[] DST_ID;
    private static byte[] SRC_ID;
    private static byte[] LENGTH;

    private static byte[] readArray;
    private static Semaphore semaphore;

    public CMSInterface(){
        semaphore = new Semaphore(0);

        ReadStuff readStuff = new ReadStuff(semaphore);
        WriteStuff writeStuff = new WriteStuff(semaphore);
    }

    public static boolean connect(ComInterface comInterface, appInterface app){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] finalByteArray;
        byte[] CONNECT_REQ = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) Utils.CONNECT_REQ).array();
        byte[] TICK_PERIOD = new byte[DEFAULT_SIZE];
        //DST_ID = new byte[DEFAULT_SIZE];
        //SRC_ID = new byte[DEFAULT_SIZE];
        //LENGTH = new byte[DEFAULT_SIZE];

        CONNECT_REQ = changeBytesPosition(CONNECT_REQ);
        DST_ID = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short)Utils.DST_ID).array();
        DST_ID = changeBytesPosition(DST_ID);
        SRC_ID = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short)Utils.SRC_ID).array();
        SRC_ID = changeBytesPosition(SRC_ID);

        LENGTH = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short)(DEFAULT_LENGTH + DEFAULT_SIZE * 2)).array();
        LENGTH = changeBytesPosition(LENGTH);

        outputStream.write(START_MESSAGE);

        try{
            outputStream.write(LENGTH);
            outputStream.write(DST_ID);
            outputStream.write(SRC_ID);
            outputStream.write(CONNECT_REQ);

            outputStream.write(TICK_PERIOD);
        }catch(IOException e){
            return false;
        }

        finalByteArray = outputStream.toByteArray();
        comInterface.writeBytes(finalByteArray);

        //FIXME
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            return false;
        }

        readArray = comInterface.readBytes();
        System.out.println(readArray.length);

        ArrayList<Byte> response = processResponse(readArray);
        String string = "";
        for (int i = 0; i < response.size(); i += 2) {
            string += "<"+response.get(i)+"|"+response.get(i+1)+">";
        }
        app.appendText(string);

        return true; //CONNECTED
    }

    public static boolean disconnect(ComInterface comInterface, appInterface app){

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] finalByteArray;
        byte[] DISCONNECT_REQ = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) Utils.DISCONNECT_REQ).array();

        DISCONNECT_REQ = changeBytesPosition(DISCONNECT_REQ);
        DST_ID = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short)Utils.DST_ID).array();
        DST_ID = changeBytesPosition(DST_ID);
        SRC_ID = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short)Utils.SRC_ID).array();
        SRC_ID = changeBytesPosition(SRC_ID);
        LENGTH = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short)(DEFAULT_LENGTH + DEFAULT_SIZE * 2)).array();
        LENGTH = changeBytesPosition(LENGTH);

        outputStream.write(START_MESSAGE);

        try{
            outputStream.write(LENGTH);
            outputStream.write(DST_ID);
            outputStream.write(SRC_ID);
            outputStream.write(DISCONNECT_REQ);
        }catch(IOException e){
            return true;
        }

        finalByteArray = outputStream.toByteArray();
        comInterface.writeBytes(finalByteArray);

        readArray = comInterface.readBytes();

        ArrayList<Byte> response = processResponse(readArray);
        String string = "";
        for (int i = 0; i < response.size(); i += 2) {
            string += "<"+response.get(i)+"|"+response.get(i+1)+">";
        }
        app.appendText(string);

        return false; //DISCONNECTED
    }

    public static void getParList(ComInterface comInterface, appInterface app){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] finalByteArray;
        byte[] PAR_LIST_REQ = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) Utils.PAR_LIST_REQ).array();

        PAR_LIST_REQ = changeBytesPosition(PAR_LIST_REQ);
        DST_ID = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short)Utils.DST_ID).array();
        DST_ID = changeBytesPosition(DST_ID);
        SRC_ID = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short)Utils.SRC_ID).array();
        SRC_ID = changeBytesPosition(SRC_ID);

        LENGTH = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short)(DEFAULT_LENGTH + DEFAULT_SIZE * 2)).array();
        LENGTH = changeBytesPosition(LENGTH);

        outputStream.write(START_MESSAGE);

        try{
            outputStream.write(LENGTH);
            outputStream.write(DST_ID);
            outputStream.write(SRC_ID);
            outputStream.write(PAR_LIST_REQ);
        }catch(IOException e){
            return;
        }

        finalByteArray = outputStream.toByteArray();
        comInterface.writeBytes(finalByteArray);
    }

    public static void singleTuneRequest(int id){
    }

    private static byte[] changeBytesPosition(byte... byteArray){
        byte temp = byteArray[1];
        byteArray[1] = byteArray[0];
        byteArray[0] = temp;

        return byteArray;
    }

    private static ArrayList<Byte> processResponse(byte[] response){
        ArrayList<Byte> arrayList = new ArrayList<>();

        if(response[0] != 0x1b){
            return null;
        }
        else{
            for (int i = 1; i < response.length; i += 2) {
                if(response[i] == 0x1b) {
                    if(response[i+1] == 0xff) {
                        arrayList.add(response[i + 2]);
                        i++;
                    }
                    else{
                        return null;
                    }
                }
                else{
                    arrayList.add(response[i+1]);
                }

                arrayList.add(response[i]);
            }
        }
        return arrayList;
    }
}

class WriteStuff extends Thread {
    private Semaphore semaphore;
    private int id;

    public WriteStuff(Semaphore semaphore) {
        this.semaphore = semaphore;
        this.start();
    }

    @Override
    public void run() {
        while (true){
            switch (id){
                case 0:
                    
                    break;
                default:
                    break;
            }
            semaphore.take();
        }
    }

    public void setId(int id) {
        this.id = id;
    }
}

class ReadStuff extends Thread {
    private Semaphore semaphore;
    private int id;

    public ReadStuff(Semaphore semaphore) {
        this.semaphore = semaphore;
        this.start();
    }

    @Override
    public void run() {
        while (true){
            semaphore.release();
            switch (id){

            }
        }
    }

    public void setId(int id) {
        this.id = id;
    }
}