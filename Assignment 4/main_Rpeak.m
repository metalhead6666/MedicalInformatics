clear all
warning off
%....................
clc
disp(' ')
disp('==========================================================================')
disp('------------------------------------------------------------ ')
disp(' ')
disp(' ')


close all
clf
display=0;    %.. visualizar resultados itnermedios

%------------------------------- load ECG
fs=1000;      
ecg=load('a.dat');
ecg=ecg(:,3);
N=length(ecg);
t=0:(1/fs):N/fs-(1/fs);


%--------------------bbb               ----------- maximizar janela
fig=gcf;
units=get(fig,'units');
set(fig,'units','normalized','outerposition',[0 0 1 1]);
set(fig,'units',units);


%==========================================================================
% INICIO 
%==========================================================================0
%==========================================================================0
%------------------------------------------------------------ Normalizacao

%ииииииииииииииииииииииииииииииииииииииии [min,max]  [-1..1]
mecg=mean(ecg);
e0 = ecg-mecg;
e0 = e0/max(e0);

%ииииииииииииииииииииииииииииииииииииииии filtro
%e0=ecg;

%==========================================================================1
%------------------------------------------------------------ LowPass Filter
ordem=4;
wc=25;
fc=wc/(0.5*fs);
[pos,a]=butter(ordem,fc);
e1= filter(pos,a, e0);
if display
    figure(1)
    plot(t,e0,t,e1,'r','LineWidth',1.5)
    title(' LOW PASS FILTER ','FontSize',14)
    zoom on
    pause
end

%==========================================================================2
%---------------------------------------------------------- High Pass Filter
wc=5;
fc=wc/(0.5*fs);
[pos,a]=butter(ordem,fc,'high');
e2= filter(pos,a, e1);
if display
    figure(1)
    plot(t,e0,t,e1,'r:',t,e2,'g','LineWidth',1.5)
    title(' HIGH+LOW PASS FILTER ','FontSize',14)
    zoom on
    pause
end

%==========================================================================3
%------------------------------------------------------- Difference + Square
e3= diff(e2);
e3(end+1)=e2(end);

e4= 50*e3.^2;
if display
    figure(1)
    plot(t,ecg,t,mecg+e2,'g:',t,e3,'m:','LineWidth',1.5)
    title(' DIFFERENTIATION+SQUARE ','FontSize',14)
    zoom on
    %hold on
    pause
    figure(1)
    plot(t,e4,'r','LineWidth',1.5)
    title(' DIFFERENTIATION+SQUARE ','FontSize',14)
    zoom on
    pause
    hold off
end

%==========================================================================3
%------------------------------------------------------------ Moving Window
timew= 0.22;
Nw= fix(timew*fs);    % samplings (even)
pos = (1/Nw)*ones(1,Nw);
a = 1;
e5= 10*filter(pos,a, e4);
if display
    figure(1)
    plot(t,e0,t,e5,'r','LineWidth',1.5)
    title(' MOVING AVERAGE FILTER ','FontSize',14)
    zoom on
    pause
end
%==========================================================================3

l = 0.7 * mean(e5);
d = 0.3;
back = 5;

stopValue = 0;
temp = 0;

R = peakDetection(ecg, 125);
plot(t, ecg, 'g', R, ecg(R), 'ro');