package ui;

import model.User;
import ui.panels.*;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Type2MainFrame extends JFrame {

    private User currentUser;
    private JPanel contentArea;

    public Type2MainFrame(User user) {
        this.currentUser = user;
        setTitle("MovieCritics – Child Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createSidebar(), BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG_DARK);
        contentArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.add(contentArea, BorderLayout.CENTER);

        showPanel(new BrowseMoviesPanel(currentUser));
        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 15, 55), getWidth(), 0, new Color(20, 40, 70));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel logo = new JLabel("  MovieCritics", UITheme.createFilmIcon(22), SwingConstants.LEFT);
        logo.setFont(UITheme.FONT_HEADER);
        logo.setForeground(Color.WHITE);

        JLabel userInfo = new JLabel(currentUser.getUsername() + " (Child)");
        userInfo.setFont(UITheme.FONT_BODY);
        userInfo.setForeground(UITheme.TEXT_MUTED);

        JButton logoutBtn = UITheme.createDangerButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(90, 34));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(userInfo);
        right.add(logoutBtn);

        header.add(logo, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_PANEL);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel sectionLabel = UITheme.createLabel("  MY FUNCTIONS", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        sectionLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        sidebar.add(sectionLabel);

        String[][] menuItems = {
            {"Browse Movies",   "browse"},
            {"Log Watched",     "watchedlog"},
            {"Rate Movies",     "rate"},
            {"Comments",        "comments"},
            {"My Watchlist",    "watchlist"},
            {"Track Progress",  "progress"},
            {"Family Ratings",  "familyratings"},
        };

        for (String[] item : menuItems) {
            JButton btn = createMenuButton(item[0]);
            String key = item[1];
            btn.addActionListener(e -> navigate(key));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setIcon(UITheme.createDotIcon(UITheme.ACCENT_BLUE, 8));
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(UITheme.TEXT_PRIMARY);
        btn.setBackground(UITheme.BG_PANEL);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(8);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setBorder(new EmptyBorder(10, 16, 10, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(25, 35, 65));
                btn.setForeground(UITheme.ACCENT_BLUE);
                btn.setIcon(UITheme.createDotIcon(UITheme.ACCENT_PINK, 8));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(UITheme.BG_PANEL);
                btn.setForeground(UITheme.TEXT_PRIMARY);
                btn.setIcon(UITheme.createDotIcon(UITheme.ACCENT_BLUE, 8));
            }
        });
        return btn;
    }

    private void navigate(String key) {
        JPanel panel = switch (key) {
            case "browse"        -> new BrowseMoviesPanel(currentUser);
            case "watchedlog"    -> new WatchedLogPanel(currentUser);
            case "rate"          -> new RateMoviePanel(currentUser);
            case "comments"      -> new CommentsPanel(currentUser);
            case "watchlist"     -> new WatchlistPanel(currentUser);
            case "progress"      -> new ProgressPanel(currentUser);
            case "familyratings" -> new FamilyRatingsPanel(currentUser);
            default              -> new BrowseMoviesPanel(currentUser);
        };
        showPanel(panel);
    }

    private void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }
}
