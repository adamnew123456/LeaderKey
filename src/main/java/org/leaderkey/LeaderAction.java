package org.leaderkey;

/**
 * This runs a command on the command line.
 */
public class LeaderAction extends Leader {
    private String command;

    public LeaderAction(String command) {
        this.command = command;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LeaderAction)) {
            return false;
        }

        LeaderAction other_action = (LeaderAction)other;
        return other_action.command.equals(this.command);
    }

    @Override
    public String toString() {
        return "`" + this.command + "`";
    }
}
