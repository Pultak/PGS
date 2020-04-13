package IO;

import core.SegmentScope;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class FileInput {

    private static RandomAccessFile inputFile;

    public static String inputFileName;

    public static long fileSize;

    private static final Semaphore semaphore = new Semaphore(1);


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


    public static List<SegmentScope> findFileSegments(String desiredSegment, long startLocation, long endLocation){
        try{
            semaphore.acquire();

            String regex = desiredSegment+" .+[—|-].+";

            List<SegmentScope> result = new ArrayList<>();
            inputFile.seek(startLocation);

            long segmentStartPosition = startLocation;
            String line = inputFile.readLine();

            while(inputFile.getFilePointer() < endLocation){
                if(line.matches(regex)){
                    segmentStartPosition = inputFile.getFilePointer();
                    break;
                }
                line = inputFile.readLine();
            }

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

            semaphore.release();
            System.out.println("\""+regex+"\" and found: "+result.size());
            return result;
        }catch(IOException | InterruptedException e){
            System.err.println(e.toString());
            System.exit(1);
            return null;
        }
    }


    public static List<SegmentScope> findParagraphSegments(long startLocation, long endLocation){
        try{
            semaphore.acquire();

            List<SegmentScope> result = new ArrayList<>();
            inputFile.seek(startLocation);

            long segmentStartPosition = startLocation;

            boolean insideParagraph = false;
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

            semaphore.release();
            return result;
        }catch(IOException | InterruptedException e){
            System.err.println(e.toString());
            System.exit(1);
            return null;
        }
    }

    public static List<String> readParagraph(long startLocation, long endLocation){
        try{
            semaphore.acquire();

            List<String> result = new ArrayList<>();
            inputFile.seek(startLocation);

            String line = inputFile.readLine();
            while(inputFile.getFilePointer() < endLocation){
                result.add(line);

                line = inputFile.readLine();
            }
            result.add(line);

            semaphore.release();
            return result;
        }catch(IOException | InterruptedException e){
            System.err.println(e.toString());
            System.exit(1);
            return null;
        }



    }

}
