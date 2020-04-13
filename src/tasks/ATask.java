package tasks;


import IO.FileInput;
import IO.FileOutput;
import core.AFileSegment;
import utils.Const;

import java.util.concurrent.Semaphore;

public abstract class ATask implements Runnable {

    protected final AFileSegment parentSegment;
    protected final Semaphore localSemaphore;
    protected final Semaphore parentSemaphore;

    public ATask(AFileSegment parentSegment, int countOfSubThreads, Semaphore parentSemaphore){
        this.parentSegment = parentSegment;
        this.localSemaphore = new Semaphore(countOfSubThreads * -1 + 1);
        this.parentSemaphore = parentSemaphore;
        System.out.println(this.getClass().getSimpleName()+" started!");
    }


    public void writeToAllStateFiles(AFileSegment actualNode){
        AFileSegment actualSegment = actualNode;
        String line = "OK\n";
        while(actualSegment != null){
            line = actualSegment.getClass().getSimpleName() + " " + actualSegment.id + " - " + line;
            FileOutput.writeToStateFile(actualSegment.getActualDirectory() + Const.STATE_FILE_NAME, line);
            actualSegment = actualSegment.parentSegment;
        }
        FileOutput.writeToStateFile(FileOutput.outputRootDirectory + Const.STATE_FILE_NAME, line);

    }

    public void writeWordStatisticsToFile(AFileSegment actualNode){
        String segmentName = actualNode.getClass().getSimpleName().toLowerCase() + actualNode.id;
        FileOutput.writeWordCountToFile(actualNode.getActualDirectory() + segmentName
                 + Const.FILE_SUFFIX, actualNode.wordMap);
    }


}
