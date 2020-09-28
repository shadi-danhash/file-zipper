package compressors;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LzwZipper {

    private HashMap<String, Double> probabilities;
    private FileType fileType = FileType.ELSE;
    private File file;
    private String destination;
    private String source;
    private String text;
    LZWData lzwData;
        


    public LzwZipper(String source, String destination, FileType fileType) {
        this.source = source;
        file = new File(source);
        this.destination = destination;
        if (fileType != null) {
            this.fileType = fileType;
        }
        probabilities = new HashMap<>();
        lzwData = new LZWData();
        lzwData.fileType = fileType;
        lzwData.filename = source;

    }

    public boolean zip() throws IOException {
        readData();
        encoding();
        LZWData.Write(destination, lzwData);
        return true;
    }

    private void readData() throws IOException {
        switch (fileType) {
            case IMAGE:
                text = Reader.read(file,fileType);
                break;
            case TEXT:
                text = Reader.read(file,fileType);
                break;
            case FOLDER:
                break;
            case ELSE:
                break;
        }
    }

    private void encoding() {
        Map<String, Integer> Table = new HashMap<String, Integer>();
        double table_Size = 255;
        double MAX_TABLE_SIZE = Math.pow(2, 11);
        for (int i = 0; i < 255; i++) {
            Table.put("" + (char) i, i);
        }

        String initString = "";
        List<Integer> encoded_values = new ArrayList<Integer>();
        for (char symbol : text.toCharArray()) {
            String Str_Symbol = initString + symbol;
            if (Table.containsKey(Str_Symbol)) {
                initString = Str_Symbol;
            } else {
                encoded_values.add(Table.get(initString));

                if (table_Size < MAX_TABLE_SIZE) {
                    Table.put(Str_Symbol, (int) table_Size++);
                }
                initString = "" + symbol;
            }
        }
        if (!initString.equals(""))
            encoded_values.add(Table.get(initString));
        
        lzwData.encodedList = encoded_values;
    }
    
    public void compressImage() throws IOException {
        
        HashMap<String, Integer> dictionary = new HashMap<>();
        int dictSize = 256;
        String P = "",filename="",BP="";
        byte inputByte;
        byte[] buffer = new byte[3];
        boolean isLeft = true;
        
        int i,byteToInt;
        char C;
        
        // Character dictionary 
        for(i = 0;i < 256;i++) {
            dictionary.put(Character.toString((char)i),i);
        }
        
        // Read input file and output file
        RandomAccessFile inputFile = new RandomAccessFile(filename,"r");
        RandomAccessFile outputFile = new RandomAccessFile(destination,"rw");
        
        try {
        
            // Read first byte to initialize P
            inputByte = inputFile.readByte();
            byteToInt = new Byte(inputByte).intValue();
            
            if(byteToInt < 0) byteToInt += 256;
            C = (char) byteToInt;
            P = ""+C;
            
            while(true) {
                inputByte = inputFile.readByte();
                byteToInt = new Byte(inputByte).intValue();
            
                if(byteToInt < 0) byteToInt += 256;
                C = (char) byteToInt;
                
                // if P+C is present in dictionary
                if(dictionary.containsKey(P+C)) {
                    P = P+C;
                }
                

                else {
                    BP = convertTo12Bit(dictionary.get(P));
                    if(isLeft) {
                        buffer[0] = (byte) Integer.parseInt(BP.substring(0,8),2);  
                        buffer[1] = (byte) Integer.parseInt(BP.substring(8,12)+"0000",2);                   
                    }
                    else {
                        buffer[1] += (byte) Integer.parseInt(BP.substring(0,4),2); 
                        buffer[2] = (byte) Integer.parseInt(BP.substring(4,12),2);
                        for(i=0;i<buffer.length;i++) {
                            outputFile.writeByte(buffer[i]);
                            buffer[i]=0;
                        }
                    }
                    isLeft = !isLeft;
                    if(dictSize < 4096) dictionary.put(P+C,dictSize++);
                    
                    P=""+C;
                }            
            }
        
        }

        catch(IOException ie) {
            BP = convertTo12Bit(dictionary.get(P));
            if(isLeft) {
                buffer[0] = (byte) Integer.parseInt(BP.substring(0,8),2);  
                buffer[1] = (byte) Integer.parseInt(BP.substring(8,12)+"0000",2);
                outputFile.writeByte(buffer[0]);  
                outputFile.writeByte(buffer[1]);                
            }
            else {
                buffer[1] += (byte) Integer.parseInt(BP.substring(0,4),2); 
                buffer[2] = (byte) Integer.parseInt(BP.substring(4,12),2);
                for(i=0;i<buffer.length;i++) {
                     outputFile.writeByte(buffer[i]);
                     buffer[i]=0;
                }
            }
            inputFile.close();
            outputFile.close();        
        }
    
    }
    
    public static String convertTo12Bit(int i) {
        String to12Bit = Integer.toBinaryString(i);
        while (to12Bit.length() < 12) to12Bit = "0" + to12Bit;
        return to12Bit;
    }
    
    private static boolean IsImage(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg");
    }

    private static boolean IsText(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension.equals("txt");
    }
    
    public static boolean AutoZip(String path, String dest) {
        try {
            LZWData lzwData = SerializedZip(path);
            lzwData.Write(dest + ".lzw", lzwData);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static LZWData SerializedZip(String path) throws IOException {

        File file = new File(path);
        if (file.isDirectory()) {
            LZWData lzwData = new LZWData();
            lzwData.fileType = FileType.FOLDER;
            lzwData.filename = path;
            lzwData.children = new ArrayList<>();
            String childrenNames[] = file.list();
            for (int i = 0; i < childrenNames.length; i++) {
                lzwData.children.add(SerializedZip(path + "\\" + childrenNames[i]));
            }
            return lzwData;
        } else if (IsImage(path)) {
            LzwZipper lzwZipper = new LzwZipper(path, null, FileType.IMAGE);
            return lzwZipper.makeLzwData();
        } else if (IsText(path)) {
            LzwZipper lzwZipper = new LzwZipper(path, null, FileType.TEXT);
            return lzwZipper.makeLzwData();
        }
        
        LzwZipper lzwZipper = new LzwZipper(path, null, FileType.ELSE);
        return lzwZipper.makeLzwData();
    }
    private LZWData makeLzwData() throws IOException {
        readData();
        encoding();
        return lzwData;
    }
}
