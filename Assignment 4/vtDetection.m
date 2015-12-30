function isVT = vtDetection(R, seconds)
    heart_beats = length(R) / seconds;
    
    if heart_beats > 1
        isVT = 1;
    else
        isVT = 0;
    end  
end