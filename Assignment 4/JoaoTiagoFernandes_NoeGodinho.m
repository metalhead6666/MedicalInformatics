clear all;clc
warning('off','all');

choice = 0;

while choice ~= 1 && choice ~= 2
    choice = input('1-Analyze\n2-Test\n');
end

if choice == 1
    % files used to analyze the application
    filename = 0;
    
    while filename ~= 1 && filename ~= 2 && filename ~= 3 && filename ~= 4
        filename = input('1-DARR_022\n2-DARR_029\n3-DPVC_106\n4-DPVC_210\n');
    end
    
    if filename == 1
        load 'DATARR//DARR_022.mat';
    elseif filename == 2
        load 'DATARR//DARR_029.mat';
    elseif filename == 3
        load 'DATPVC//DPVC_106.mat';
    else
        load 'DATPVC//DPVC_210.mat';
    end
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
    disp(str);
    pause;
end

analyzeECG(DAT.ecg);
