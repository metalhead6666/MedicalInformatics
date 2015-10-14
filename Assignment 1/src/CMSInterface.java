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

    private static Semaphore semaphore;
    private static ComInterface _comInterface;
    private static appInterface _appInterface;

    public CMSInterface(){
        semaphore = new Semaphore(0);
        new ReadStuff(semaphore);
    }

    private static byte[] changeBytesPosition(byte... byteArray){
        byte temp = byteArray[1];
        byteArray[1] = byteArray[0];
        byteArray[0] = temp;

        return byteArray;
    }

    public static boolean sendMessageCD(ComInterface comInterface, appInterface app, boolean type){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] finalByteArray;
        byte[] CONNECT_REQ = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) Utils.CONNECT_REQ).array();
        byte[] TICK_PERIOD = new byte[DEFAULT_SIZE];

        _comInterface = comInterface;
        _appInterface = app;

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

            if(type){
                outputStream.write(TICK_PERIOD);
            }
        }catch(IOException e){
            return !type;
        }

        finalByteArray = outputStream.toByteArray();
        comInterface.writeBytes(finalByteArray);

        semaphore.take();

        return type;
    }

    public static boolean connect(ComInterface comInterface, appInterface app){
        return sendMessageCD(comInterface, app, true);
    }

    public static boolean disconnect(ComInterface comInterface, appInterface app){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] finalByteArray;
        byte[] DISCONNECT_REQ = ByteBuffer.allocate(DEFAULT_SIZE).putShort((short) Utils.DISCONNECT_REQ).array();

        _comInterface = comInterface;
        _appInterface = app;

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

        semaphore.take();

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

    /*public static void singleTuneRequest(int id){

    }*/

    class ReadStuff extends Thread{
        private Semaphore semaphore;

        public ReadStuff(Semaphore semaphore){
            this.semaphore = semaphore;
            this.start();
        }

        @Override
        public synchronized void run(){
            byte[] readArray;
            String string;
            ArrayList<Byte> response;

            while(true){
                semaphore.release();

                string = "";

                readArray = _comInterface.readBytes();
                System.out.println(readArray.length);

                response = processResponse(readArray);

                if(response == null){
                    _appInterface.appendText("Some error happened while reading, do something about it!");
                }

                else{
                    for(int i = 0; i < response.size(); i += 2){
                        string += "<" + response.get(i) + "|" + response.get(i + 1) + ">";
                    }

                    _appInterface.appendText(string);
                    response.clear();
                }
            }
        }

        private ArrayList<Byte> processResponse(byte[] response){
            ArrayList<Byte> arrayList = new ArrayList<>();

            if(response[0] != 0x1b){
                return null;
            }

            else{
                for(int i = 1; i < response.length; i += 2){
                    if(response[i] == 0x1b){
                        if(response[i + 1] == 0xff){
                            arrayList.add(response[i + 2]);
                            ++i;
                        }

                        else{
                            return null;
                        }
                    }

                    else{
                        arrayList.add(response[i + 1]);
                    }

                    arrayList.add(response[i]);
                }
            }

            return arrayList;
        }
    }
}