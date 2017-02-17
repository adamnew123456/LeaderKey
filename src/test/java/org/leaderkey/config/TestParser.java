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

    @Test(timeout=5000)
    public void testKeyNames() {
        String[][] key_name_values = {
            {"Space", " "},
            {"Tilde", "~"},
            {"Backtick", "`"},
            {"Exclamation", "!"},
            {"At", "@"},
            {"Pound", "#"},
            {"Dollar", "$"},
            {"Percent", "%"},
            {"Carat", "^"},
            {"Ampersand", "&"},
            {"Star", "*"},
            {"LParen", "("},
            {"RParen", ")"},
            {"Dash", "-"},
            {"Underscore", "_"},
            {"Plus", "+"},
            {"Equals", "="},
            {"LBrace", "{"},
            {"RBrace", "}"},
            {"LBracket", "["},
            {"RBracket", "]"},
            {"Backslash", "\\"},
            {"Pipe", "|"},
            {"Colon", ":"},
            {"Semicolon", ";"},
            {"Quote", "'"},
            {"DoubleQuote", "\""},
            {"LAngle", "<"},
            {"RAngle", ">"},
            {"Question", "?"},
            {"Slash", "/"}
        };

        for (int i = 0; i < key_name_values.length; i++) {
            String key_name = key_name_values[i][0];
            String key_chars = key_name_values[i][1];

            Parser parser = parserFromString(
                "<" + key_name + "> \"test\" `doit`\n");

            try {
                LeaderGroup actual = parser.parse();

                LeaderGroup expected = new LeaderGroup();
                LeaderAction action = new LeaderAction("doit", EMPTY_RUNNER);

                expected.attach(key_chars.charAt(0), action, "test");

                assertEquals(expected, actual);
            } catch (SyntaxException err) {
                fail(key_name + ": " + err);
            }
        }
    }

    @Test(timeout=5000)
    public void testEscapes() {
        String[][] escape_strings = {
            {"\\b", "\b"},
            {"\\f", "\f"},
            {"\\n", "\n"},
            {"\\r", "\r"},
            {"\\t", "\t"},
            {"\\x4e", "N"},
        };

        for (int i = 0; i < escape_strings.length; i++) {
            String escape_text = escape_strings[i][0];
            String escape_value = escape_strings[i][1];

            Parser parser = parserFromString(
                "k \"test\" `" + escape_text + "`\n");

            try {
                LeaderGroup actual = parser.parse();

                LeaderGroup expected = new LeaderGroup();
                LeaderAction action = new LeaderAction(escape_value, EMPTY_RUNNER);

                expected.attach('k', action, "test");

                assertEquals(expected, actual);
            } catch (SyntaxException err) {
                fail(escape_text + ": " + err);
            }
        }
    }
}
