package org.leaderkey;

import java.util.HashMap;
import java.util.Map;

/**
 * This maps a group of starting characters to each of their own actions.
 */
public class LeaderGroup extends Leader {
    private String hint;
    private Map<Character, Leader> actions;

    public LeaderGroup(String hint) {
        this.hint = hint;
        actions = new HashMap<Character, Leader>();
    }

    /**
     * Attaches an action to the group, with the given prefix.
     */
    public void attach(Character prefix, Leader action) {
        actions.put(prefix, action);
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
