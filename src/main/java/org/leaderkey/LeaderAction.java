package org.leaderkey;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * This runs a command on the command line.
 */
public class LeaderAction extends Leader {
    private String command;
    private BiConsumer<String, List<String>> launcher;

    public LeaderAction(String command,
                        BiConsumer<String, List<String>> launcher) {
        this.command = command;
        this.launcher = launcher;
    }

    /**
     * A LeaderAction will simply tell the UI to terminate, after it starts
     * running the given command.
     */
    @Override
    public LeaderUI.UIMode prepareUI(LeaderUI ui, List<String> vars) {
        ui.clearHints();
        launcher.accept(command, vars);
        return LeaderUI.UIMode.DONE;
    }

    /**
     * This should never be called.
     */
    @Override
    public Leader execute(String input, List<String> vars) {
        assert false;
        return null;
    }

    /**
     * The hint of an action is just the command that it executes.
     */
    @Override
    public String getHint() {
        return "executes `" + this.command + "`";
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
