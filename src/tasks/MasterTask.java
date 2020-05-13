package tasks;

import segments.Chapter;

import java.util.List;

public class MasterTask extends ATask{


    public MasterTask(int taskID) {
        super(taskID);
    }

    @Override
    public void run() {
        waitForFirstAssignment();
        while(TaskManager.threadsNeeded) {

            if(parentSegment != null) {
                for (Chapter chapter : (List<Chapter>) parentSegment.children) {
                    //is chapter free?
                    if (chapter.setAssigned()) {
                        prepareFieldAndAcquireWorkers(chapter, Task.ForemanTask);
                        parentSemaphore.release();
                    }
                }
            }
            //System.out.println("MASTER THREAD DONE! ("+parentSegment+")");
            freeTask(Task.MasterTask);
        }
    }
}
