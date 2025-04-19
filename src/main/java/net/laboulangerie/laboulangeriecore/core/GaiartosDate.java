package net.laboulangerie.laboulangeriecore.core;

import java.time.LocalDate;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class GaiartosDate {
    private LocalDate date;
    private LocalDate firstDay = LocalDate.of(2022, 9, 1);

    public GaiartosDate(LocalDate date) {
        this.date = date;
    }

    public List<String> getMonthNames() {
        FileConfiguration config = LaBoulangerieCore.PLUGIN.getConfig();
        return config.getStringList("calendar.months");
    }

    public String getMonth() {
        List<String> months = getMonthNames();
        return months.get((date.getMonthValue() - 1) % months.size());
    }

    public int getYear() {
        int nMonthsSinceFirstDay =
                (date.getYear() - firstDay.getYear()) * 12 - firstDay.getMonthValue() + 1 + date.getMonthValue();

        return (int) Math.ceil(((double) nMonthsSinceFirstDay) / Math.max(getMonthNames().size(), 1));
    }
}
