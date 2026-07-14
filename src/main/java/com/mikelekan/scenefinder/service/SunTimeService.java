package com.mikelekan.scenefinder.service;

import com.mikelekan.scenefinder.dto.LocationDTO;
import org.shredzone.commons.suncalc.SunTimes;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class SunTimeService
{
    public SunTimes getSunTimes(LocationDTO location, LocalDate date)
    {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        return SunTimes.compute()
                .elevation(location.getElevationFt())
                .on(targetDate)
                .at(location.getLatitude(), location.getLongitude())
                .execute();
    }
}
