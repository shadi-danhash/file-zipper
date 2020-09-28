package compressors;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LZWData implements Serializable {

    List<Integer> encodedList;
    String filename;
    ArrayList<LZWData> children = new ArrayList<>();
    FileType fileType;
    public static void Write(String path , LZWData lzwData) throws IOException {

        OutputStream outputStream = new FileOutputStream(path);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(lzwData);
        objectOutputStream.close();
    }
    
    public static LZWData Read(String path) throws IOException, ClassNotFoundException {
        InputStream inputStream = new FileInputStream(path);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        LZWData temp =  (LZWData) objectInputStream.readObject();
        objectInputStream.close();
        return temp;
    }
    public static String getSimpleName(String path){
        File f = new File(path);
        return f.getName();
    }
    public static String getSimpleName(File f){
        return f.getName();
    }
}
