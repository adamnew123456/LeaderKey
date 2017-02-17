package org.leaderkey;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * This maps a group of starting characters to each of their own actions.
 */
public class LeaderGroup extends Leader {
    private Map<Character, Leader> actions;
    private Map<Character, String> hints;

    public LeaderGroup() {
        actions = new HashMap<Character, Leader>();
        hints = new HashMap<Character, String>();
    }

    /**
     * Attaches an action to the group, with the given prefix.
     */
    public void attach(Character prefix, Leader action, String hint) {
        actions.put(prefix, action);
        hints.put(prefix, hint);
    }

    /**
     * A LeaderGroup will put hints for all its children on the UI, and wait
     * for more leaders.
     */
    @Override
    public LeaderUI.UIMode prepareUI(LeaderUI ui, List<String> vars) {
        ui.clearHints();
        for (char prefix: hints.keySet()) {
            String hint = hints.get(prefix);
            ui.addHint(prefix + " => " + hint);
        }

        return LeaderUI.UIMode.WAIT_FOR_LEADER;
    }

    /**
     * For execution, a leader simply chooses the appropriate child, or returns
     * null.
     */
    @Override
    public Leader execute(String input, List<String> vars) {
        char key = input.charAt(0);
        return actions.getOrDefault(key, null);
    }

    @Override
    public String getHint() {
        return toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LeaderGroup)) {
            return false;
        }

        LeaderGroup other_group = (LeaderGroup)other;
        return other_group.actions.equals(this.actions) &&
            other_group.hints.equals(this.hints);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");

        for (char key: hints.keySet()) {
            String hint = hints.get(key);
            buffer.append(key+ "=>" + hint + ";");
        }

        buffer.append("}");
        return buffer.toString();
    }
}
