function [ finalNum ] = noiseDetect(ECG)
%NOISEDETECT Summary of this function goes here
%   Simple function that receives ECG data and check if it has noise.
%   To give some extra information, it will generate gaussian noise to that
%   ECG and recalculate everything again.
%   The second argument is the ECG name (PVC, VT, Noise, Normal, etc).
%   Returns the value of the % of correct values.

n = length(ECG);

%%%%%%%%%%%% ECG to evaluate %%%%%%%%%%%%
subplot(2, 2, 1)
plot(ECG)
title('Normal ECG - Graph');
ecgDiff = diff(ECG);

subplot(2, 2, 2)
hist(ecgDiff, 5);
title('Normal - Histogram');

h = hist(ecgDiff, 5);
finalNum = (h(3)/n) * 100;
strToGive = num2str((h(3)/n) * 100);
xlabel(strToGive);

%%%%%%%%%%%% Gaussian noise to check the differences %%%%%%%%%%%%
ecgToTest = awgn(ECG,10,'measured');
subplot(2, 2, 3)
plot(ecgToTest);
title('Noise ECG (Gaussian Noise) - Graph');

ecgDiff = diff(ecgToTest);
subplot(2, 2, 4)
hist(ecgDiff, 5);
title('Noise ECG (Gaussian Noise) - Histogram');

h = hist(ecgDiff, 5);
strToCompare = num2str((h(3)/n) * 100);
xlabel(strToCompare)
end

