package IO;

import core.SegmentScope;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class FileInput {

    private static FileInputStream inputFileStream;
    private static BufferedReader inputReader;

    public static long fileSize;

    private static final Semaphore semaphore = new Semaphore(1);


    public static void initInput(String inputFileName){
        try{
            File inputFile = new File(inputFileName);
            fileSize = inputFile.length();
            inputFileStream = new FileInputStream(inputFile);
            inputReader = new BufferedReader(new InputStreamReader(inputFileStream, StandardCharsets.UTF_8));
        }catch(FileNotFoundException e){
            System.err.println("INPUT FILE NOT FOUND!!");
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    public static void closeAllInputs(){
        try{
            inputFileStream.close();
        }catch(IOException e){
            System.err.println("Cant close input file because: ");
            System.err.println(e.toString());
        }
    }


    public static List<SegmentScope> findFileSegments(String desiredSegment, long startLocation, long endLocation){
        try{
            semaphore.acquire();

            List<SegmentScope> result = new ArrayList<>();
            inputFileStream.getChannel().position(startLocation);

            long segmentStartPosition = startLocation;


            String line = inputReader.readLine();
            while(inputFileStream.getChannel().position() < endLocation){

                if(line.contains(desiredSegment)){
                    long segmentEndPosition = inputFileStream.getChannel().position();
                    result.add(new SegmentScope(segmentStartPosition, segmentEndPosition));
                    segmentStartPosition = inputFileStream.getChannel().position();
                }

                line = inputReader.readLine();
            }
            result.add(new SegmentScope(segmentStartPosition, inputFileStream.getChannel().position()));

            semaphore.release();
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
            inputFileStream.getChannel().position(startLocation);

            long segmentStartPosition = startLocation;

            boolean insideParagraph = false;
            String line = inputReader.readLine();
            while(inputFileStream.getChannel().position() < endLocation){

                if(line.length() != 0){
                    insideParagraph = true;
                }else{
                    if(insideParagraph){
                        result.add(new SegmentScope(segmentStartPosition, inputFileStream.getChannel().position()));
                        insideParagraph = false;
                    }
                    segmentStartPosition = inputFileStream.getChannel().position();
                }

                line = inputReader.readLine();
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
            inputFileStream.getChannel().position(startLocation);

            String line = inputReader.readLine();
            while(inputFileStream.getChannel().position() < endLocation){
                result.add(line);

                line = inputReader.readLine();
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
