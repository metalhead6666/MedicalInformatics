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
    private static byte BH = 27;
    private int TOTAL_LENGTH = 6;

    public static boolean connect(ComInterface comInterface) {
        byte CONNECT_RQS = 1;
        //byte[] LENGTH = ByteBuffer.allocate(2).putInt(Utils.DST_ID + Utils.SRC_ID + )


        comInterface.writeBytes();
        return true; // CONNECTED
    }

    public static boolean disconnect() {
    	return false; // DISCONNECTED
    }

    public static void getParList() {

    }

    public static void singleTuneRequest(int id) {

    }
}