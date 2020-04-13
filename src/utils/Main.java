package utils;

import IO.FileInput;
import IO.FileOutput;
import core.AFileSegment;
import core.SegmentScope;
import core.Volume;
import tasks.BossTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {

    public static List<Volume> volumes;


    public static void main(String[] args){
        if(args.length < 1){
            System.err.println("NOT ENOUGH PARAMETERS!");
            System.err.println("Usage: <input-file-name>");
            System.exit(1);
        }

        initEssentials(args[0]);

        Semaphore mainSemaphore = new Semaphore(Const.COUNT_OF_BOSS_THREADS * -1 + 1);
        for(int i = 0; i < Const.COUNT_OF_BOSS_THREADS; i++){
            Thread thread = new Thread(new BossTask(mainSemaphore));
            thread.start();
        }
        try {
            mainSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        HashMap<String, MutableInteger> result = new HashMap<>();
        Functions.sumUpEveryWord(result, volumes);
        FileOutput.writeWordCountToFile(FileOutput.outputRootDirectory + FileInput.inputFileName, result);
        FileOutput.writeToStateFile(FileOutput.outputRootDirectory + Const.STATE_FILE_NAME, "File - OK");

        FileInput.closeAllInputs();
    }

    public static void initEssentials(String fileName){
        FileInput.initInput(fileName);
        FileOutput.initOutputStructure(fileName);
        initVolumeSegments();
    }

    public static void initVolumeSegments(){
        List<SegmentScope> segments = FileInput.findFileSegments(Const.VOLUME_BEGINNING, 0, FileInput.fileSize);
        volumes = new ArrayList<>();
        int i = 1;
        for(SegmentScope segment : segments){
            volumes.add(new Volume(segment.startLocation, segment.endLocation, i));
            ++i;
        }
    }


}
