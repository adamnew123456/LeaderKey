package org.leaderkey;
import org.leaderkey.config.Parser;
import org.leaderkey.config.SyntaxException;

import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("leaderkey <CONFIG-FILE>");
            System.exit(1);
        }

        LeaderGroup base_leader = null;
        CommandRunner runner = new CommandRunner();

        try {
            FileInputStream input = new FileInputStream(args[0]);
            Parser parser = new Parser(input, runner);
            base_leader = parser.parse();
        } catch (IOException err) {
            System.err.println("Unable to read configuration file: " + err);
            System.exit(1);
        } catch (SyntaxException err) {
            System.err.println("Unable to parse configuration file: " + err);
            System.exit(1);
        }

        final LeaderGroup base_leader_final = base_leader;
        SwingUtilities.invokeLater(() -> {
                new UI(base_leader_final);
            });
    }
}
