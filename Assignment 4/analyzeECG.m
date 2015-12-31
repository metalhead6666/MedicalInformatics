function analyzeECG(ecg, fs)
    N = length(ecg);

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
        isNoise = noiseDetect(ecg);
        fprintf('Noise: %d\n', isNoise);

        if isNoise == 0
            isVT = vtDetection(ecg, R, seconds);
            fprintf('VT: %d\n', isVT);

            if isVT == 0
                figure(4);
                numberofPVC = pvcDetection(ecg, R);
                fprintf('Number of PVC: %d\n', numberofPVC);
            end
        end
        
    else
        for window = 0 : maximum - 1
            ecgWindow = ecg(window*position+1 : (window+1)*position);
            nWindow = length(ecgWindow);
            R = mh_rpeakdetect(ecgWindow, fs);

            figure(2);
            plot(1:nWindow, ecgWindow, 'g', R, ecgWindow(R), 'ro');
            title('Display a part of the ECG');

            figure(3);
            isNoise = noiseDetect(ecg);
            fprintf('Noise: %d\n', isNoise);

            if isNoise == 0
                isVT = vtDetection(ecg, R, seconds);
                fprintf('VT: %d\n', isVT);
                
                if isVT == 0
                    figure(4);
                    numberofPVC = pvcDetection(ecg, R);
                    fprintf('Number of PVC: %d\n', numberofPVC);
                else
                    close(figure(4));
                end
            else
                close(figure(4));
            end                                
            
            pause;
            clc
        end
    end
end