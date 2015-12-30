clear all;clc
warning('off','all');

choice = 0;

while choice ~= 1 && choice ~= 2
    choice = input('1-Analyze\n2-Test\n');
end

if choice == 1
    % files used to analyze the application
    load 'DATARR//DARR_022.mat';
    load 'DATARR//DARR_029.mat';
    load 'DATPVC//DPVC_106.mat';
    load 'DATPVC//DPVC_210.mat';
else
    % choose a file randomly to test the application
    dirfiles = dir('DATARR');
    array = dirfiles(3 : length(dirfiles)-1);
    dirfiles = dir('DATPVC');
    array = [array; dirfiles(3 : length(dirfiles)-1)];
    randnum = randi(length(array));
    filename = array(randnum).name;
    
    if strcmp(filename(1:4),'DPVC') == 1
        str = strcat('DATPVC//', filename);
    else
        str = strcat('DATARR//', filename);
    end
    
    load(str);
end

analyzeECG(DAT.ecg);
