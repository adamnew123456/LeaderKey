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
    public String toString() {
        return "`" + this.command + "`";
    }
}
