package utils;

public class MutableInteger implements Comparable<MutableInteger>{

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

    @Override
    public int compareTo(MutableInteger o) {
        return Integer.compare(value, o.value);
    }
}
