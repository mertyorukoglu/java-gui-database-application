package ui.panels;

import dao.UserDAO;
import dao.WatchedLogDAO;
import model.User;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class ProgressPanel extends JPanel {

    private User currentUser;
    private WatchedLogDAO watchedLogDAO;
    private UserDAO userDAO;

    public ProgressPanel(User currentUser) {
        this.currentUser = currentUser;
        this.watchedLogDAO = new WatchedLogDAO();
        this.userDAO = new UserDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("Track Progress", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel desc = UITheme.createLabel(
            "See how many movies each family member (child) has watched.",
            UITheme.FONT_BODY, UITheme.TEXT_MUTED);

        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(desc, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        Map<String, Integer> progressData = watchedLogDAO.getWatchCountPerChild();
        int myCount = watchedLogDAO.countWatchedByUser(currentUser.getUserId());
        int maxCount = progressData.values().stream().max(Integer::compareTo).orElse(1);

        JPanel mainCard = UITheme.createCardPanel();
        mainCard.setLayout(new BorderLayout(0, 16));

        JPanel myStatPanel = new JPanel(new BorderLayout(0, 4));
        myStatPanel.setOpaque(false);
        JLabel myLbl = UITheme.createLabel("My Progress (" + currentUser.getUsername() + ")", UITheme.FONT_HEADER, UITheme.ACCENT_PURPLE);
        JLabel myCount2 = UITheme.createLabel(myCount + " movies watched", new Font("Segoe UI", Font.BOLD, 36), UITheme.TEXT_PRIMARY);
        myStatPanel.add(myLbl, BorderLayout.NORTH);
        myStatPanel.add(myCount2, BorderLayout.CENTER);

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (progressData.isEmpty()) {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(UITheme.FONT_BODY);
                    g2.drawString("No progress data available.", 20, getHeight() / 2);
                    return;
                }

                int n = progressData.size();
                int barW = (getWidth() - 60) / n;
                int maxH = getHeight() - 60;
                Color[] colors = {UITheme.ACCENT_PURPLE, UITheme.ACCENT_BLUE, UITheme.ACCENT_PINK, UITheme.SUCCESS, UITheme.WARNING};

                int i = 0;
                for (Map.Entry<String, Integer> entry : progressData.entrySet()) {
                    int barH = maxCount == 0 ? 0 : (int) ((double) entry.getValue() / maxCount * maxH);
                    int x = 30 + i * barW;
                    int y = getHeight() - 30 - barH;

                    Color barColor = entry.getKey().equals(currentUser.getUsername())
                        ? UITheme.ACCENT_PURPLE : colors[i % colors.length];

                    g2.setColor(barColor);
                    g2.fillRoundRect(x + 10, y, barW - 20, Math.max(barH, 4), 10, 10);

                    if (entry.getKey().equals(currentUser.getUsername())) {
                        g2.setColor(Color.WHITE);
                        g2.setStroke(new BasicStroke(2));
                        g2.drawRoundRect(x + 10, y, barW - 20, Math.max(barH, 4), 10, 10);
                        g2.setStroke(new BasicStroke(1));
                    }

                    g2.setColor(UITheme.TEXT_PRIMARY);
                    g2.setFont(UITheme.FONT_SMALL);
                    g2.drawString(String.valueOf(entry.getValue()), x + barW / 2 - 5, y - 5);
                    String name = entry.getKey().length() > 8 ? entry.getKey().substring(0, 8) : entry.getKey();
                    g2.drawString(name, x + 8, getHeight() - 10);
                    i++;
                }

                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString("(Outlined bar = You)", 10, 15);
            }
        };
        chartPanel.setPreferredSize(new Dimension(0, 280));

        mainCard.add(myStatPanel, BorderLayout.NORTH);
        mainCard.add(chartPanel, BorderLayout.CENTER);

        JPanel leaderboard = new JPanel();
        leaderboard.setOpaque(false);
        leaderboard.setLayout(new BoxLayout(leaderboard, BoxLayout.Y_AXIS));
        JLabel lbTitle = UITheme.createSectionTitle("📊 Sibling Comparison");
        leaderboard.add(lbTitle);

        progressData.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .forEach(entry -> {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(entry.getKey().equals(currentUser.getUsername())
                    ? new Color(40, 30, 70) : UITheme.BG_PANEL);
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

                String you = entry.getKey().equals(currentUser.getUsername()) ? " ← You" : "";
                JLabel nameLbl = UITheme.createLabel(entry.getKey() + you, UITheme.FONT_BODY, UITheme.TEXT_PRIMARY);
                JLabel cntLbl = UITheme.createLabel(entry.getValue() + " movies", UITheme.FONT_BODY, UITheme.ACCENT_BLUE);
                row.add(nameLbl, BorderLayout.WEST);
                row.add(cntLbl, BorderLayout.EAST);
                leaderboard.add(row);
                leaderboard.add(Box.createVerticalStrut(4));
            });

        mainCard.add(leaderboard, BorderLayout.SOUTH);
        add(mainCard, BorderLayout.CENTER);
    }
}
