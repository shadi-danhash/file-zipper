package compressors;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Base64;

public class Reader {

    private static StringBuilder stringBuilder;

    public static String read(File file, FileType fileType) throws IOException {
        if (fileType == FileType.IMAGE || fileType==FileType.ELSE)
            return readImg(file);
        else return readTxt(file);

    }

    public static String readTxt(File file) throws IOException {
        stringBuilder = new StringBuilder();
        Charset encoding = Charset.defaultCharset();
        handleFile(file, encoding);
        return stringBuilder.toString();
    }

    public static String readImg(File file) throws IOException {
//        int[] data = readFile(file);
//        String asTxt = "";
//        for (int i = 0; i < data.length; i++) {
//            char c = (char) data[i];
//            asTxt += c;
//        }
//        return asTxt;

        FileInputStream fileInputStream = new FileInputStream(file);
        byte imageData[] = new byte[(int)file.length()];
        fileInputStream.read(imageData);
        return Base64.getEncoder().encodeToString(imageData);
    }

    private static void handleFile(File file, Charset encoding)
            throws IOException {
        try (InputStream in = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(in, encoding);
             // buffer for efficiency
             BufferedReader buffer = new BufferedReader(reader)) {
            handleCharacters(buffer);
        }
    }

    private static void handleCharacters(BufferedReader reader)
            throws IOException {
        int r;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            stringBuilder.append(ch);
        }
    }

    public static int[] readFile(File file) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(file);
        int[] data = new int[(int) file.length()];
        int intRead;
        int i = 0;
        while ((intRead = fileInputStream.read()) != -1) {
            data[i] = intRead;
            i++;
        }
        return data;

    }

//    public static int[] readFile(File file) throws IOException {
//
//        FileInputStream fileInputStream = new FileInputStream(file);
//        int[] data = new int[(int) file.length()];
//        int a,b,c;
//        int i = 0;
//        while (true) {
//            a = fileInputStream.read();
//            b = fileInputStream.read();
//            c = fileInputStream.read();
//            int pix = concat(a,b,c);
//            data[i] = pix;
//            if (a==-1 || b==-1 || c==-1)
//                break;
//            i++;
//        }
//        return data;
//
//    }
//    private static int concat(int a,int b,int c){
//        int pix = a;
//        if(b!=-1){pix*=8;pix+=b;}
//        if(c!=-1){pix*=8;pix+=b;}
//        return pix;
//
//    }
}
