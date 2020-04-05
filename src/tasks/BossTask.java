package tasks;

import core.Volume;
import utils.Const;
import utils.Main;

import java.util.ArrayList;
import java.util.List;


public class BossTask extends ATask<Main> {

    public BossTask(){
        super(null);
    }

    @Override
    public void run() {
        for(Volume volume : Main.volumes){
            //is volume free?
            if(volume.setAssigned()){
                List<Thread> underBossThreads = new ArrayList<>();
                for(int i = 0; i < Const.COUNT_OF_UNDER_BOSS_THREADS; i++){
                    Thread thread = new Thread(new UnderBossTask(volume));
                    underBossThreads.add(thread);
                    thread.start();
                }
                for(Thread thread : underBossThreads){
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                System.out.println("BOSS THREAD DONE");
                //todo file writing
            }
        }

    }
}
