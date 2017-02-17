package org.leaderkey;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * This executes a system command, after performing all the argument
 * substitutions.
 */
public class CommandRunner implements BiConsumer<String, List<String>> {
    @Override
    public void accept(String command, List<String> args) {
        StringBuilder command_builder = new StringBuilder();

        for (int i = 0; i < command.length(); i++) {
            char cmd_current = command.charAt(i);
            if (cmd_current == '%' && i + 1 < command.length()) {
                char next_char = command.charAt(i + 1);
                if (Character.isDigit(next_char)) {
                    String digit_string = String.valueOf(next_char);
                    int index = Integer.valueOf(digit_string);
                    if (index < args.size()) {
                        command_builder.append(args.get(index));
                    } else {
                        command_builder.append("%" + index);
                    }

                    i++;
                } else if (next_char == '%') {
                    // This escapes the character after the second %, so make
                    // sure it gets ignored
                    command_builder.append("%");
                    i++;
                } else {
                    command_builder.append("%" + next_char);
                }
            } else {
                command_builder.append(cmd_current);
            }
        }

        try {
            Runtime.getRuntime().exec(command_builder.toString());
        } catch (IOException err) {
            // We can't do anything with this, the UI doesn't have any
            // way we can notify the user.:w
        }
    }
}
