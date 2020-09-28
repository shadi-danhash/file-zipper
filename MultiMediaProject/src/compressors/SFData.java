package compressors;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SFData implements Serializable {

    byte leftBits;
    byte[] data;
    HashMap<Character,String> table = new HashMap<>();
    String filename;
    FileType fileType;
    ArrayList<SFData> children = new ArrayList<>();

    public static void Write(String path , SFData sfData) throws IOException {
        OutputStream outputStream = new FileOutputStream(path);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(sfData);
        objectOutputStream.close();
    }

    public static SFData Read(String path) throws IOException, ClassNotFoundException {
        InputStream inputStream = new FileInputStream(path);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        SFData temp =  (SFData) objectInputStream.readObject();
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

    @Override
    public String toString() {
        String s =  "\ndata type: "+ fileType
                +"\nchildren number: "+ children.size();
        for (int i=0;i<children.size();i++){
            s+="\n len of file: " + children.get(i).data.length+
                    " file name: "+ children.get(i).filename+
                    " map length: "+children.get(i).table.size();
            s+=children.get(i).toString();
        }
        if (data!=null)
            s+="\ndata length: "+data.length;
        s+="\nfile name: "+filename;
        s+="\nleft bits: "+leftBits+"\n";
        return s;
    }
}
