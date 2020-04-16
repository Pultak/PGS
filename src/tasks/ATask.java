package tasks;


import IO.FileOutput;
import segments.AFileSegment;
import utils.Const;

import java.util.concurrent.Semaphore;

public abstract class ATask implements Runnable {

    protected final AFileSegment parentSegment;
    protected final Semaphore localSemaphore;
    protected final Semaphore parentSemaphore;

    public ATask(AFileSegment parentSegment, int countOfSubThreads, Semaphore parentSemaphore){
        this.parentSegment = parentSegment;
        this.localSemaphore = new Semaphore(0);
        this.parentSemaphore = parentSemaphore;
        //System.out.println(this.getClass().getSimpleName()+" started! works for: "+parentSegment);
    }


    /**
     * Method that writes to actual directory state file and into every parent directory located inside file system
     * @param actualNode actual segment
     */
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

    /**
     * Writes word statistics under actual segment directory
     * @param actualNode actual segment
     */
    public void writeWordStatisticsToFile(AFileSegment actualNode){
        String segmentName = actualNode.getClass().getSimpleName().toLowerCase() + actualNode.id;
        FileOutput.writeWordCountToFile(actualNode.getActualDirectory() + segmentName
                 + Const.FILE_SUFFIX, actualNode.wordMap);
    }


}
