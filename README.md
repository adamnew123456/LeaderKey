Leaderkey is an application launcher which uses Spacemacs-like leader keys for choosing applications.

Configuration
-------------

The Leaderkey configuration language is made up of leader groups, prompts and commands.

- Leader groups provide several options for keys which users can press to specify a particular
  sequence of actions (which can be either prompts or commands)
- Prompts require the user to input a line of text, which is passed onto later actions in the
  same chain.
- Commands execute a system command, which can contain arguments gathered from prompts.

Here is an example configuration file:

    s "system" {
      x "xterm" `xterm`
      g "gvim on file" [filename] `gvim -f %1`
    }

    i "internet" {
      f "firefox" `firefox`
    }
   
There are several interactions possible after Leaderkey opens:

- The user hits 'i' and then 'f', which executes the command `firefox`.
- The user hits 's' and then 'g' and is prompted for a filename. After entering "/tmp/foo.txt", 
  the command `gvim -f /tmp/foo.txt` is executed.
  
The text in double-quotes is help text - after the user presses 's', the hints "x => xterm" and "g => gvim on file"
are displayed. Prompts are also their own help text: after pressing 's' then 'g', the hint "filename" is displayed
while prompting for input.
  
Note that only alphanumeric characters can be used as keys directly; some have to be mapped via aliases
such as <Tilde> or <Space>. See the Key Symbols section for a complete listing.

Currently only 9 variables, %1 through %9, are supported for prompts (others will be inaccessible). Variables can
be escaped by using %%. Undefined variables (for example, %2 in the above file) will automatically be escaped; in
the above example, `gvim -f %9` would literally be `gvim -f %9`. 

Additionally, you can use some C-style escapes in hints, commands and prompts.

- \\b, \\f, \\n, \\r, \\t
- \\x?? hexadecimal escapes

Other restrictions include:

- Commands cannot be followed by anything, they always terminate the current leader.
- Prompts cannot be followed by groups, only by leaders.

Key Symbols
-----------

- Space: (a literal space character)
- Tilde: ~
- Backtick: \`
- Exclamation: !
- At: @
- Pound: #
- Dollar: $
- Percent: %
- Carat: ^
- Ampersand: &
- Star: *
- LParen: (
- RParen: )
- Dash: -
- Underscore: _
- Plus: +
- Equals: =
- LBrace: 
- RBrace: }
- LBracket: [
- RBracket: ]
- Backslash: \\
- Pipe: |
- Colon: :
- Semicolon: ;
- Quote: '
- DoubleQuote: "
- LAngle: <
- RAngle: >
- Question: ?
- Slash: /

Building
--------

Run `gradle jar` at the top-level, which will produce a JAR file called 
LeaderKey.jar underneath build/libs.

Running
-------

Execute the JAR file with the path to the leader configuration file as an
argument; `java -jar LeaderKey.jar /path/to/config.leader'
