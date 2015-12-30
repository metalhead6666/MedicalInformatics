function isVT = vtDetection(ECG, R, seconds)
    % ArModel = ar(ECG, 4);
    % A = ArModel.A;
    % rootsAr = roots(A);

    % zplane(rootsAr);
    
    [w, ~] = pwelch(ECG);

    temp = w(1 : length(w)/8);
    plot(temp, 'r');
    %plot(locs, w(locs), 'ro');
    
    heart_beats = length(R)/seconds;
    
    if heart_beats > 1
        isVT = 1;
    else
        isVT = 0;
    end  
end