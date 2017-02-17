package org.leaderkey;

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
        buffer.append("{\n");

        for (Map.Entry<Character, Leader> entry: actions.entrySet()) {
            buffer.append("    " + entry.getKey());
            buffer.append(" => " + entry.getValue() + "\n");
        }

        buffer.append("}\n");
        return buffer.toString();
    }
}
