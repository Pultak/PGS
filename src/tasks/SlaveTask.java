package tasks;

import segments.Paragraph;
import utils.MutableInteger;

import java.util.List;

public class SlaveTask extends ATask {


    public SlaveTask(int taskID) {
        super(taskID);
    }

    @Override
    public void run() {
        waitForFirstAssignment();
        while(TaskManager.threadsNeeded) {
            if(parentSegment != null) {
                Paragraph parentSegment = (Paragraph) this.parentSegment;
                int id = 0;
                for (String line : (List<String>) parentSegment.children) {
                    if (parentSegment.assignLineToThread(id)) {
                        String[] words = line.toLowerCase().trim().split("[^a-z]+");

                        for (String word : words) {
                            MutableInteger count = parentSegment.wordMap.get(word);
                            if (count == null) {
                                parentSegment.wordMap.put(word, new MutableInteger());
                            } else {
                                count.increment();
                            }
                        }
                        parentSemaphore.release();
                        //System.out.println("Lajna: "+id+" on "+Thread.currentThread().getId()+" status: "+parentSemaphore.availablePermits());
                    }
                    id++;
                }
            }
            freeTask(Task.SlaveTask);
        }
    }




}
