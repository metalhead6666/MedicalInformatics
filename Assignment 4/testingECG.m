clc
clear all

warning('off','all')
warning

%ecg = load('ecgnoise.dat');
%load 'DATARR//DARR_003.mat'
load 'DATPVC//DPVC_116.mat'
ecg = DAT.ecg;

N = length(ecg);
fs = 125;

period = int32(N/fs);    
seconds = 10;
position = int32(fs*seconds);    
maximum = period/seconds;

figure(1);
plot(ecg);
title('Display ECG loaded');

for window = 0 : maximum - 1
    ecgWindow = ecg(window*position+1 : (window+1)*position);
    nWindow = length(ecgWindow);
    R = mh_rpeakdetect(ecgWindow, fs);
    
    %figure(2);
    %plot(1:nWindow, ecgWindow, 'g', R, ecgWindow(R), 'ro');
    %title('Display a part of the ECG');
    
    %figure(3);
    %pvcDetection(ecgWindow, R, fs);
    
    %figure(4);
    %noiseDetect(ecgWindow);
    
    figure(1);  
    vtDetection(ecgWindow, fs);
    pause;
end

% plot(1:N, ecg, 'g', R, ecg(R), 'ro');

% value = noiseDetect(ecg);
