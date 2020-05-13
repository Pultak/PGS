package tasks;


import IO.FileOutput;
import segments.AFileSegment;
import utils.Const;
import utils.Functions;

import java.util.concurrent.Semaphore;

public abstract class ATask implements Runnable {

    public int taskID;

    public AFileSegment parentSegment;
    public Semaphore parentSemaphore;

    protected final Semaphore localSemaphore;

    public boolean isTaskFree = true;

    private int currentlyWorkingThreads = 0;

    public static int assignCount = 0;
    public static int freedCount = 0;

    public ATask(int taskID){
        this.localSemaphore = new Semaphore(0);
        this.taskID = taskID;
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


    public void setTaskAssigned(AFileSegment parentSegment, Semaphore parentSemaphore){
        this.parentSegment = parentSegment;
        this.parentSemaphore = parentSemaphore;
        isTaskFree = false;
        localSemaphore.release();
        ++currentlyWorkingThreads;
        System.out.println(this.getClass().getSimpleName() + taskID + " assigned to "+parentSegment+"! from " + Thread.currentThread().getId());
        ++assignCount;
    }

    public void freeTask(Task taskType){
        this.parentSemaphore = null;
        this.parentSegment = null;
        this.isTaskFree = true;
        System.out.println(this.getClass().getSimpleName() + taskID + " freed!");
        TaskManager.TASK_MANAGER.allTasks.get(taskType).getKey().release();
        ++freedCount;
        --currentlyWorkingThreads;
        try {
            localSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected void prepareFieldAndAcquireWorkers(AFileSegment segment, Task childTaskType){
        segment.initSubSegments();
        int segmentSize = segment.children.size();
        FileOutput.createOutputFiles(segment);

        while(localSemaphore.availablePermits() != segmentSize){
            ATask worker = TaskManager.TASK_MANAGER.getThread(childTaskType);
            if(worker != null){
                worker.setTaskAssigned(segment, localSemaphore);
            }
        }
        try {
            localSemaphore.acquire(segmentSize);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Functions.sumUpEveryWord(segment.wordMap, segment.children);
        writeWordStatisticsToFile(segment);
        writeToAllStateFiles(segment);
    }

    protected void waitForFirstAssignment(){
        try {
            localSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println(this.getClass().getSimpleName() + taskID + " first assign!");
    }
}
