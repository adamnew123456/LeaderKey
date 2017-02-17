package org.leaderkey;

/**
 * The leader UI is an abstract representation of what the real UI provides
 * to leaders.
 */
public interface LeaderUI {
    public enum UIMode {
        WAIT_FOR_LEADER,
        WAIT_FOR_PROMPT,
        DONE
    }

    /**
     * Remove all currently existing hint messages from the UI.
     */
    public void clearHints();

    /**
     * Add a new hint to the UI's current hint list. These should be displayed
     * to the user to allow the user to know what current actions are
     * available.
     */
    public void addHint(String hint);
}
