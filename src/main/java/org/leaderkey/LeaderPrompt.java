package org.leaderkey;

/**
 * This requests input from the user before moving into the next stage in the
 * pipeline.
 */
public class LeaderPrompt extends Leader {
    private String var;
    private String hint;
    private Leader action;

    public LeaderPrompt(String var, String hint) {
        this.var = var;
        this.hint = hint;
    }

    /**
     * Attaches an action so that it occurs when the prompt is answered.
     */
    public void attach(Leader action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LeaderPrompt)) {
            return false;
        }

        LeaderPrompt other_prompt = (LeaderPrompt)other;
        return other_prompt.var.equals(this.var) && 
            other_prompt.hint.equals(this.var);
    }

    @Override
    public String toString() {
        return "[" + var + "](" + hint + ") => " + action;
    }
}
