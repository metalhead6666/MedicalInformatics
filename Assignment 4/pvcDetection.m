ecg = load('ecg1.dat');

N = length(ecg);
fs = 250;
limit = 100;
pvcCount = 0;

figure(1)
plot(ecg);

R = mh_rpeakdetect(ecg, fs);
[w, vf] = pwelch(ecg, 150);
figure(2)
plot(w);
%plot(1:N, ecg, 'g', R, ecg(R), 'ro');

dR = diff(R);

ndR = length(dR);
figure(3)
plot(dR, 'go');

mu = mean(dR);
mu2 = mu + 0.10 * mu;
hline = refline([0 mu]);
hline.Color = 'r';

hline = refline([0 mu2]);
hline.Color = 'b';

for i = 2 : ndR
    temp = dR(i) - dR(i-1);
    if(temp > limit)
        pvcCount = pvcCount + 1;
    end
end

pvcCount