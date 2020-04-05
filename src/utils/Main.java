package utils;

import IO.FileInput;
import IO.FileOutput;
import core.SegmentScope;
import core.Volume;
import tasks.BossTask;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static List<Volume> volumes;

    public static void main(String[] args){
        if(args.length < 1){
            System.err.println("NOT ENOUGH PARAMETERS!");
            System.err.println("Usage: <input-file-name>");
            System.exit(1);
        }

        initEssentials(args[0]);

        List<Thread> bossThreads = new ArrayList<>();
        for(int i = 0; i < Const.COUNT_OF_BOSS_THREADS; i++){
            Thread thread = new Thread(new BossTask());
            bossThreads.add(thread);
            thread.start();
        }
        for(Thread bossThread : bossThreads){
            try{
                bossThread.join();
            }catch(InterruptedException e){
                System.err.println(e.toString());
            }
        }

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
        for(SegmentScope segment : segments){
            volumes.add(new Volume(segment.startLocation, segment.endLocation));
        }
    }


}
