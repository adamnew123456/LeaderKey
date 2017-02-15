package org.leaderkey.config;

import org.leaderkey.Leader;
import org.leaderkey.LeaderAction;
import org.leaderkey.LeaderGroup;
import org.leaderkey.LeaderPrompt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * The ConfigParser is responsible for consuming leaderkey configuration
 * files and returning Leader groups defined by them.
 *
 * Grammar:
 *
 *   top ::= rules
 *   rules ::= rule+
 *   rule ::= key hint '{' rules '}' |
 *            key hint subrule
 *   subrule ::= prompt hint subrule |
 *               action
 *   prompt ::= '[' string ']'
 *   action ::= '`' string '`'
 *   key ::= alphanum | '<' string '>'
 *   hint ::= '"' string '"'
 *   string ::= C-style string (sans octal)
 */
public class Parser {
    private static int EOF = -1;
    private Reader reader;
    private int current_char;
    private Map<String, Character> key_names;

    public Parser(InputStream stream) {
        reader = new InputStreamReader(stream);
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

        key_names = new HashMap<String, Character>();
        for (int i = 0; i < key_name_values.length; i++) {
            key_names.put(key_name_values[i][0],
                          key_name_values[i][1].charAt(0));
        }
    }

    /**
     * Reads the next character in the input.
     */
    private void read() throws SyntaxException {
        try {
            current_char = reader.read();
        } catch (IOException err) {
            throw new SyntaxException("IO Error: " + err.toString());
        }
    }

    /**
     * Accept only if the next character is in the string.
     */
    private char acceptIfIsIn(String allowed) throws SyntaxException {
        if (current_char == EOF) {
            throw new SyntaxException("Saw EOF, expected one of " + allowed);
        }

        String char_string = String.valueOf((char)current_char);
        if (allowed.contains(char_string)) {
            read();
            return char_string.charAt(0);
        } else {
            throw new SyntaxException("Saw " + char_string + " but expected one of " + allowed);
        }
    }

    /**
     * Consumes a chunk of whitespace.
     */
    private void chomp() throws SyntaxException {
        while (current_char != EOF && Character.isWhitespace((char)current_char)) {
            read();
        }
    }

    /**
     * Reads a (possibly multi-line) string-like value from the input.
     */
    private String parseStringLike(char delimiter, char escape)
        throws SyntaxException {
        StringBuilder builder = new StringBuilder();

        boolean escaped = false;
        while (true) {
            if (current_char == EOF) {
                throw new SyntaxException("EOF found in string-like");
            }

            char current = (char)current_char;
            if (escaped) {
                escaped = false;

                switch (current) {
                case 'b':
                    builder.append('\b');
                    read();
                    break;
                case 'f':
                    builder.append('\f');
                    read();
                    break;
                case 'n':
                    builder.append('\n');
                    read();
                    break;
                case 'r':
                    builder.append('\r');
                    read();
                    break;
                case 't':
                    builder.append('\t');
                    read();
                    break;
                case 'x':
                    char first = acceptIfIsIn("0123456789abcdefABCDEF");
                    char second = acceptIfIsIn("0123456789abcdefABCDEF");
                    builder.append((char)(Integer.parseInt("" + first + second)));
                    break;
                default:
                    builder.append(current);
                    read();
                }
            } else if (current == escape) {
                read();
                escaped = true;
            } else if (current == delimiter) {
                read();
                break;
            } else {
                builder.append(current);
                read();
            }
        }

        return builder.toString();
    }

    /**
     * Parses a sub-rule.
     */
    private Leader parseSubRule() throws SyntaxException {
        if (current_char == EOF) {
            throw new SyntaxException("Expected subcommand, saw EOF");
        } else if ((char)current_char == '[') {
            acceptIfIsIn("[");
            String var = parseStringLike(']', '\\');

            chomp();
            acceptIfIsIn("\"");
            String hint = parseStringLike('"', '\\');

            chomp();
            Leader subrule = parseSubRule();
            LeaderPrompt rule = new LeaderPrompt(var, hint);
            rule.attach(subrule);
            return rule;
        } else if ((char)current_char == '`') {
            acceptIfIsIn("`");
            String action = parseStringLike('`', '\\');
            return new LeaderAction(action);
        } else {
            throw new SyntaxException("Expected subrule, saw " +
                                      (char)current_char);
        }
    }

    /**
     * Parses a single rule.
     */
    private Pair<Character, Leader> parseRule() throws SyntaxException {
        char key_character;
        if (current_char == EOF) {
            throw new SyntaxException("Expected rule, saw EOF");
        } else if ((char)current_char == '<') {
            read();

            String key_name = parseStringLike('>', '\\');
            if (key_names.containsKey(key_name)) {
                key_character = key_names.get(key_name);
            } else {
                throw new SyntaxException("Expected key, saw " + key_name);
            }
        } else {
            key_character = acceptIfIsIn(
                    "abcdefghijklmnopqrstuvwxyz" + 
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + 
                    "0123456789");
        }


        chomp();
        acceptIfIsIn("\"");
        String hint = parseStringLike('"', '\\');

        chomp();
        if (current_char == EOF) {
            throw new SyntaxException("Expecting action or subrule in rule, saw EOF");
        } else if ((char)current_char == '{') {
            read();
            chomp();
            LeaderGroup group = parseRules(hint);
            acceptIfIsIn("}");
            return new Pair<Character, Leader>(key_character, group);
        } else {
            Leader subrule = parseSubRule();
            return new Pair<Character, Leader>(key_character, subrule);
        }
    }

    /**
     * Parses a group of subactions all bounds to a common leader.
     */
    private LeaderGroup parseRules(String help) throws SyntaxException {
        LeaderGroup toplevel = new LeaderGroup(help);

        while (true) {
            chomp();
            if (current_char == EOF) {
                break;
            } else if ((char)current_char == '}') {
                break;
            }

            Pair<Character, Leader> bind = parseRule();
            toplevel.attach(bind.first, bind.second);
            chomp();
        }

        return toplevel;
    }

    /**
     * Parses the whole configuration file.
     */
    public LeaderGroup parse() throws SyntaxException {
        read();
        return parseRules("");
    }
}
