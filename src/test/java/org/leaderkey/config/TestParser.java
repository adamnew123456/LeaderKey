package org.leaderkey.config;

import org.leaderkey.Leader;
import org.leaderkey.LeaderAction;
import org.leaderkey.LeaderGroup;
import org.leaderkey.LeaderPrompt;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiConsumer;

public class TestParser {
    private static BiConsumer<String, List<String>> EMPTY_RUNNER =
        (command, args) -> {};

    private Parser parserFromString(String input) {
        ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        return new Parser(stream, EMPTY_RUNNER);
    }

    @Test(timeout=5000)
    public void testParseBasic() {
        Parser parser = parserFromString(
            "f \"open firefox\" {\n" +
            "    o \"open url\" [url] \"url\" `firefox %1`\n" +
            "}\n");

        try {
            LeaderGroup actual = parser.parse();

            LeaderGroup expected = new LeaderGroup();
            LeaderGroup firefox = new LeaderGroup();
            LeaderPrompt url_prompt = new LeaderPrompt("url", "url");
            LeaderAction url_command = new LeaderAction("firefox %1", EMPTY_RUNNER);

            url_prompt.attach(url_command);
            firefox.attach('o', url_prompt, "open url");
            expected.attach('f', firefox, "open firefox");

            assertEquals(expected, actual);
        } catch (SyntaxException err) {
            fail(err.toString());
        }
    }

    @Test(timeout=5000)
    public void testParseMultipleRules() {
        Parser parser = parserFromString(
            "f \"open firefox\" {\n" +
            "    o \"open url\" [url] \"url\" `firefox %1`\n" +
            "    t \"new tab\" `firefox`\n" +
            "}\n");

        try {
            LeaderGroup actual = parser.parse();

            LeaderGroup expected = new LeaderGroup();
            LeaderGroup firefox  = new LeaderGroup();
            LeaderAction tab_command = new LeaderAction("firefox", EMPTY_RUNNER);

            LeaderPrompt url_prompt = new LeaderPrompt("url", "url");
            LeaderAction url_command = new LeaderAction("firefox %1", EMPTY_RUNNER);

            url_prompt.attach(url_command);
            firefox.attach('o', url_prompt, "open url");
            firefox.attach('t', tab_command, "new tab");
            expected.attach('f', firefox, "open firefox");

            assertEquals(expected, actual);
        } catch (SyntaxException err) {
            fail(err.toString());
        }
    }
}
