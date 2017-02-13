package org.leaderkey;

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
}
