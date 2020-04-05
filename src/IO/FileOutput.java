package IO;

import utils.Const;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class FileOutput {


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

        File mainStateFile = new File(structureFileName+"/"+ Const.STATE_FILE_NAME);
        File mainResultFile = new File(structureFileName+"/"+structureFileName+".txt");
        try{
            mainStateFile.createNewFile();
            mainResultFile.createNewFile();
        }catch(IOException e){
            System.err.println("FAILED TO INIT MAIN STATE FILE!");
            System.exit(1);
        }
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






}
