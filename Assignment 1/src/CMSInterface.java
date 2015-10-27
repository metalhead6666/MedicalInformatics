import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Simple program to open communications ports and connect to Agilent Monitor
 * Agilent Communication Interface - TO BE IMPLEMENTED
 *
 * @author Francisco Cardoso (fmcc@student.dei.uc.pt)
 * @author Ricardo Sal (ricsal@student.dei.uc.pt)
 * @version 1.2 - 30 Set 2003
 */

public class CMSInterface {
    private static byte START_MESSAGE = 0x1b;
    private final static int DEFAULT_LENGTH = 6; /* LENGTH + SRC_ID + DST_ID */
    private final static int DEFAULT_SIZE = 2;

    private static Semaphore semaphore;
    private static ComInterface _comInterface;
    private static appInterface _app;

    public CMSInterface() {
        semaphore = new Semaphore(0);
        new ReadStuff(semaphore);
    }

    /* changes the byte position to send */
    private static byte[] changeBytesPosition(byte... byteArray) {
        byte temp = byteArray[1];
        byteArray[1] = byteArray[0];
        byteArray[0] = temp;

        return byteArray;
    }

    /* sets the variables received when a command is called on the interface */
    public static void setVar(ComInterface comInterface, appInterface app){
        _comInterface = comInterface;
        _app = app;
    }

    /* creates and joins in an array the parts of the message */
    public static byte[] createArrayByteToSend(int size_array, short request){
        byte[] finalByteArray = new byte[size_array];
        byte[] length, dstId, srcId, req;
        int i = 0;

        length = createMiniByteArray((short)size_array);
        dstId = createMiniByteArray((short)Utils.DST_ID);
        srcId = createMiniByteArray((short)Utils.SRC_ID);
        req = createMiniByteArray(request);

        finalByteArray = addToArray(length, i, finalByteArray);
        i += 2;
        finalByteArray = addToArray(dstId, i, finalByteArray);
        i += 2;
        finalByteArray = addToArray(srcId, i, finalByteArray);
        i += 2;
        finalByteArray = addToArray(req, i, finalByteArray);

        return finalByteArray;
    }

    /* inserts into a segment the bytes needed */
    public static byte[] createMiniByteArray(short put){
        byte[] miniByte;

        miniByte = ByteBuffer.allocate(DEFAULT_SIZE).putShort(put).array();
        miniByte = changeBytesPosition(miniByte);

        return miniByte;
    }

    /* method to add a segment into the final array */
    public static byte[] addToArray(byte[] add, int pos, byte... array){
        array[pos] = add[0];
        array[pos + 1] = add[1];

        return array;
    }

    /* after the array to send is created, this method sends it to com */
    public static boolean sendMessageToCom(boolean type, byte[] byteArray){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(START_MESSAGE);

        try{
            outputStream.write(byteArray);
        }catch(IOException e){
            return !type;
        }

        _comInterface.writeBytes(outputStream.toByteArray());
        semaphore.take();

        return type;
    }

    /* method to connect, called by the interface */
    public boolean connect(ComInterface comInterface, appInterface app){
        byte[] finalArray;

        setVar(comInterface, app);
        finalArray = createArrayByteToSend(DEFAULT_LENGTH + DEFAULT_SIZE * 2, (short)Utils.CONNECT_REQ);
        finalArray[8] = finalArray[9] = 0;
        return sendMessageToCom(true, finalArray);
    }

    /* method to disconnect, called by the interface */
    public boolean disconnect(ComInterface comInterface, appInterface app) {
        setVar(comInterface, app);
        return sendMessageToCom(false, createArrayByteToSend(DEFAULT_LENGTH + DEFAULT_SIZE, (short)Utils.DISCONNECT_REQ));
    }

    /* method to get the par list, called by the interface */
    public void getParList(ComInterface comInterface, appInterface app){
        setVar(comInterface, app);
        sendMessageToCom(false, createArrayByteToSend(DEFAULT_LENGTH + DEFAULT_SIZE, (short)Utils.PAR_LIST_REQ));
    }

