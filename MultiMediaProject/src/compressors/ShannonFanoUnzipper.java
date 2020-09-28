package compressors;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;

public class ShannonFanoUnzipper {

    private SFData sfData;
    private String source;
    private String destination;
    private FileType fileType;
    private File file;
    private String zipped;
    private HashMap<String, Character> flipedTable;

    ShannonFanoUnzipper(File file, String destination, FileType fileType) {
        this.file = file;
        this.destination = destination;
        if (fileType != null)
            this.fileType = fileType;
        source = file + "";
    }

    public ShannonFanoUnzipper(String source, String destination, FileType fileType) {
        this.source = source;
        file = new File(source);
        this.destination = destination;
        if (fileType != null)
            this.fileType = fileType;

    }

//    private String decoded = "";


    private void decode() throws IOException {
        // Decodes the encoded string using Coding Map.
        StringBuilder toBeWritten = new StringBuilder();
        String code = "";
        for (int i = 0; i < zipped.length(); i++) {
            code += zipped.charAt(i);
//            c=flipedTable.get(code);
            if (flipedTable.containsKey(code)) {
                toBeWritten.append(flipedTable.get(code));
                code = "";
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(destination);
        if (fileType == FileType.IMAGE) {
            byte[] imageData = Base64.getDecoder().decode(toBeWritten.toString());

            fileOutputStream.write(imageData);
        } else {
            FileWriter fileWriter = new FileWriter(destination);
            BufferedWriter bf = new BufferedWriter(fileWriter);
            bf.write(toBeWritten.toString());
            bf.close();
//            for (int i=0;i<toBeWritten.toString().length();i++)
//                fileOutputStream.write(toBeWritten.toString().charAt(i));
        }

        fileOutputStream.close();
    }


    public boolean unzip() throws IOException, ClassNotFoundException {
        sfData = SFData.Read(source);
        convertByteArrayToString();
        flipTable();
        decode();
        return true;
    }

    public boolean unzip(SFData sfData) throws IOException, ClassNotFoundException {
        convertByteArrayToString();
        flipTable();
        decode();
        return true;
    }

    private void flipTable() {
        flipedTable = new HashMap<>();
        sfData.table.forEach((key, value) -> {
            flipedTable.put(value, key);
        });
    }

    private void convertByteArrayToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < sfData.data.length; i++) {
            String sub;
            if (i == sfData.data.length - 1)
                sub = convertByteToString(sfData.data[i], sfData.leftBits);
            else
                sub = convertByteToString(sfData.data[i], 0);
            stringBuilder.append(sub);
        }
        zipped = stringBuilder.toString();
    }

    private String convertByteToString(Byte b, int leftBits) {
        String s = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');

        s = s.substring(leftBits, s.length());
        return s;
    }

    public static boolean AutoUnzip(String src, String dest) {
        try {
            SFData sfData = SFData.Read(src);
            SerializedDecode(sfData, dest);
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void SerializedDecode(SFData sfData, String dest) throws IOException, ClassNotFoundException {
        if (sfData.fileType == FileType.FOLDER) {
            String name = dest + "\\" + SFData.getSimpleName(sfData.filename);
            File dir = new File(name);
            if (dir.mkdirs()) {
                for (SFData child : sfData.children) {
                    SerializedDecode(child, name);
                }
            } else {
                throw new RuntimeException("cannot create folder " + dir);
            }
        } else {
            String newDest = dest + SFData.getSimpleName(sfData.filename);
            AutoWrite(sfData, newDest, sfData.fileType);
        }
    }

    private static void AutoWrite(SFData sfData, String dest, FileType fileType) throws IOException, ClassNotFoundException {
        ShannonFanoUnzipper shannonFanoUnzipper = new ShannonFanoUnzipper("", dest, fileType);
        shannonFanoUnzipper.sfData = sfData;
        shannonFanoUnzipper.unzip(sfData);
    }
}
