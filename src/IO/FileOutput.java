package IO;

import segments.AFileSegment;
import utils.Const;
import utils.MutableInteger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class FileOutput {


    /**
     * Map of semaphores for every possible state file
     */
    private static Map<String, Semaphore> stateFileSemaphores = new HashMap<>();
    public static String outputRootDirectory;


    /**
     * Method that deletes old file records
     * @param inputFileName name of the root directory
     */
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

    /**
     * Method to init/use semaphore for state file management
     * @param key path to state file used as key
     * @return new/already used semaphore
     */
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


    /**
     * Method used to write down word statistics into file
     * @param pathToFile path to desired file
     * @param wordMap map of word count
     */
    public static void writeWordCountToFile(String pathToFile, HashMap<String, MutableInteger> wordMap){
        File newFile = new File(pathToFile);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
            //sorting the map
            List<Map.Entry<String, MutableInteger>> sortedResult = wordMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());
            if(sortedResult.get(0).getKey().equals("")){
                sortedResult.remove(0);
            }
            for(Map.Entry<String, MutableInteger> entry : sortedResult){
                bw.write(entry.getKey()+" "+entry.getValue().get()+"\n");
            }
            bw.close();

        } catch (IOException | SecurityException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Method to makes sure that actual file sub-segment has its own directory for file writing
     * @param segment actual segment
     */
    public static void createOutputFiles(AFileSegment segment){
        String basicPath = segment.getActualDirectory();
        File directory = new File(basicPath);
        File newStateFile = new File(basicPath + Const.STATE_FILE_NAME);
        File newWordCountFile = new File(basicPath + segment.getClass().getSimpleName().toLowerCase()
                + segment.id + Const.FILE_SUFFIX);
        try {
            directory.mkdir();
            newStateFile.createNewFile();
            newWordCountFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

}
