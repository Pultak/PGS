package utils;

import IO.FileInput;
import IO.FileOutput;
import segments.*;
import tasks.ATask;
import tasks.BossTask;
import tasks.Task;
import tasks.TaskManager;

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

        FileInput.loadConfigurationFile();
        initEssentials(args[0]);

        Semaphore mainSemaphore = new Semaphore(0);
        TaskManager.TASK_MANAGER.allTasks.get(Task.BossTask).getValue().forEach( task -> {
            task.setTaskAssigned(null, mainSemaphore);
        });
        try {
            mainSemaphore.acquire(Const.COUNT_OF_BOSS_THREADS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        HashMap<String, MutableInteger> result = new HashMap<>();
        Functions.sumUpEveryWord(result, volumes);
        FileOutput.writeWordCountToFile(FileOutput.outputRootDirectory + FileInput.inputFileName, result);
        FileOutput.writeToStateFile(FileOutput.outputRootDirectory + Const.STATE_FILE_NAME, "File - OK");

        FileInput.closeAllInputs();
        TaskManager.TASK_MANAGER.killAllThreads();
        System.out.println(ATask.assignCount+"/"+ATask.freedCount);
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
