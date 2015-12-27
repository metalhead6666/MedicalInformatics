ecg = load('ecgPVC.dat');

N = length(ecg);
fs = 250;

R = mh_rpeakdetect(ecg, fs);
plot(1:N, ecg, 'g', R, ecg(R), 'ro');

value = noiseDetect(ecg);