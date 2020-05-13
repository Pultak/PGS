package tasks;

import segments.Volume;
import utils.Main;


public class BossTask extends ATask {

    public BossTask(int taskID) {
        super(taskID);
    }

    @Override
    public void run() {
        waitForFirstAssignment();
        while(TaskManager.threadsNeeded) {

            for (Volume volume : Main.volumes) {
                //is volume free?
                if (volume.setAssigned()) {
                    prepareFieldAndAcquireWorkers(volume, Task.UnderBossTask);
                }
            }
            //System.out.println("BOSS THREAD DONE!");
            parentSemaphore.release();
            freeTask(Task.BossTask);
        }
    }
}
