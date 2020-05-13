package tasks;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import segments.Paragraph;

import java.util.List;
public class ForemanTask extends ATask{

    public ForemanTask(int taskID) {
        super(taskID);
    }

    @Override
    public void run() {
        waitForFirstAssignment();
        while(TaskManager.threadsNeeded) {
            if(parentSegment != null) {
                List<Paragraph> list = (List<Paragraph>) parentSegment.children;
                for (int i = 0; i < list.size(); i++) {
                    Paragraph paragraph = list.get(i);
                    //is paragraph free?
                    if (paragraph.setAssigned()) {
                        paragraph.initSubSegments();
                        int segmentSize = paragraph.children.size();
                        while (localSemaphore.availablePermits() != segmentSize) {

                            ATask worker = TaskManager.TASK_MANAGER.getThread(Task.SlaveTask);
                            if (worker != null) {
                                worker.setTaskAssigned(paragraph, localSemaphore);
                            }
                        }
                        try {
                            localSemaphore.acquire(segmentSize);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        parentSemaphore.release();
                    }
                }
            }
            //System.out.println("FOREMAN THREAD DONE ("+parentSegment+")");
            freeTask(Task.ForemanTask);
        }
    }
}