    /* method to get the single tune request, called by the interface */
    public void singleTuneRequest(ComInterface comInterface, appInterface app, String text){
        byte[] finalArray, idByte;
        int id = Integer.parseInt(text);

        setVar(comInterface, app);
        finalArray = createArrayByteToSend(DEFAULT_LENGTH + DEFAULT_SIZE * 2, (short)Utils.TUNE_REQ);
        idByte = createMiniByteArray((short)id);
        finalArray = addToArray(idByte, 8, finalArray);
        sendMessageToCom(false, finalArray);

        /*ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int id = Integer.parseInt(text);

        byte[] TUNE_ID;
        byte[] finalByteArray;
        byte[] TUNE_REQ = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) Utils.TUNE_REQ).array();
        TUNE_REQ = changeBytesPosition(TUNE_REQ);

        DST_ID = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) Utils.DST_ID).array();
        DST_ID = changeBytesPosition(DST_ID);
        SRC_ID = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) Utils.SRC_ID).array();
        SRC_ID = changeBytesPosition(SRC_ID);

        //FIXME
        //the lenght might not be this one, I just
        //copy and paste some code
        //I guess it's missing the TUNE_ID size here?
        LENGTH = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) (DEFAULT_LENGTH + DEFAULT_SIZE)).array();
        LENGTH = changeBytesPosition(LENGTH);

        TUNE_ID = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) id).array();
        TUNE_ID = changeBytesPosition(TUNE_ID);

        // TODO
        // I think there is something missing here


        outputStream.write(START_MESSAGE);

        try {
            outputStream.write(LENGTH);
            outputStream.write(DST_ID);
            outputStream.write(SRC_ID);

            outputStream.write(TUNE_REQ);
            outputStream.write(TUNE_ID);
        } catch (IOException e) {
            return;
        }

        finalByteArray = outputStream.toByteArray();

        comInterface.writeBytes(finalByteArray);

        semaphore.take();*/
    }

    /* class with a thread to read  */
    class ReadStuff extends Thread {
        private Semaphore semaphore;

        public ReadStuff(Semaphore semaphore) {
            this.semaphore = semaphore;
            this.start();
        }

        @Override
        public synchronized void run() {
            byte[] readArray;
            String string;
            ArrayList<Byte> response;

            while (true) {
                semaphore.release();

                string = "";

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                readArray = _comInterface.readBytes();
                System.out.println(readArray.length);

                if (readArray.length != 0) {
                    response = processResponse(readArray);
                } else {
                    response = null;
                }

                if (response == null) {
                    semaphore.take();
                } else {                           	
                    for (int i = 0; i < response.size(); i += 2) {
                    	if(i == response.size() - 1){
                    		string += "<" + response.get(i) + ">";
                    		break;
                    	}
                    	else{
                    		string += "<" + response.get(i) + "|" + response.get(i + 1) + ">";
                    	}                        
                    }
                    string += "\n";
                    _app.appendText(string);
                    response.clear();
                    semaphore.take();
                }
            }
        }

        private ArrayList<Byte> processResponse(byte[] response) {
            ArrayList<Byte> arrayList = new ArrayList<>();

            if (response == null) {
                return null;
            }

            if (response[0] != 0x1b) {
                return null;
            } else {
                for (int i = 1; i < response.length; i += 2) {
                    if (response[i] == 0x1b) {
                        if (response[i + 1] == 0xff) {
                            arrayList.add(response[i + 2]);
                            ++i;
                        }

                        //TODO
                        // this part might be wrong for the ParList
                        /*else {
                            return null;
                        }*/
                    } else {
                        try {
                            arrayList.add(response[i + 1]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("UPS  - Line: 230 + ArrayIndexOutOfBoundsException");
                        }
                    }

                    arrayList.add(response[i]);
                }
            }

            return arrayList;
        }

        private String processParList(ArrayList<Byte> responseParList) {
            String finalResponseProcessed = "";

            //TODO
            //i think this works well, but we need to test it
            for (int i = 1; i < responseParList.size(); ++i) {

                //responseParList.get(i) is ALWAYS the size
                //here we are processing ONLY one message at the time;
                int j;
                for (j = i; j < i + responseParList.get(i); j += 2) {
                    finalResponseProcessed += "<" + responseParList.get(j) + "|" + responseParList.get(j + 1) + ">";
                }

                //jumping the 0x1b of the next message
                i = j + 1;
            }

            return finalResponseProcessed;
        }

        /* Similar to processParList */
        private String processTuneRes(ArrayList<Byte> responseTuneReq) {
            String finalResponseProcessed = "";

            //get the size of the TUNE_RES
            int sizeOfMessage = responseTuneReq.get(1);

            //TODO:
            //read the documentation to check if this is right
            //process the response according to the size of it
            for (int i = 2; i < sizeOfMessage; i += 2) {
                finalResponseProcessed += "<" + responseTuneReq.get(i) + "|" + responseTuneReq.get(i + 1) + ">";
            }

            return finalResponseProcessed;
        }
    }
}
