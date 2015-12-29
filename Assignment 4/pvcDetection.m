function pvcDetection()
    ecg = load('ecgPVC.dat');

    N = length(ecg);
    fs = 250;
    limit = 160;    
    period = int32(N/fs);    
    seconds = 5;
    position = int32(fs*seconds);    
    maximum = period/seconds;

    for window=0 : maximum - 1        
        ecgwindow = ecg(window*position+1 : (window+1)*position);        
        area = [];
        
        R = mh_rpeakdetect(ecgwindow, fs);
        mean_dr = mean(diff(R));

        for i=2 : length(R)
            qrs = ecgwindow(int8(R(i)-0.1*mean_dr) : int8(R(i)+0.1*mean_dr));
            qrs = qrs - mean(qrs);
            area = [area; sum(abs(qrs))];
        end

        na = length(area);
        plot(1:na, area, 'g:', 1:na, area, 'bo', 1:na, limit * ones(1, na), 'r');
        pause;
    end
        
    % figure(1)
    % plot(ecg);
    % 
    % R = mh_rpeakdetect(ecg, fs);
    % [w, vf] = pwelch(ecg, 150);
    % figure(2)
    % plot(w);
    % %plot(1:N, ecg, 'g', R, ecg(R), 'ro');
    % 
    % dR = diff(R);
    % 
    % ndR = length(dR);
    % figure(3)
    % plot(dR, 'go');
    % 
    % mu = mean(dR);
    % mu2 = mu + 0.10 * mu;
    % hline = refline([0 mu]);
    % hline.Color = 'r';
    % 
    % hline = refline([0 mu2]);
    % hline.Color = 'b';
    % 
    % for i = 2 : ndR
    %     temp = dR(i) - dR(i-1);
    %     if(temp > limit)
    %         pvcCount = pvcCount + 1;
    %     end
    % end
    % 
    % pvcCount
end