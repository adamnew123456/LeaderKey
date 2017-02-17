package org.leaderkey;

import java.util.List;

/**
 * A leader can be either:
 *
 * - A leader key, which maps a group of keys to other leaders
 * - A prompt, which requires the user to enter a value to proceed to another
 *   leader.
 * - An action, which runs a shell command that is formatted with the prompts
 *   defined before it.
 */
public abstract class Leader {
    /**
     * This method should prepare the UI for the execution of the leader.
     * If this returns DONE, then execute is never called and the UI exits
     * as soon as this returns.
     */
    public abstract LeaderUI.UIMode prepareUI(LeaderUI ui,
                                              List<String> variables);

    /**
     * This method should choose what the following Leader to act upon is.
     * Note that if prepareUI returns DONE than this is never called. Should
     * this return null, the UI will terminate.
     */
    public abstract Leader execute(String input, List<String> variables);

    /*
     * This is a hint that the user can provide themselves to remember what
     * each Leader does.
     */
    public abstract String getHint();
}
