package net.laboulangerie.laboulangeriecore.core.event;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class EventState {
    private String name;
    private ArrayList<EventStep> steps = new ArrayList<>();
    private int stage = 0;
    private boolean started = false;
    private boolean ended = false;

    public EventState(String name) {
        this.name = name;
    }

    public void start() { started = true; }
    public void stop() {
        started = false;
        ended = true;
    }
    public void reset() {
        started = false;
        ended = false;
        stage = 0;
    }
    public String getName() { return name; }
    /**
     * Execute the next step in this event
     * @return false if the step couldn't run because the event wasn't started or had already ended
     */
    public boolean nextStep(Player executor) {
        if (!started || ended) return false;

        steps.get(stage).executeActions(executor);

        stage++;
        if (stage == steps.size()) stop();
        return true;
    }

    /**
     * Go to the specified step without executing it
     * @return false if the step couldn't run because the event wasn't started or had already ended or if the step doesn't exist
     */
    public boolean goTo(String name) {
        if (!started || ended) return false;

        EventStep step = null;
        int index = 0;
        for (; index < steps.size(); index++) {
            EventStep potentialStep = steps.get(index);
            if (potentialStep.getName().equals(name)) {
                step = potentialStep;
                break;
            }
        }

        if (step == null) return false;
        stage = ++index;
        return true;
    }

    public void addStep(EventStep step) {
        this.steps.add(step);
    }

    public ArrayList<EventStep> getSteps() { return steps; }
    public boolean hasStarted() { return started; }
    public boolean hasEnded() { return ended; }
}
