function peaks = peakDetection(x,ff)
%
% peaks = PeakDetection(x,f),
% R-peak detector
%
% inputs:
% x: vector of input data
% f: approximate ECG beat-rate in Hertz, normalized by the sampling frequency
%
% output:
% peaks: vector of R-peak impulse train
%
% Notes:
% - The R-peaks are found from a peak search in windows of length N; where 
% N corresponds to the R-peak period calculated from the given f. R-peaks 
% with periods smaller than N/2 or greater than N are not detected.
% - The signal baseline wander is recommended to be removed before the
% R-peak detection
%
%
% Open Source ECG Toolbox, version 1.0, November 2006
% Released under the GNU General Public License
% Copyright (C) 2006  Reza Sameni
% Sharif University of Technology, Tehran, Iran -- LIS-INPG, Grenoble, France
% reza.sameni@gmail.com

% This program is free software; you can redistribute it and/or modify it
% under the terms of the GNU General Public License as published by the
% Free Software Foundation; either version 2 of the License, or (at your
% option) any later version.
% This program is distributed in the hope that it will be useful, but
% WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
% Public License for more details. You should have received a copy of the
% GNU General Public License along with this program; if not, write to the
% Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
% MA  02110-1301, USA.

N = length(x);
peaks = zeros(N,1);

th = .5;
rng = floor(th/ff);

if(abs(max(x))>abs(min(x)))
    for j = 1:N,
        %         index = max(j-rng,1):min(j+rng,N);
        if(j>rng & j<N-rng)
            index = j-rng:j+rng;
        elseif(j>rng)
            index = N-2*rng:N;
        else
            index = 1:2*rng;
        end

        if(max(x(index))==x(j))
            peaks(j) = 1;
        end
    end
else
    for j = 1:N,
        %         index = max(j-rng,1):min(j+rng,N);
        if(j>rng & j<N-rng)
            index = j-rng:j+rng;
        elseif(j>rng)
            index = N-2*rng:N;
        else
            index = 1:2*rng;
        end

        if(min(x(index))==x(j))
            peaks(j) = 1;
        end
    end
end


% remove fake peaks
I = find(peaks);
d = diff(I);
z = find(d<rng);
peaks(I(z))=0;