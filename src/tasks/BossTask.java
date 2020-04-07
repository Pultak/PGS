package tasks;

import IO.FileOutput;
import core.Volume;
import utils.Const;
import utils.Functions;
import utils.Main;

import java.util.concurrent.Semaphore;


public class BossTask extends ATask {

    public BossTask(Semaphore parentSemaphore){
        super(null, Const.COUNT_OF_UNDER_BOSS_THREADS, parentSemaphore);
    }

    @Override
    public void run() {
        for(Volume volume : Main.volumes){
            //is volume free?
            if(volume.setAssigned()){
                for(int i = 0; i < Const.COUNT_OF_UNDER_BOSS_THREADS; i++){
                    Thread thread = new Thread(new UnderBossTask(volume, localSemaphore));
                    thread.start();
                }

                try {
                    localSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                Functions.sumUpEveryWord(volume.wordMap, volume.children);
                writeWordStatisticsToFile();
                writeToAllStateFiles("Volume "+volume.id+" - OK");
            }
        }
        System.out.println("BOSS THREAD DONE");
        parentSemaphore.release();
    }
}
