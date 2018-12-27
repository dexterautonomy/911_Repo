package com.hingebridge.utility;

import com.hingebridge.model.AdvertObject;
import com.hingebridge.repository.AdvertObjectRepo;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

@Component
public class AdvertAlgorithmClass
{
    @Autowired
    private AdvertObjectRepo aor;
    
    public void displayAdvert(ModelMap model)
    {
        int adSize = adAlgorithm().size();
        final int COMPLETE = 6;
        
        model.addAttribute("adObject", adAlgorithm());
        
        if(adSize < 3)
        {
            model.addAttribute("top", adSize);
        }
        else if(adSize > 3 && adSize < COMPLETE)
        {
            model.addAttribute("top", 3);
            model.addAttribute("bottom", adSize);
        }
        else
        {
            model.addAttribute("top", 3);
            model.addAttribute("bottom", COMPLETE);
        }
    }
    
    private List<AdvertObject> adAlgorithm()
    {
        final int START = 0;
        int end = 6;
        
        List<AdvertObject> activeAds = aor.getActiveAds();  //activeAds means, not paused and not expired
        List<AdvertObject> firstSixAds = new LinkedList<>();  //firstSixAds means activeAds that the user have credit above 0
        
        List<AdvertObject> worthyAds = new LinkedList<>();
        Optional<AdvertObject> adObject;
        
        Map<Long, String> adIdImageMap = new HashMap<>();
        
        if(!activeAds.isEmpty())
        {
            for(AdvertObject ao : activeAds)
            {
                if(ao.getUserAdvert().getAdscredit() > 0)
                {
                    firstSixAds.add(ao);
                }
                else
                {
                    terminateAd(ao.getId());
                }
            }
        
            if(!firstSixAds.isEmpty())
            {
                if(firstSixAds.size() < end)
                {
                    end = firstSixAds.size();
                }
        
                AdvertObject adObj;
                
                for(int count = START; count < end; count++)
                {
                    adObj = firstSixAds.get(count);
                    long adId = adObj.getId();
                    String adImage = adObj.getAdsImage();
                
                    switch(adObj.getPayOption())
                    {
                        case "CPM":
                        {
                            adIdImageMap.put(adId, adImage);
                            perView(adId, "CPM");
                        }
                        break;
                    
                        case "CPC":
                        {
                            adIdImageMap.put(adId, adImage);
                            perView(adId, "CPC");
                        }
                        break;
                    }
                }
                
                List<Long> adIdKeysList = new LinkedList<>(adIdImageMap.keySet());
                Collections.shuffle(adIdKeysList);
        
                for (long key : adIdKeysList)
                {
                    adObject = aor.findById(key);
                    worthyAds.add(adObject.get());
                }
            }
        }
        return worthyAds;
    }
    
    private void perView(long adId, String cpmCpc)
    {
        Optional<AdvertObject> ao = aor.findById(adId);
        long credit = ao.get().getUserAdvert().getAdscredit();
        long views = ao.get().getViews();
        
        switch (cpmCpc)
        {
            case "CPM":
            {
                credit = credit - ao.get().getUserAdvert().getCpm();
                
                if(credit < 0)
                {
                    credit = 0;
                }
                ao.get().setViews(++views);
                ao.get().getUserAdvert().setAdscredit(credit);
                aor.save(ao.get());
            }
            break;
            
            case "CPC":
            {
                ao.get().setViews(++views);
                aor.save(ao.get());
            }
            break;
        }
    }
    
    public String perClick(long adId)
    {
        Optional<AdvertObject> ao = aor.findById(adId);
        
        String targetUrl = ao.get().getLandingPage();
        long credit = ao.get().getUserAdvert().getAdscredit();
        long clicks = ao.get().getClicks();
                
        switch (ao.get().getPayOption())
        {
            case "CPM":
            {
                ao.get().setClicks(++clicks);
                aor.save(ao.get());
                break;
            }
                    
            case "CPC":
            {
                credit = credit - ao.get().getUserAdvert().getCpc();
                
                if(credit < 0)
                {
                    credit = 0;
                }
                ao.get().setClicks(++clicks);
                ao.get().getUserAdvert().setAdscredit(credit);
                aor.save(ao.get());
            }
        }
        return targetUrl;
    }
    
    private void terminateAd(long adId)
    {
        Optional<AdvertObject> ao = aor.findById(adId);
        
        ao.get().setApprove(0);
        ao.get().setPause(1);
        ao.get().setExpired(1);
        aor.save(ao.get());
    }
}