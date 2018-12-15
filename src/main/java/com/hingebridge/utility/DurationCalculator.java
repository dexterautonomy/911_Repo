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
        long hours = minutes/60;
        long days = minutes/1440;
        long weeks = minutes/10080;
        long months = minutes/43200;
        long years = minutes/525600;
        
        if(minutes < 1)
        {
            duree = "Just now";
        }
        else if(hours < 1)
        {
            if(minutes > 1)
            {
                duree = minutes + " mins ago";
            }
            else
            {
                duree = "a minute ago";
            }
        }
        else if(days < 1)
        {
            if(hours > 1)
            {
                duree = hours + " hrs ago";
            }
            else
            {
                duree = "an hour ago";
            }
        }
        else if(weeks < 1)
        {
            if(days > 1)
            {
                duree = days + " days ago";
            }
            else
            {
                duree = "yesterday";
            }
        }
        else if(months < 1)
        {
            if(weeks > 1)
            {
                duree = weeks + " wks ago";
            }
            else
            {
                duree = "last week";
            }
        }
        else if(years < 1)
        {
            if(months > 1)
            {
                duree = months + " mths ago";
            }
            else
            {
                duree = "last month";
            }
        }
        else
        {
            if(years > 1)
            {
                duree = years + " yrs ago";
            }
            else
            {
                duree = "last year";
            }
        }
        
        return duree;
    }
}