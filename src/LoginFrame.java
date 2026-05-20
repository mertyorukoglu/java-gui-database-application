package ui;

import dao.UserDAO;
import model.User;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        setTitle("MovieCritics – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.BG_DARK, 0, getHeight(), new Color(18, 12, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setBorder(new EmptyBorder(40, 50, 40, 50));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("🎬");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = UITheme.createLabel("MovieCritics", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = UITheme.createLabel("Family Movie Tracking System", UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(icon);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(subtitle);

        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            new EmptyBorder(28, 28, 28, 28)
        ));

        JLabel userLabel = UITheme.createLabel("Username", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField = UITheme.createStyledTextField();
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMinimumSize(new Dimension(10, 38));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel passLabel = UITheme.createLabel("Password", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = UITheme.createStyledPasswordField();
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMinimumSize(new Dimension(10, 38));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JButton loginBtn = UITheme.createPrimaryButton("Sign In →");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMinimumSize(new Dimension(10, 42));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setMinimumSize(new Dimension(10, 24));
        statusLabel.setPreferredSize(new Dimension(300, 24));

        card.add(userLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);

        JLabel hintLabel = new JLabel("");
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);

        root.add(topPanel, BorderLayout.NORTH);
        root.add(Box.createVerticalStrut(30), BorderLayout.CENTER);
        root.add(card, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(hintLabel);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);

        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        usernameField.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        User user = userDAO.login(username, password);
        if (user == null) {
            statusLabel.setText("Invalid username or password.");
            passwordField.setText("");
            return;
        }

        dispose();
        if (user.isParent()) {
            new Type1MainFrame(user).setVisible(true);
        } else {
            new Type2MainFrame(user).setVisible(true);
        }
    }
}
