package org.leaderkey.config;

import org.leaderkey.Leader;
import org.leaderkey.LeaderAction;
import org.leaderkey.LeaderGroup;
import org.leaderkey.LeaderPrompt;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class TestParser {
    @Test(timeout=5000)
    public void testParseBasic() {
        String input =
            "f \"open firefox\" {\n" +
            "    o \"open url\" [url] \"url\" `firefox %1`\n" +
            "}\n";

        ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        Parser parser = new Parser(stream);

        try {
            LeaderGroup actual = parser.parse();

            LeaderGroup expected = new LeaderGroup();
            LeaderGroup firefox = new LeaderGroup();
            LeaderPrompt url_prompt = new LeaderPrompt("url", "url");
            LeaderAction url_command = new LeaderAction("firefox %1");

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
        String input =
            "f \"open firefox\" {\n" +
            "    o \"open url\" [url] \"url\" `firefox %1`\n" +
            "    t \"new tab\" `firefox`\n" +
            "}\n";

        ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        Parser parser = new Parser(stream);

        try {
            LeaderGroup actual = parser.parse();

            LeaderAction tab_command = new LeaderAction("firefox");
            LeaderGroup expected = new LeaderGroup();
            LeaderGroup firefox  = new LeaderGroup();
            LeaderPrompt url_prompt = new LeaderPrompt("url", "url");
            LeaderAction url_command = new LeaderAction("firefox %1");

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
