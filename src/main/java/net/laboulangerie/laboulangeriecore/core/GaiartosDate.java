package net.laboulangerie.laboulangeriecore.core;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class GaiartosDate {
    private LocalDate date;
    private LocalDate epochDate;
    private int startYear;

    public GaiartosDate(LocalDate date) {
        this.date = date;
        FileConfiguration config = LaBoulangerieCore.PLUGIN.getConfig();
        String epochStr = config.getString("calendar.epoch-date", "2022-09-01");
        this.epochDate = LocalDate.parse(epochStr, DateTimeFormatter.ISO_LOCAL_DATE);
        this.startYear = config.getInt("calendar.start-year", 0);
    }

    public List<String> getDayNames() {
        FileConfiguration config = LaBoulangerieCore.PLUGIN.getConfig();
        return config.getStringList("calendar.day-names");
    }

    public String getDayName() {
        List<String> dayNames = getDayNames();
        if (dayNames.isEmpty()) {
            return "";
        }
        int dayOfWeek = date.getDayOfWeek().getValue() - 1; // 0-6 (Monday=0)
        return dayNames.get(dayOfWeek % dayNames.size());
    }

    public int getDayOfYear() {
        long daysSinceEpoch = ChronoUnit.DAYS.between(epochDate, date);
        return (int) ((daysSinceEpoch % 7) + 1); // 1-7
    }

    public int getYear() {
        long daysSinceEpoch = ChronoUnit.DAYS.between(epochDate, date);
        return (int) (daysSinceEpoch / 7) + startYear;
    }

    public String getAgeName() {
        FileConfiguration config = LaBoulangerieCore.PLUGIN.getConfig();
        return config.getString("calendar.age-name", "");
    }
}
