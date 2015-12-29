clc
clear all

warning('off','all')
warning

ecg = load('ecgST.dat');

N = length(ecg);
fs = 250;

period = int32(N/fs);    
seconds = 5;
position = int32(fs*seconds);    
maximum = period/seconds;

figure(1);
plot(ecg);
title('Display ECG loaded');

for window = 0 : maximum - 1
    ecgWindow = ecg(window*position+1 : (window+1)*position);
    nWindow = length(ecgWindow);
    R = mh_rpeakdetect(ecgWindow, fs);
    
    figure(2);
    plot(1:nWindow, ecgWindow, 'g', R, ecgWindow(R), 'ro');
    title('Display a part of the ECG');
    
    figure(3);
    pvcDetection(ecgWindow, R, fs);
    
    figure(4);
    noiseDetect(ecgWindow);
    pause;
end

% plot(1:N, ecg, 'g', R, ecg(R), 'ro');

% value = noiseDetect(ecg);
