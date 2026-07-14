package com.mikelekan.scenefinder.service;

import org.shredzone.commons.suncalc.SunTimes;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SunCalculatorService {

    private static final ZoneId MOUNTAIN_TZ = ZoneId.of("America/Denver");
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a");

    // Golden hour duration in minutes after sunrise / before sunset
    private static final int GOLDEN_HOUR_MINUTES = 40;

    public Map<String, String> calculateSunTimes(double lat, double lng) {

        LocalDate today = LocalDate.now(MOUNTAIN_TZ);

        SunTimes sunTimes = SunTimes.compute()
                .on(today)
                .at(lat, lng)
                .timezone(MOUNTAIN_TZ)
                .execute();

        Map<String, String> result = new LinkedHashMap<>();

        // Sunrise
        ZonedDateTime sunrise = sunTimes.getRise();
        result.put("sunriseTime",
                sunrise != null ? sunrise.format(TIME_FORMAT) : "—");

        // Morning golden hour — starts at sunrise, ends 40 min after
        result.put("morningGoldenHour",
                sunrise != null
                        ? sunrise.plusMinutes(GOLDEN_HOUR_MINUTES).format(TIME_FORMAT)
                        : "—");

        // Solar noon
        ZonedDateTime noon = sunTimes.getNoon();
        result.put("solarNoonTime",
                noon != null ? noon.format(TIME_FORMAT) : "—");

        // Sunset
        ZonedDateTime sunset = sunTimes.getSet();
        result.put("sunsetTime",
                sunset != null ? sunset.format(TIME_FORMAT) : "—");

        // Evening golden hour — starts 40 min before sunset
        result.put("eveningGoldenHour",
                sunset != null
                        ? sunset.minusMinutes(GOLDEN_HOUR_MINUTES).format(TIME_FORMAT)
                        : "—");

        return result;
    }
}