package org.leaderkey;

import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;

import javax.swing.*;

/**
 * The user interface is responsible for accepting keyboard input, presenting
 * it to the current leader, and then doing what the leader requires of it.
 */
public class UI implements LeaderUI {
    JFrame main_window;
    JPanel content;

    JTextField prompt_entry;
    JLabel leader_history;

    JList<String> hint_view;
    DefaultListModel<String> hint_list;
    List<String> current_variables;

    Leader root_leader, current_leader;

    LeaderKeyListener wait_for_leader;

    private class LeaderKeyListener implements KeyListener {
        private boolean enabled;
        private JFrame parent;

        public LeaderKeyListener(JFrame parent) {
            this.parent = parent;
            enabled = true;
        }

        public void enable() {
            enabled = true;
        }

        public void disable() {
            enabled = false;
        }

        @Override
        public void keyPressed(KeyEvent event) {}

        @Override
        public void keyReleased(KeyEvent event) {
            if (event.getKeyCode() == event.VK_ESCAPE) {
                parent.dispose();
            }
        }

        @Override
        public void keyTyped(KeyEvent event) {
            if (!enabled) {
                return;
            }

            char key = event.getKeyChar();
            String key_string = String.valueOf(key);
            leader_history.setText(leader_history.getText() + " " + key);
            System.out.println("Got key: " + key_string);

            current_leader = current_leader.execute(key_string, current_variables);
            updateLeader();
        }
    }

    public UI(Leader root) {
        root_leader = root;
        current_leader = root;
        current_variables = new ArrayList<String>();

        main_window = new JFrame("LeaderKey");
        main_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_window.setLayout(new BorderLayout());

        // Wait for leader is responsible for accepting the next leader key and
        // dispatching to the next leader
        wait_for_leader = new LeaderKeyListener(main_window);
        main_window.addKeyListener(wait_for_leader);

        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        main_window.add(content, BorderLayout.CENTER);

        prompt_entry = new JTextField();
        leader_history = new JLabel("Action: ");
        hint_list = new DefaultListModel<String>();
        hint_view = new JList<String>(hint_list);

        prompt_entry.addActionListener((event) -> {
                String prompt_value = prompt_entry.getText();
                current_leader = current_leader.execute(prompt_value, current_variables);
                updateLeader();
            });

        main_window.setVisible(true);
        main_window.setFocusable(true);
        updateLeader();
    }

    /**
     * This asks the current leader to configure the UI, and then goes to the
     * mode that it requests.
     */
    private void updateLeader() {
        System.out.println("Current leader: " + current_leader);
        if (current_leader == null) {
            doneMode();
            return;
        }

        LeaderUI.UIMode mode = current_leader.prepareUI(this, current_variables);
        System.out.println("Entering mode " + mode);

        switch (mode) {
        case WAIT_FOR_LEADER:
            waitForLeaderMode();
            break;
        case WAIT_FOR_PROMPT:
            waitForPromptMode();
            break;
        case DONE:
            doneMode();
            break;
        }
    }

    /**
     * This UI mode is where the user can press a new leader, and then
     * dispatch to one of its subleaders.
     */
    private void waitForLeaderMode() {
        wait_for_leader.enable();

        content.removeAll();
        content.add(leader_history);
        content.add(hint_view);
        content.revalidate();
        content.repaint();
        main_window.pack();

        main_window.requestFocus();
    }

    /**
     * This UI mode is where the user can enter prompted text, and then dispatch
     * to the prompt's action.
     */
    private void waitForPromptMode() {
        System.out.println("Setting up prompt...");
        wait_for_leader.disable();

        content.removeAll();
        content.add(leader_history);
        content.add(prompt_entry);
        content.add(hint_view);
        content.revalidate();
        content.repaint();
        main_window.pack();

        System.out.println("Focusing prompt");
        prompt_entry.requestFocus();
    }

    /**
     * This terminates the UI.
     */
    private void doneMode() {
        main_window.dispose();
    }

    /**
     * Removes all subcommand hints from the UI.
     */
    public void clearHints() {
        hint_list.clear();
    }

    /**
     * Adds a new subcommand hint to the UI.
     */
    public void addHint(String hint) {
        hint_list.addElement(hint);
    }
}
