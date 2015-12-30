function analyzeECG(ecg)
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
        isNoise = noiseDetect(ecg);

        if isNoise == 0
            figure(4);
            isVT = vtDetection(ecg, R, seconds);

            if isVT == 0
                figure(5);
                pvcDetection(ecg, R, fs);
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
                figure(4);
                isVT = vtDetection(ecg, R, seconds);
                fprintf('VT: %d\n', isVT);
                
                if isVT == 0
                    figure(5);
                    numberofPVC = pvcDetection(ecg, R, fs);
                    fprintf('Number of PVC: %d\n', numberofPVC);
                else
                    close(figure(5));
                end
            else
                close(figure(4));
                close(figure(5));
            end                                
            
            pause;
            clc
        end
    end

    % plot(1:N, ecg, 'g', R, ecg(R), 'ro');
end