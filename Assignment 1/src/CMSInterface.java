import javax.comm.CommPort;
import javax.comm.CommPortIdentifier;
import javax.comm.CommPortOwnershipListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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

    public static boolean connect(ComInterface comInterface){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] finalByteArray;
        byte[] CONNECT_REQ = ByteBuffer.allocate(DEFAULT_SIZE).putInt(1).array();
        byte[] TICK_PERIOD = new byte[2];

        CONNECT_REQ = changeBytesPosition(CONNECT_REQ);
        DST_ID = ByteBuffer.allocate(DEFAULT_SIZE).putInt(Utils.DST_ID).array();
        DST_ID = changeBytesPosition(DST_ID);
        SRC_ID = ByteBuffer.allocate(DEFAULT_SIZE).putInt(Utils.SRC_ID).array();
        SRC_ID = changeBytesPosition(SRC_ID);

        LENGTH = ByteBuffer.allocate(2).putInt(DEFAULT_LENGTH + DEFAULT_SIZE * 2).array();
        LENGTH = changeBytesPosition(LENGTH);

        outputStream.write(START_MESSAGE);

        try{
            outputStream.write(LENGTH);
            outputStream.write(DST_ID);
            outputStream.write(SRC_ID);
            outputStream.write(CONNECT_REQ);
            outputStream.write(TICK_PERIOD);
        }catch(IOException e){
            ;
        }

        finalByteArray = outputStream.toByteArray();
        comInterface.writeBytes(finalByteArray);

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