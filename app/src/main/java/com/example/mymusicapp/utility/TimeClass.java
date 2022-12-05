package com.example.mymusicapp.utility;

public class TimeClass {
    public String getDurationInMinutes(int ms){
        long minutes = (ms/1000)/60;
        long seconds = (ms/1000)%60;
        String min=String.valueOf(minutes),sec=String.valueOf(seconds);
        if(minutes<10){
            min = "0"+minutes;
        }
        if(seconds<10){
            sec = "0"+seconds;
        }
        return min+":"+sec;
    }
}
