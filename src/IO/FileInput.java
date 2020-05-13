package IO;

import org.json.JSONException;
import org.json.JSONObject;
import segments.SegmentScope;
import utils.Const;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class FileInput {

    private static RandomAccessFile inputFile;
    public static String inputFileName;
    public static long fileSize;

    private static final Semaphore inputFileSemaphore = new Semaphore(1);


    public static void initInput(String inputFileName){
        FileInput.inputFileName = inputFileName;
        try{
            File inputFile = new File(inputFileName);
            fileSize = inputFile.length();
            FileInput.inputFile = new RandomAccessFile(inputFile, "r");
        }catch(FileNotFoundException e){
            System.err.println("INPUT FILE NOT FOUND!!");
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    public static void closeAllInputs(){
        try{
            inputFile.close();
        }catch(IOException e){
            System.err.println("Cant close input file because: ");
            System.err.println(e.toString());
        }
    }


    /**
     * Method that splits file segment into smaller parts with desired name.
     * @param desiredSegment segment name
     * @param startLocation segment file start location
     * @param endLocation segment file end location
     * @return List of segments with desired name
     */
    public static List<SegmentScope> findFileSegments(String desiredSegment, long startLocation, long endLocation){
        try{
            inputFileSemaphore.acquire();

            List<SegmentScope> result = new ArrayList<>();
            String regex = desiredSegment+".+";
            long segmentStartPosition = startLocation;

            inputFile.seek(startLocation);
            String line = inputFile.readLine();

            //finding start location of first sub-segment
            while(inputFile.getFilePointer() < endLocation){
                if(line.matches(regex)){
                    segmentStartPosition = inputFile.getFilePointer();
                    break;
                }
                line = inputFile.readLine();
            }

            //finding other segments
            line = inputFile.readLine();
            while(inputFile.getFilePointer() < endLocation){
                if(line.matches(regex)){
                    long segmentEndPosition = inputFile.getFilePointer();
                    result.add(new SegmentScope(segmentStartPosition, segmentEndPosition));
                    segmentStartPosition = inputFile.getFilePointer();
                }
                line = inputFile.readLine();
            }
            result.add(new SegmentScope(segmentStartPosition, inputFile.getFilePointer()));

            inputFileSemaphore.release();
            return result;
        }catch(IOException | InterruptedException e){
            System.err.println(e.toString());
            System.exit(1);
            return null;
        }
    }


    /**
     * Method used explicitly for spliting of paragraphs
     * @param startLocation segment file start location
     * @param endLocation segment file end location
     * @return List of paragraph file segments
     */
    public static List<SegmentScope> findParagraphSegments(long startLocation, long endLocation){
        try{
            inputFileSemaphore.acquire();

            List<SegmentScope> result = new ArrayList<>();
            long segmentStartPosition = startLocation;
            boolean insideParagraph = false;

            inputFile.seek(startLocation);
            String line = inputFile.readLine();

            while(inputFile.getFilePointer() < endLocation){
                if(line.length() != 0){
                    insideParagraph = true;
                }else{
                    if(insideParagraph){
                        result.add(new SegmentScope(segmentStartPosition, inputFile.getFilePointer()));
                        insideParagraph = false;
                    }
                    segmentStartPosition = inputFile.getFilePointer();
                }
                line = inputFile.readLine();
            }

            //is the end position also end of paragraph?
            if(insideParagraph){
                result.add(new SegmentScope(segmentStartPosition, endLocation));
            }

            inputFileSemaphore.release();
            return result;
        }catch(IOException | InterruptedException e){
            System.err.println(e.toString());
            System.exit(1);
            return null;
        }
    }

    /**
     * Method reads all lines in passed file scope
     * @param startLocation paragraphs file start location
     * @param endLocation paragraphs file start location
     * @return Every line in passed paragraph
     */
    public static List<String> readParagraph(long startLocation, long endLocation){
        try{
            inputFileSemaphore.acquire();
            List<String> result = new ArrayList<>();

            inputFile.seek(startLocation);
            String line = inputFile.readLine();
            while(inputFile.getFilePointer() < endLocation){
                result.add(line);
                line = inputFile.readLine();
            }
            if(!line.equals("")){
                result.add(line);
            }

            inputFileSemaphore.release();
            return result;
        }catch(IOException | InterruptedException e){
            System.err.println(e.toString());
            System.exit(1);
            return null;
        }
    }

    public static void loadConfigurationFile(){
        try {
            JSONObject root = new JSONObject(new String(Files.readAllBytes(Paths.get("configuration.json"))));
            //System.out.println(root.toString());
            Const.COUNT_OF_BOSS_THREADS = root.getInt(Const.JSON_BOSS_COUNT);
            Const.COUNT_OF_UNDER_BOSS_THREADS = root.getInt(Const.JSON_UNDER_BOSS_COUNT);
            Const.COUNT_OF_MASTER_THREADS = root.getInt(Const.JSON_MASTER_COUNT);
            Const.COUNT_OF_FOREMAN_THREADS = root.getInt(Const.JSON_FOREMAN_COUNT);
            Const.COUNT_OF_SLAVE_THREADS = root.getInt(Const.JSON_SLAVE_COUNT);
        }catch(IOException | JSONException e){
            System.err.println("Configuration parsing failed!");
            System.err.println(e.toString());
            System.err.println("Using default values!");
        }
    }
}
