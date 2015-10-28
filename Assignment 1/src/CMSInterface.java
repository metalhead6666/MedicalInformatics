import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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
    
    private static boolean isParList = false;
    private static int actualParListMessageLength = 0;
    private static int totalParListMessageLength = 0;
    private static short totalNumberParListMessages = 0;
    private static int actualNumberParListMessage = 0;
    private static String processedParListMessage = "";

    public CMSInterface(){
        semaphore = new Semaphore(0);
        new ReadStuff(semaphore);
    }

    /* changes the byte position to send */
    private static byte[] changeBytesPosition(byte... byteArray){
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

        isParList = false;
        setVar(comInterface, app);
        finalArray = createArrayByteToSend(DEFAULT_LENGTH + DEFAULT_SIZE * 2, (short)Utils.CONNECT_REQ);
        finalArray[8] = finalArray[9] = 0;
        
        return sendMessageToCom(true, finalArray);
    }

    /* method to disconnect, called by the interface */
    public boolean disconnect(ComInterface comInterface, appInterface app){
    	isParList = false;
        setVar(comInterface, app);
        
        return sendMessageToCom(false, createArrayByteToSend(DEFAULT_LENGTH + DEFAULT_SIZE, (short)Utils.DISCONNECT_REQ));
    }

    /* method to get the par list, called by the interface */
    public void getParList(ComInterface comInterface, appInterface app){
    	isParList = true;
        setVar(comInterface, app);
        sendMessageToCom(false, createArrayByteToSend(DEFAULT_LENGTH + DEFAULT_SIZE, (short)Utils.PAR_LIST_REQ));
    }

    /* method to get the single tune request, called by the interface */
    public void singleTuneRequest(ComInterface comInterface, appInterface app, String text){
        byte[] finalArray, idByte;
        int id = Integer.parseInt(text);

        isParList = false;
        setVar(comInterface, app);
        finalArray = createArrayByteToSend(DEFAULT_LENGTH + DEFAULT_SIZE * 2, (short)Utils.TUNE_REQ);
        idByte = createMiniByteArray((short)id);
        finalArray = addToArray(idByte, DEFAULT_LENGTH + DEFAULT_SIZE, finalArray);
        sendMessageToCom(false, finalArray);
    }

    /* class with a thread to read  */
    class ReadStuff extends Thread{
        private Semaphore semaphore;

        public ReadStuff(Semaphore semaphore){
            this.semaphore = semaphore;
            this.start();
        }

        @Override
        public synchronized void run(){
            byte[] readArray, response;
            String string;       

            while(true){
                semaphore.release();
                string = "";

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }

                readArray = _comInterface.readBytes();

                if(readArray.length != 0){
                    if(isParList){
                    	processParListMessage(readArray);
                    }
                    
                    else{
                    	response = processResponse(readArray);
                        
                        for(int i = 0; i < response.length; i += 2){
                        	string += "<" + response[i] + "|" + response[i + 1] + ">";                     
                        }
                        
                        string += "\n";
                        _app.appendText(string);
                    }
                }
                
                semaphore.take();
            }
        }
        
        private void processParListMessage(byte... readArray){
        	byte[] temp;
        	
        	if(totalNumberParListMessages == 0){
        		temp = new byte[2];
        		temp[0] = readArray[0];
        		temp[1] = readArray[1];
        		
        		totalNumberParListMessages = readArray[9];
        		actualNumberParListMessage = readArray[8];
        		totalParListMessageLength = ByteBuffer.wrap(temp).getShort();
        		actualParListMessageLength = 0;
        	}
        	
        	for(int i = 0; i < readArray.length; i += 2){
        		
        	}
        }

        private byte[] processResponse(byte... response){
            byte[] byteArray = new byte[response.length];

            if(response == null || response[0] != 0x1b){
                return null;
            }
            
            for(int i = 1, j = 0; i < response.length; i += 2, j += 2){
                if(response[i] == 0x1b){
                    if(response[i + 1] == 0xff){
                        byteArray[j] = response[i + 2];
                        ++i;
                    }
                } 
                
                else{
                	byteArray[j] = response[i + 1];
                }

                byteArray[j + 1] = response[i];
            }

            return byteArray;
        }
    }
}
