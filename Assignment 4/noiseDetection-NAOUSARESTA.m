clear all
close all
clc

ecg = load('ecg1.dat');

subplot(2, 2, 1)
N = length(ecg);
fs = 250;
plot(ecg)
title('Normal ECG - Graph');

ecgDiff = diff(ecg);
subplot(2, 2, 2)
title('Normal ECG - Histogram');
h = hist(ecgDiff, 5);
valueOnZero = h(4);
str = num2str((valueOnZero/N) * 100);
xlabel(str);

ecg = awgn(ecg, 10, 'measured');
subplot(2, 2, 3)
plot(ecg);
title('Noise ECG (Gaussian Noise) - Graph');

ecgDiff = diff(ecg);
subplot(2, 2, 4)
hist(ecgDiff, 5);
title('Noise ECG (Gaussian Noise) - Histogram');
h = hist(ecgDiff, 5);
valueOnZero = h(4);
str = num2str((valueOnZero/N) * 100);
xlabel(str)