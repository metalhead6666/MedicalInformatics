import javax.comm.CommPortIdentifier;

/**
 * Simple program to open communications ports and connect to Agilent Monitor
 * Agilent Communication Interface - TO BE IMPLEMENTED
 * @version 1.2 - 30 Set 2003
 * @author Francisco Cardoso (fmcc@student.dei.uc.pt)
 * @author Ricardo Sal (ricsal@student.dei.uc.pt)
 */


public class CMSInterface {

    public static boolean connect(Object item) {
    	CommPortIdentifier port = (CommPortIdentifier) item;
    	
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