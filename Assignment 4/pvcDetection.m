function pvc = pvcDetection(ECG, R)  
    AREA = [];
    mean_dr = mean(diff(R));
    
    for i = 2 : length(R)
        try
            qrs = ECG(int32(R(i) - 0.1 * mean_dr) : int32(R(i) + 0.1 * mean_dr));
        catch
            qrs = ECG(abs(int32(R(i) - 0.1 * mean_dr)) : length(ECG));
        end
        
        qrs = qrs - mean(qrs);        
        AREA = [AREA; sum(abs(qrs))];
    end
    
    na = length(AREA);
    plot(1:na, AREA, 'g:', 1:na, AREA, 'bo');

    max_value = 1000;
    limiar = 650;
    normalize_value = max_value / max(AREA);
    pvc = 0;
    
    for i = 1 : length(AREA)    
        if AREA(i) * normalize_value > limiar
            pvc = pvc + 1;
        end
    end
end