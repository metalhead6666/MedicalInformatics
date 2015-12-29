function pvcDetection(ECG, R, fs)
    limit = 160;    
       
    AREA = [];
    mean_dr = mean(diff(R));
    for i = 2 : length(R)
        try
            qrs = ECG(R(i) - 0.1 * mean_dr : R(i) + 0.1 * mean_dr);
        catch
            qrs = ECG(R(i) - 0.1 * mean_dr : length(ECG));
        end
        qrs = qrs - mean(qrs);
        area = sum(abs(qrs));
        if area > limit
            disp('A wild PVC appears.')
        end
        AREA = [AREA; area];
    end

    na = length(AREA);
    plot(1:na, AREA, 'g:', 1:na, AREA, 'bo', 1:na, limit * ones(1, na), 'r');   
    
        
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