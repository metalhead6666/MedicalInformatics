function noise = noiseDetect(ECG)
    %NOISEDETECT
    %   Simple function that receives ECG data and check if it has noise.
    %   To give some extra information, it will generate gaussian noise to that
    %   ECG and recalculate everything again.
    %   The second argument is the ECG name (PVC, VT, Noise, Normal, etc).
    %   Returns the value of the % of correct values.

    numHist = 5;
    valGet = 3;
    noise = -1;

    %%%%%%%%%%%% ECG to evaluate %%%%%%%%%%%%
    subplot(2, 1, 1)
    plot(ECG)
    title('Normal ECG - Graph');
    ecgDiff = diff(ECG);

    subplot(2, 1, 2)
    h = hist(ecgDiff, numHist);

    hist(ecgDiff, numHist);
    title('Normal - Histogram');
    finalNum = (h(valGet)/ sum(h)) * 100;
    strToGive = num2str((h(valGet)/sum(h)) * 100);
    xlabel(strToGive);

    %%%%%%%%%%%% Gaussian noise to check the differences %%%%%%%%%%%%
    %ecgToTest = awgn(ECG,10,'measured');
    %subplot(2, 2, 3)
    %plot(ecgToTest);
    %title('Noise ECG (Gaussian Noise) - Graph');

    %ecgDiff = diff(ecgToTest);
    %subplot(2, 2, 4)
    %hist(ecgDiff, numHist);
    %title('Noise ECG (Gaussian Noise) - Histogram');

    %h = hist(ecgDiff, numHist);
    %strToCompare = num2str((h(valGet)/sum(h)) * 100);
    %xlabel(strToCompare)

    if finalNum > 80
        noise = 0;
    end
end

