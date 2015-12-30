clear all;clc

warning('off','all')

%load 'DATARR//DARR_003.mat'
%load 'DATPVC//DPVC_228.mat'
%ecg = DAT.ecg;
ecg = load('ecgnoise.dat');

analyzeECG(ecg);
