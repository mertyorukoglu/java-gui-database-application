package ui.panels;

import dao.MovieDAO;
import dao.RatingDAO;
import dao.WatchedLogDAO;
import model.Movie;
import model.User;
import model.UserMovieRating;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class FamilyAnalyticsPanel extends JPanel {

    private User currentUser;
    private MovieDAO movieDAO;
    private RatingDAO ratingDAO;
    private WatchedLogDAO watchedLogDAO;

    public FamilyAnalyticsPanel(User currentUser) {
        this.currentUser = currentUser;
        this.movieDAO = new MovieDAO();
        this.ratingDAO = new RatingDAO();
        this.watchedLogDAO = new WatchedLogDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("Family Analytics", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        chartsPanel.setOpaque(false);

        chartsPanel.add(buildMostWatchedChart());
        chartsPanel.add(buildAverageRatingsChart());

        add(chartsPanel, BorderLayout.CENTER);

        JPanel statsPanel = buildStatsPanel();
        add(statsPanel, BorderLayout.SOUTH);
    }

    private JPanel buildMostWatchedChart() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 12));

        JLabel lbl = UITheme.createSectionTitle("Most Watched Movies");
        card.add(lbl, BorderLayout.NORTH);

        Map<Integer, Integer> watchData = watchedLogDAO.getMostWatchedMovies();
        List<Movie> movies = movieDAO.getAllMovies();

        JPanel barPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                setBackground(UITheme.BG_CARD);
                g2.setColor(UITheme.BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (watchData.isEmpty()) {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(UITheme.FONT_BODY);
                    g2.drawString("No watch data yet.", 20, getHeight() / 2);
                    return;
                }

                int maxCount = watchData.values().stream().max(Integer::compareTo).orElse(1);
                int barW = (getWidth() - 40) / Math.min(watchData.size(), 5);
                int i = 0;
                Color[] colors = {UITheme.ACCENT_PURPLE, UITheme.ACCENT_BLUE, UITheme.ACCENT_PINK, UITheme.SUCCESS, UITheme.WARNING};

                for (Map.Entry<Integer, Integer> entry : watchData.entrySet()) {
                    if (i >= 5) break;
                    Movie m = movies.stream().filter(mv -> mv.getMovieID() == entry.getKey()).findFirst().orElse(null);
                    String name = m != null ? (m.getTitle().length() > 10 ? m.getTitle().substring(0, 10) + "…" : m.getTitle()) : "ID:" + entry.getKey();
                    int barH = (int) ((double) entry.getValue() / maxCount * (getHeight() - 60));
                    int x = 20 + i * barW;
                    int y = getHeight() - 30 - barH;

                    g2.setColor(colors[i % colors.length]);
                    g2.fillRoundRect(x + 10, y, barW - 20, barH, 8, 8);

                    g2.setColor(UITheme.TEXT_PRIMARY);
                    g2.setFont(UITheme.FONT_SMALL);
                    g2.drawString(String.valueOf(entry.getValue()), x + barW / 2 - 5, y - 4);
                    g2.drawString(name, x + 5, getHeight() - 10);
                    i++;
                }
            }
        };
        barPanel.setPreferredSize(new Dimension(0, 220));
        card.add(barPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildAverageRatingsChart() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 12));

        JLabel lbl = UITheme.createSectionTitle("Average Movie Ratings");
        card.add(lbl, BorderLayout.NORTH);

        List<Movie> movies = movieDAO.getAllMovies();
        Map<String, Double> avgRatings = new LinkedHashMap<>();
        for (Movie m : movies) {
            var ratings = ratingDAO.getRatingsForMovie(m.getMovieID());
            if (!ratings.isEmpty()) {
                double avg = ratings.stream().mapToInt(r -> r.getRating()).average().orElse(0);
                avgRatings.put(m.getTitle().length() > 14 ? m.getTitle().substring(0, 14) + "…" : m.getTitle(), avg);
            }
        }

        JPanel barPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (avgRatings.isEmpty()) {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(UITheme.FONT_BODY);
                    g2.drawString("No ratings yet.", 20, getHeight() / 2);
                    return;
                }

                int n = Math.min(avgRatings.size(), 5);
                int barW = (getWidth() - 40) / n;
                Color[] colors = {UITheme.ACCENT_BLUE, UITheme.SUCCESS, UITheme.WARNING, UITheme.ACCENT_PINK, UITheme.ACCENT_PURPLE};
                int i = 0;

                for (Map.Entry<String, Double> entry : avgRatings.entrySet()) {
                    if (i >= 5) break;
                    int barH = (int) (entry.getValue() / 5.0 * (getHeight() - 60));
                    int x = 20 + i * barW;
                    int y = getHeight() - 30 - barH;

                    g2.setColor(colors[i % colors.length]);
                    g2.fillRoundRect(x + 10, y, barW - 20, barH, 8, 8);

                    g2.setColor(UITheme.TEXT_PRIMARY);
                    g2.setFont(UITheme.FONT_SMALL);
                    g2.drawString(String.format("%.1f", entry.getValue()), x + barW / 2 - 8, y - 4);
                    g2.drawString(entry.getKey(), x + 5, getHeight() - 10);
                    i++;
                }
            }
        };
        barPanel.setPreferredSize(new Dimension(0, 220));
        card.add(barPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildStatsPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(1, 3, 16, 0));

        int totalMovies = movieDAO.getAllMovies().size();
        int totalWatched = (int) movieDAO.getAllMovies().stream().filter(Movie::isWatched).count();
        List<UserMovieRating> allRatings = ratingDAO.getAllRatings();
        double avgAllRatings = allRatings.isEmpty() ? 0 : allRatings.stream().mapToInt(r -> r.getRating()).average().orElse(0);

        card.add(createStatCard("🎬  Total Movies", String.valueOf(totalMovies), UITheme.ACCENT_PURPLE));
        card.add(createStatCard("✅  Watched Movies", String.valueOf(totalWatched), UITheme.SUCCESS));
        card.add(createStatCard("⭐  Avg. Family Rating", String.format("%.1f / 5", avgAllRatings), UITheme.WARNING));

        return card;
    }

    private JPanel createStatCard(String label, String value, Color accentColor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            new EmptyBorder(12, 16, 12, 16)
        ));
        JLabel lblTitle = UITheme.createLabel(label, UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        JLabel lblValue = UITheme.createLabel(value, new Font("Segoe UI", Font.BOLD, 28), accentColor);
        p.add(lblTitle, BorderLayout.NORTH);
        p.add(lblValue, BorderLayout.CENTER);
        return p;
    }
}
