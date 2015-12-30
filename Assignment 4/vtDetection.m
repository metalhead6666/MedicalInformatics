function vtDetection(ECG, fs)
    ArModel = ar(ECG, 4);
    A = ArModel.A;
    rootsAr = roots(A);

    zplane(rootsAr);
    
    %plot(locs, w(locs), 'ro');
end