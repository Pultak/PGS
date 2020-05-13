package tasks;

import segments.Book;

import java.util.List;

public class UnderBossTask extends ATask {

    public UnderBossTask(int taskID) {
        super(taskID);
    }

    @Override
    public void run() {
        waitForFirstAssignment();
        while(TaskManager.threadsNeeded) {

            if(parentSegment != null) {
                for (Book book : (List<Book>) parentSegment.children) {
                    //is book free?
                    if (book.setAssigned()) {
                        prepareFieldAndAcquireWorkers(book, Task.MasterTask);
                        parentSemaphore.release();
                    }
                }
            }
            //System.out.println("UNDER BOSS THREAD DONE! ("+parentSegment+")");
            freeTask(Task.UnderBossTask);
        }
    }
}