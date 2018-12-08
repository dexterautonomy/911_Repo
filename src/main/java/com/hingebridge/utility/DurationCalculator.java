package com.hingebridge.utility;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class DurationCalculator
{    
    protected String dateSuffix(String sdf)//this method removes the st, nd, rd, and th from SimpleDateFormat so you can manipulate it into a proper date object
    {
        String date_string = null;
        
        if(sdf.contains("st"))
        {
            date_string = sdf.replace("st", "");
        }
        else if(sdf.contains("nd"))
        {
            date_string = sdf.replace("nd", "");
        }
        else if(sdf.contains("rd"))
        {
            date_string = sdf.replace("rd", "");
        }
        else if(sdf.contains("th"))
        {
            date_string = sdf.replace("th", "");
        }
        return date_string;
    }
    
    public String calculateDuration(String date)
    {
        String duree = null;
        String postDate = dateSuffix(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy,  hh:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime ldt = LocalDateTime.parse(postDate, formatter);
        
        Duration duration = Duration.between(ldt, now);
        long minutes = duration.toMinutes();
        
        if(minutes < 1)
        {
            duree = "Just now";
        }
        else if(minutes < 2)
        {
            duree = "1 min ago";
        }
        else if(minutes < 3)
        {
            duree = "2 mins ago";
        }
        else if(minutes < 4)
        {
            duree = "3 mins ago";
        }
        else if(minutes < 5)
        {
            duree = "4 mins ago";
        }
        else if(minutes < 6)
        {
            duree = "5 mins ago";
        }
        else if(minutes < 7)
        {
            duree = "6 mins ago";
        }
        else if(minutes < 8)
        {
            duree = "7 mins ago";
        }
        else if(minutes < 9)
        {
            duree = "8 mins ago";
        }
        else if(minutes < 10)
        {
            duree = "9 mins ago";
        }
        else if(minutes < 11)
        {
            duree = "3 mins ago";
        }
        else if(minutes < 20)
        {
            duree = "10+ mins ago";
        }
        else if(minutes < 30)
        {
            duree = "20+ mins ago";
        }
        else if(minutes < 40)
        {
            duree = "30+ mins ago";
        }
        else if(minutes < 50)
        {
            duree = "40+ mins ago";
        }
        else if(minutes < 60)
        {
            duree = "50+ mins ago";
        }
        else if(minutes < 120)
        {
            duree = "1+ hrs ago";
        }
        else if(minutes < 180)
        {
            duree = "2+ hrs ago";
        }
        else if(minutes < 240)
        {
            duree = "3+ hrs ago";
        }
        else if(minutes < 300)
        {
            duree = "4+ hrs ago";
        }
        else if(minutes < 360)
        {
            duree = "5+ hrs ago";
        }
        
        
        return duree;
    }
}