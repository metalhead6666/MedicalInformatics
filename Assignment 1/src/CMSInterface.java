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

    public static boolean connect(ComInterface comInterface){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] finalByteArray;
        byte[] CONNECT_REQ = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) 1).array();
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

            for (byte b : LENGTH) {
                System.out.println("fghjttywrefdg "+b);
            }

            outputStream.write(TICK_PERIOD);
        }catch(IOException e){
            return false;
        }

        finalByteArray = outputStream.toByteArray();
        comInterface.writeBytes(finalByteArray);

        for (int i = 0; i < finalByteArray.length; i++) {
            System.out.println("::: "+finalByteArray[i]);
        }

        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            System.out.println("Not good thing...");
        }

        readArray = comInterface.readBytes();
        System.out.println(readArray.length);

        return true; // CONNECTED
    }

    public static boolean disconnect(){
    	return false; // DISCONNECTED
    }

    public static void getParList(){

    }

    public static void singleTuneRequest(int id){

    }

    private static byte[] changeBytesPosition(byte[] byteArray){
        byte temp = byteArray[1];
        byteArray[1] = byteArray[0];
        byteArray[0] = temp;

        return byteArray;
    }
}