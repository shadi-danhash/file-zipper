package compressors;

import javafx.util.Pair;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ShannonFanoZipper {

    private HashMap<Character, Double> probabilities;
    private FileType fileType = FileType.ELSE;
    private File file;
    private String destination;
    private String source;
    private Double[] probabilitiesArray;
    private ArrayList<Node> nodes = new ArrayList<>();
    private String text = "";
    private String zipped = "";
    private SFData sfData = new SFData();
    private HashMap<Character, String> table = new HashMap<>();

    private class Node implements Comparable<Node> {
        Object symbol;
        Double probability;
        String code;

        public Node(Object symbol, Double probability) {
            this.symbol = symbol;
            this.probability = probability;
            this.code = "";
        }

        @Override
        public int compareTo(Node other) {
            return this.probability.compareTo(other.probability);
        }

        public Object getSymbol() {
            return symbol;
        }

        public Double getProbability() {
            return probability;
        }

        public String getCode() {
            return code;
        }

        public void appendCode(char c) {
            code += c;
        }

    }


    public ShannonFanoZipper(File file, String destination, FileType fileType) {
        this.file = file;
        this.destination = destination;
        if (fileType != null)
            this.fileType = fileType;
        source = file + "";
        probabilities = new HashMap<>();
    }

    public ShannonFanoZipper(String source, String destination, FileType fileType) {
        this.source = source;
        file = new File(source);
        this.destination = destination;
        if (fileType != null)
            this.fileType = fileType;
        probabilities = new HashMap<>();

    }

    public boolean zip() throws IOException {
        readData();
        initializeNodes(text);
        split(0, nodes.size());
        createTable();
        try {
            createBitsAsString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        createDataToStore(zipped);
        SFData.Write(destination, sfData);
        return true;
    }

    private SFData makeSFData() throws IOException {
        readData();
        initializeNodes(text);
        split(0, nodes.size());
        createTable();
        createBitsAsString();
        createDataToStore(zipped);
        return sfData;
    }

    private void readData() throws IOException {
        switch (fileType) {
            case IMAGE:
                text = Reader.read(file, fileType);
                break;
            case TEXT:
                text = Reader.read(file, fileType);
                break;
            case FOLDER:
                break;
            case ELSE:
                text = Reader.read(file, fileType);
                break;
        }

    }


    private void initializeNodes(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!probabilities.containsKey(s.charAt(i)))
                probabilities.put(s.charAt(i), 1.0);
            else
                probabilities.replace(s.charAt(i), 1.0 + probabilities.get(s.charAt(i)));
        }

        probabilities.forEach((key, value) -> {
            probabilities.replace(key, probabilities.get(key) / s.length());
            nodes.add(new Node(key, value));
        });

        Collections.sort(nodes);
        Collections.reverse(nodes);

    }


    private void split(int L, int R) {

        if (R - L == 1) {
            return;
        }
        if (R <= L) return;
        double S = 0;
        double T = 0;
        double half = 0;
        for (int i = L; i < R; i++) {
            half += nodes.get(i).getProbability();
        }
        half /= 2;
        int index = 0;

        for (int i = L; i < R; i++) {
            T = S;
            S += nodes.get(i).getProbability();
            if (S >= half) {
                if (half - T < S - half) {
                    index = i;
                } else {
                    index = i + 1;
                }
                break;
            }
        }


        for (int i = L; i < index; i++) {
            nodes.get(i).appendCode('0');
        }
        for (int i = index; i < R; i++) {
            nodes.get(i).appendCode('1');
        }
        split(L, index);
        split(index, R);
    }

    int count = 0;

    private void createTable() {
        for (Node node : nodes) {
            table.put((Character) node.getSymbol(), node.getCode());
        }
    }

    private void createBitsAsString() throws FileNotFoundException {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            stringBuilder.append(table.get(text.charAt(i)));
        }
        zipped = stringBuilder.toString();
    }

    private void createDataToStore(String bitsAsString) {
        //split the stream to sun strings each on contains 7 characters
        byte leftBits = 0;
        int nbits = 8;
        //size : number of sub strings
        int size = (bitsAsString.length() / nbits);
        size += (bitsAsString.length() % nbits) != 0 ? 1 : 0;
        byte bytes[] = new byte[size];
        int j = 0;
        for (int i = 0; i < bitsAsString.length(); i = i, j++) {
            String sub;
            if (i + nbits <= bitsAsString.length()) {
                sub = bitsAsString.substring(i, i + nbits);
                i += nbits;
            } else {
                sub = bitsAsString.substring(i, bitsAsString.length());
                leftBits = (byte) (nbits - bitsAsString.length() + i);
                i = bitsAsString.length();
            }
            bytes[j] = getByte(sub);
        }
        sfData.data = bytes;
        sfData.leftBits = leftBits;
        sfData.filename = source;
        sfData.table = table;
        sfData.fileType = this.fileType;

    }

    // returns the decimal value of nbits binary characters
    private byte getByte(String s) {
;
        int i = Integer.parseInt(s, 2);
        return (byte) i;
    }

    public static boolean AutoZip(String path, String dest) {
        try {
            SFData sfData = SerializedZip(path);
            SFData.Write(dest + ".sf", sfData);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static SFData SerializedZip(String path) throws IOException {

        File file = new File(path);
        if (file.isDirectory()) {
            SFData sfData = new SFData();
            sfData.fileType = FileType.FOLDER;
            sfData.filename = path;
            sfData.children = new ArrayList<>();
            String childrenNames[] = file.list();
            for (int i = 0; i < childrenNames.length; i++) {
                sfData.children.add(SerializedZip(path + "\\" + childrenNames[i]));
            }
            return sfData;
        } else if (IsImage(path)) {
            ShannonFanoZipper shannonFanoZipper = new ShannonFanoZipper(path, null, FileType.IMAGE);
            return shannonFanoZipper.makeSFData();
        } else if (IsText(path)) {
            ShannonFanoZipper shannonFanoZipper = new ShannonFanoZipper(path, null, FileType.TEXT);
            return shannonFanoZipper.makeSFData();
        }
        ShannonFanoZipper shannonFanoZipper = new ShannonFanoZipper(path, null, FileType.ELSE);
        return shannonFanoZipper.makeSFData();

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

}
