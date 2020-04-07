package IO;

import core.AFileSegment;
import core.Volume;
import utils.Const;
import utils.MutableInteger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class FileOutput {


    private static Map<String, Semaphore> stateFileSemaphores = new HashMap<>();

    public static String outputRootDirectory;
    public static String inputFileName;

    public static void initOutputStructure(String inputFileName){

        File inputFile = new File(inputFileName);
        String structureFileName = inputFile.getName().replace(".txt", "");
        File structureMainFolder = new File(structureFileName);
        if(structureMainFolder.exists()){
            try{
                deleteDirectoryRecursion(structureMainFolder.toPath());
            }catch(IOException e){
                System.err.println("FAILED TO DELETE LAST OUTPUT STRUCTURE!");
                System.err.println(e.toString());
                System.exit(1);
            }
        }
        if(!structureMainFolder.mkdir()){
            System.err.println("FAILED TO INIT OUTPUT STRUCTURE!");
            System.exit(1);
        }

        outputRootDirectory = structureFileName+"/";
    }

    private static void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);
    }


    public static void writeToStateFile(String pathToFile, String line){
        Semaphore fileSemaphore = popFromStateFileSemaphores(pathToFile);
        try {
            Files.write(Paths.get(pathToFile), line.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        fileSemaphore.release();
    }

    private static synchronized Semaphore popFromStateFileSemaphores(String key){
        Semaphore fileSemaphore = stateFileSemaphores.get(key);
        if(fileSemaphore == null){
            fileSemaphore = new Semaphore(0);
            stateFileSemaphores.put(key, fileSemaphore);
            File newFile = new File(key);
            try {
                newFile.createNewFile();
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }else{
            try {
                fileSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return fileSemaphore;
    }


    public static void writeWordCountToFile(String pathToFile, HashMap<String, MutableInteger> wordMap){
        File newFile = new File(pathToFile);
        try {
            if(newFile.createNewFile()){
                BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
                for(Iterator keyIterator = wordMap.keySet().iterator(), valueIterator = wordMap.values().iterator(); keyIterator.hasNext();){
                    bw.write(keyIterator.next()+" "+valueIterator.next()+"\n");
                }
                bw.close();
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


}
