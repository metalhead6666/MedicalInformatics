clc
clear all

warning('off','all')
warning

%load 'DATARR//DARR_003.mat'
load 'DATPVC//DPVC_228.mat'
ecg = DAT.ecg;
%ecg = load('ecgnoise.dat');

N = length(ecg);
fs = 125;

period = int32(N/fs);    
seconds = 25;
position = int32(fs*seconds);    
maximum = period/seconds;

figure(1);
plot(ecg);
title('Display ECG loaded');

if maximum == 0
    R = mh_rpeakdetect(ecg, fs);
    nWindow = length(ecg);
    
    figure(2);
    plot(1:nWindow, ecg, 'g', R, ecg(R), 'ro');
    title('Display a part of the ECG');

    figure(3);
    pvcDetection(ecg, R, fs);

    figure(4);
    noiseDetect(ecg);

    figure(5);  
    vtDetection(ecg);
else
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

        figure(5);  
        vtDetection(ecgWindow, R);
        pause;
        clc
    end
end

% plot(1:N, ecg, 'g', R, ecg(R), 'ro');
