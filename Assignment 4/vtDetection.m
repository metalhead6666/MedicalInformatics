function vtDetection(ECG, R)
    % ArModel = ar(ECG, 4);
    % A = ArModel.A;
    % rootsAr = roots(A);

    % zplane(rootsAr);
    
    [w, fs] = pwelch(ECG);

    temp = w(1 : length(w)/8);
    plot(temp, 'r');
    
    %plot(locs, w(locs), 'ro');
end