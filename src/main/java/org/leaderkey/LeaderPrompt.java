package org.leaderkey;

import java.util.List;

/**
 * This requests input from the user before moving into the next stage in the
 * pipeline.
 */
public class LeaderPrompt extends Leader {
    private String hint;
    private Leader action;

    public LeaderPrompt(String hint) {
        this.hint = hint;
    }

    /**
     * Attaches an action so that it occurs when the prompt is answered.
     */
    public void attach(Leader action) {
        this.action = action;
    }

    /**
     * Preparation for a prompt just involves setting the hint and waiting for
     * input.
     */
    @Override
    public LeaderUI.UIMode prepareUI(LeaderUI ui, List<String> args) {
        ui.clearHints();
        ui.addHint(hint);
        return LeaderUI.UIMode.WAIT_FOR_PROMPT;
    }

    /**
     * Executing a prompt means saving the input and going to the next action.
     */
    @Override
    public Leader execute(String input, List<String> args) {
        args.add(input);
        return action;
    }

    /**
     * The hint of a prompt is merely its configuration-assigned hint.:
     */
    @Override
    public String getHint() {
        return hint;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LeaderPrompt)) {
            return false;
        }

        LeaderPrompt other_prompt = (LeaderPrompt)other;
        return other_prompt.hint.equals(this.hint);
    }

    @Override
    public String toString() {
        return "[" + hint + "] => " + action;
    }
}
