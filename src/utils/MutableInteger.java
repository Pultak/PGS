package utils;

public class MutableInteger{

    int value;

    public MutableInteger(){
        this.value = 1;
    }

    public MutableInteger(int count){
        this.value = count;
    }

    public void increment(){
        ++value;
    }

    public void addValue(int value){
        this.value += value;
    }

    public int get(){
        return value;
    }

}
