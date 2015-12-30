
clear
close all
list={...
    'DARR_008', 'DARR_024', 'DARR_029', 'DARR_019', ...
    'DARR_026', 'DARR_030', 'DARR_003', 'DARR_022', ...
    'DARR_027', 'DARR_035' }

for i=1:length(list)
    cmd=['load ' char(list(i)) ];
    eval(cmd);
    
    N=length(DAT.ecg); 
    figure(1)
    indR =find( DAT.class==-1);
    indN =find( DAT.class== 0);
    indVT=find( DAT.class== 4);
    
    plot(1:N,DAT.ecg(1:N),'g', ...
    indR , DAT.class(indR ),'b+', ...
    indN , DAT.class(indN ),'y+', ...
    indVT, DAT.class(indVT),'r+' )
    xlabel(list(i));
    pause
    
end
