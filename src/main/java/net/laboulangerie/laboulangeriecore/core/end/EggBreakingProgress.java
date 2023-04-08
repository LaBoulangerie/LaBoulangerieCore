package net.laboulangerie.laboulangeriecore.core.end;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class EggBreakingProgress {
    public static int REQUIRED_CLICKS = LaBoulangerieCore.PLUGIN.getConfig().getInt("clicks-to-break-egg");
    private int timesClicked = 0;
    private boolean clickedRecently = true;

    /**
     * 
     * @return whether or not the progression has reached its maximum
     */
    public boolean registerClick() {
        if (timesClicked <= REQUIRED_CLICKS - 1) {
            timesClicked++;
            clickedRecently = true;
            return false;
        } else
            return true;
    }

    public boolean wasClickedRecently() {
        return clickedRecently;
    }

    public void setClickedRecently(boolean clickedRecently) {
        this.clickedRecently = clickedRecently;
    }

    public double getProgression() {
        return (double) timesClicked / (double) REQUIRED_CLICKS;
    }
}
