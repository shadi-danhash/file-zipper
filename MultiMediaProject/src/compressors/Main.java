package compressors;


import java.io.*;


public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        //File file = new File("C:\\Users\\ASUS\\Documents\\NetBeansProjects\\multimedia\\ww.txt");
        LzwZipper.AutoZip("C:\\Users\\ASUS\\Documents\\NetBeansProjects\\multimedia\\zip", "C:\\Users\\ASUS\\Documents\\NetBeansProjects\\multimedia\\zip");
        
        LzwUnzipper.AutoUnzip("C:\\Users\\ASUS\\Documents\\NetBeansProjects\\multimedia\\zip.lzw", "C:\\Users\\ASUS\\Documents\\NetBeansProjects\\multimedia");
        
        /*LzwZipper lz = new LzwZipper(file,"C:\\Users\\ASUS\\Documents\\NetBeansProjects\\multimedia\\w" , FileType.IMAGE);
        lz.zip();
        LzwUnzipper lu = new LzwUnzipper("C:\\Users\\ASUS\\Documents\\NetBeansProjects\\multimedia\\w", "C:\\Users\\ASUS\\Documents\\NetBeansProjects\\multimedia\\iconnn.txt");
        lu.unzip();*/
        
    }
}
