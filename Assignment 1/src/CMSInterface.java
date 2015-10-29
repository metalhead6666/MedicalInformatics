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
    private final static byte START_MESSAGE = 0x1b;
    private final static byte ESCAPE_MESSAGE = (byte)0xff;
    private final static int DEFAULT_LENGTH = 6; /* LENGTH + SRC_ID + DST_ID */
    private final static int DEFAULT_SIZE = 2;

    private static Semaphore semaphore;
    private static ComInterface _comInterface;
    private static appInterface _app;
    
    private static boolean isParList = false;
    private static int actualParListMessageLength = 0;
    private static int totalParListMessageLength = 0;
    private static short totalNumberParListMessages = 0;
    private static int actualNumberParListMessage = 1;
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
                        	//TODO depending on the command code response
                        	//we need to show different messages
                        	//maybe having a different function for each one?
                        	//the responses aren't all equal, that's the problem...
                        	if(response[6] == Utils.CONNECT_RES || response[7] == Utils.CONNECT_RES){
                            	string += "<"+responseASCIIConversion(i, Utils.CONNECT_RES)+"=[" + response[i] + "|" + response[i + 1] + "]>";                     

                        	}
                        	else if(response[6] == Utils.TUNE_RES || response[7] == Utils.TUNE_RES){
                            	string += "<"+responseASCIIConversion(i, Utils.TUNE_RES)+"=[" + response[i] + "|" + response[i + 1] + "]>";                     
                        	}
                        }
                        
                        string += "\n";
                        _app.appendText(string);
                    }
                }

                semaphore.take();
            }
        }
        
        private String responseASCIIConversion(int i, int code){
        	try{
	        	switch (i) {
				case 0:
					return "length";
				case 2:
					return "dst_id";
				case 4:
					return "src_id";
				case 6:
					return "command_code";
				}
	        	
	        	if(code == Utils.CONNECT_RES){
	        		switch (i){
		        		case 8:
		        			return "window_size";
		        		case 10:
		        			return "compat";
		        		case 12:
		        			return "error";
	        		}
	        	}
	        	else if (code == Utils.TUNE_RES){
	        		switch (i){
		        		case 8:
		        			return "tune_id";
		        		default:
		        			return "message";
	        		}
	        	}
	        	
	        	else if (code == Utils.PAR_LIST_RES){
	        		switch (i){
		        		case 8:
		        			return "actual|total";
		        		default:
		        			return "message";
	        		}
	        	}
	        	
	        	return "none";
        	} catch (Exception e){
        		return "e";
        	}
        }
        
        private void processParListMessage(byte... readArray){
        	byte[] temp;
        	
        	if(totalNumberParListMessages == 0){
        		temp = new byte[2];
        		temp[0] = readArray[2];
        		temp[1] = readArray[1];
        		
        		totalNumberParListMessages = readArray[9];
        		actualNumberParListMessage = readArray[8];
        		totalParListMessageLength = ByteBuffer.wrap(temp).getShort();
        		actualParListMessageLength = 0;
        	}
        	
        	for(int i = 0; i < readArray.length; i += 2){        		        		
        		if(actualParListMessageLength == totalParListMessageLength){
        			if(i + 2 >= readArray.length){
        				break;
        			}
        			
        			++i;
        		
        			++actualNumberParListMessage;
            		actualParListMessageLength = 0;
        			
        			temp = new byte[2];
            		temp[0] = readArray[i + 1];
            		temp[1] = readArray[i];
            		totalParListMessageLength = ByteBuffer.wrap(temp).getShort();
            		processedParListMessage += "\n";
                    _app.appendText(processedParListMessage);
                    processedParListMessage = "";
        		}        	
        		
        		if(readArray[i] == START_MESSAGE){
        			++i;
        			
        			if(i >= readArray.length){
        				totalParListMessageLength = 0;
        				actualParListMessageLength = 0;
        				break;
        			}
        			
        			else if(readArray[i] == ESCAPE_MESSAGE){
        				if(readArray.length != i+1){
        					processedParListMessage += "<"+ responseASCIIConversion(i+1, Utils.PAR_LIST_RES) + "=" + readArray[i + 1] + "|" + readArray[i - 1] + ">";
        				}
        			}
        			
        			else{
        				if(readArray.length != i+1){
        					processedParListMessage += "<" + responseASCIIConversion(i+1, Utils.PAR_LIST_RES) + "=" + readArray[i + 1] + "|" + readArray[i] + ">";
        				}
        			}
        		}
        		
        		else{
        			if(readArray.length != i+1){
        				processedParListMessage += "<" + responseASCIIConversion(i+1, Utils.PAR_LIST_RES) + "=" + readArray[i + 1] + "|" + readArray[i] + ">";
        			}
        		}
        		
        		actualParListMessageLength += 2;
        	}
        	
        	System.out.println(actualNumberParListMessage + " - " + totalNumberParListMessages);
        	System.out.println(actualParListMessageLength + " - " + totalParListMessageLength + "\n");
        	
        	if(actualParListMessageLength == totalParListMessageLength){
    			++actualNumberParListMessage;
        		actualParListMessageLength = 0;
    			
        		processedParListMessage += "\n";
                _app.appendText(processedParListMessage);
                processedParListMessage = "";
    		}
        	
        	if(actualNumberParListMessage == totalNumberParListMessages){
        		System.out.println("OUT");
        		processedParListMessage = "";
        		isParList = false;
        		actualNumberParListMessage = 1;
        		totalNumberParListMessages = 0;
        		actualParListMessageLength = 0;
        		totalParListMessageLength = 0;        	
        	}
        }

        private byte[] processResponse(byte... response){
            byte[] temp, byteArray;        	

            if(response == null || response[0] != 0x1b){
                return null;
            }
            
            temp = new byte[2];
            temp[0] = response[2];
            temp[1] = response[1];
            byteArray = new byte[ByteBuffer.wrap(temp).getShort()];
            
            for(int i = 1, j = 0; i < response.length; i += 2, j += 2){
                if(response[i] == START_MESSAGE){
                    if(response[i + 1] == ESCAPE_MESSAGE){
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
