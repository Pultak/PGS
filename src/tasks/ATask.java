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
    }


    public void writeToAllStateFiles(String line){
        AFileSegment actualSegment = parentSegment;

        while(true){
            FileOutput.writeToStateFile(actualSegment.getActualDirectory() + Const.STATE_FILE_NAME, line);
            actualSegment = actualSegment.parentSegment;
            if(actualSegment != null){
                // TODO: 07.04.20 change here for better performance
                line = actualSegment.getClass().getSimpleName() + " " + actualSegment.id + " - ";
            }else{
                break;
            }
        }

        FileOutput.writeToStateFile(FileOutput.outputRootDirectory + Const.STATE_FILE_NAME, line);
    }

    public void writeWordStatisticsToFile(){
        if(parentSegment == null){
            //FileOutput.writeWordCountToFile(FileOutput.outputRootDirectory + FileInput.inputFileName, );
            System.out.println("NOT HAPPENING???");
        }else{
            String segmentName = parentSegment.getClass().getSimpleName() + parentSegment.id;
            FileOutput.writeWordCountToFile(parentSegment.getActualDirectory() + segmentName +"/" + segmentName
                     + Const.FILE_SUFFIX, parentSegment.wordMap);
        }
    }

}
