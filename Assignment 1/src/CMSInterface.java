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

        //FIXME PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
        byte[] TRANS_HD = {
                readArray[2], readArray[1],
                readArray[4], readArray[3],
                readArray[6], readArray[5],
        };

        byte[] CONNECT_RSP = {
                readArray[8], readArray[7],
        };

        byte[] WINDOW_SIZE = {
                readArray[10], readArray[9],
        };

        byte[] COMPAT = {
                readArray[12], readArray[11],
        };

        byte[] ERRETURN = {
                readArray[14], readArray[13],
        };

        //TODO
        // PRINT TO THE TEXT AREA

        return true; //CONNECTED
    }

    public static boolean disconnect(ComInterface comInterface){
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

        //FIXME
        byte[] TRANS_HD = {
                readArray[2], readArray[1],
                readArray[4], readArray[3],
                readArray[6], readArray[5],
        };

        byte[] DISCONNECT_RSP = {
                readArray[8], readArray[7],
        };

        byte[] RESPONSE = {
                readArray[10], readArray[9],
        };

        //TODO
        // PRINT TO THE TEXT AREA

        return false; //DISCONNECTED
    }

    public static void getParList(){

    }

    public static void singleTuneRequest(int id){

    }

    private static byte[] changeBytesPosition(byte... byteArray){
        byte temp = byteArray[1];
        byteArray[1] = byteArray[0];
        byteArray[0] = temp;

        return byteArray;
    }

    //FIXME
    private static byte[] func_27_27255(byte... byteArray){
        byte[] temp = new byte[3];
        temp[0] = byteArray[0];
        temp[1] =  (byte) 255;
        temp[2] = byteArray[1];

        return temp;
    }

    //FIXME
    private static byte[] func_27255_27(byte... byteArray){
        byte[] temp = new byte[2];
        temp[0] = byteArray[0];
        temp[1] = byteArray[2];

        return temp;
    }
}