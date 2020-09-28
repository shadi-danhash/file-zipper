package compressors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LzwUnzipper {

    private File file;
    private String destination;
    private String source;
    private LZWData lzwData;
    private String zipped;

    public LzwUnzipper(File file, String destination) {
        this.file = file;
        this.destination = destination;
    }

    public LzwUnzipper(String source, String destination) {
        file = new File(source);
        this.destination = destination;
        this.source = source;
    }

    public boolean unzip() throws IOException, ClassNotFoundException {
        //lzwData = lzwData.Read(source);
        Decode_String();
        Create_decoded_file();
        return true;
    }

    public void Decode_String() {

        double MAX_TABLE_SIZE = Math.pow(2, 11);

        List<Integer> get_compress_values = new ArrayList<Integer>();
        int table_Size = 255;
        get_compress_values = lzwData.encodedList;
        Map<Integer, String> Table = new HashMap<Integer, String>();
        for (int i = 0; i < 255; i++) {
            Table.put(i, "" + (char) i);
        }
        String Encode_values = "";
        try{
        Encode_values = "" + (char) (int) get_compress_values.remove(0);
        }catch(Exception e){
            
        }
        StringBuffer decoded_values = new StringBuffer(Encode_values);
        String get_value_from_table = null;
        
        for (int check_key : get_compress_values) {

            if (Table.containsKey(check_key)) {
                get_value_from_table = Table.get(check_key);
            } else if (check_key == table_Size) {
                get_value_from_table = Encode_values + Encode_values.charAt(0);
            }

            decoded_values.append(get_value_from_table);

            if (table_Size < MAX_TABLE_SIZE) {
                Table.put(table_Size++, Encode_values + get_value_from_table.charAt(0));
            }

            Encode_values = get_value_from_table;
        }
        zipped = decoded_values.toString();
    }

    private  void Create_decoded_file() throws IOException {
        
        FileOutputStream fileOutputStream = new FileOutputStream(destination);
        if (lzwData.fileType == FileType.IMAGE) {
            byte[] imageData = Base64.getDecoder().decode(zipped);

            fileOutputStream.write(imageData);
        } else if (lzwData.fileType == FileType.TEXT) {
            FileWriter writer = new FileWriter(destination, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
        try {

            bufferedWriter.write(zipped);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        bufferedWriter.flush();
        bufferedWriter.close();
        }
        fileOutputStream.close();
    }
    
     public static boolean AutoUnzip(String src, String dest) {
        try {
            LZWData lzwData = LZWData.Read(src);
            SerializedDecode(lzwData, dest);
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static void SerializedDecode(LZWData lzwData, String dest) throws IOException, ClassNotFoundException {
        if (lzwData.fileType == FileType.FOLDER) {
            String name = dest + "\\" + LZWData.getSimpleName(lzwData.filename);
            File dir = new File(name);
            if (dir.mkdirs()) {
                for (LZWData child : lzwData.children) {
                    SerializedDecode(child, name);
                }
            } else {
                throw new RuntimeException("cannot create folder " + dir);
            }
        }else {
            String newDest = dest+ LZWData.getSimpleName(lzwData.filename);
            LzwUnzipper lzwUnzipper = new LzwUnzipper("", newDest);
            lzwUnzipper.lzwData = lzwData;
            lzwUnzipper.unzip();

        }
    }
}
