function [ pvc ] = pvcDetection(ECG, R)  
    AREA = [];
    mean_dr = mean(diff(R));
    
    for i = 2 : length(R)
        try
            qrs = ECG(int32(R(i) - 0.1 * mean_dr) : int32(R(i) + 0.1 * mean_dr));
        catch
            qrs = ECG(abs(int32(R(i) - 0.1 * mean_dr)) : length(ECG));
        end
        qrs = qrs - mean(qrs);
        area = sum(abs(qrs));
        AREA = [AREA; area];
    end
    
    na = length(AREA);
    plot(1:na, AREA, 'g:', 1:na, AREA, 'bo');

    max_value = 1000;
    limiar = 650;
    normalize_value = max_value / max(AREA);
    pvc = 0;
    
    for i = 1 : length(AREA)
        value = (AREA(i)*normalize_value);
        
        if value > limiar
            pvc = pvc + 1;
        end
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