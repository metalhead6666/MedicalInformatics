import javax.comm.CommPort;
import javax.comm.CommPortIdentifier;
import javax.comm.CommPortOwnershipListener;
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

    public static boolean connect(ComInterface comInterface) {
        byte[] CONNECT_REQ = ByteBuffer.allocate(DEFAULT_SIZE).putInt(1).array();

        DST_ID = ByteBuffer.allocate(DEFAULT_SIZE).putInt(Utils.DST_ID).array();
        SRC_ID = ByteBuffer.allocate(DEFAULT_SIZE).putInt(Utils.SRC_ID).array();

        //LENGTH = ByteBuffer.allocate(2).putInt(Utils.DST_ID + Utils.SRC_ID + )
        //comInterface.writeBytes();

        return true; // CONNECTED
    }

    public static boolean disconnect() {
    	return false; // DISCONNECTED
    }

    public static void getParList() {

    }

    public static void singleTuneRequest(int id) {

    }

    private static byte[] changeBytesPosition(byte[] byteArray){
        byte temp = byteArray[1];
        byteArray[1] = byteArray[0];
        byteArray[0] = temp;

        return byteArray;
    }
}