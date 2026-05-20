package ui.panels;

import dao.MovieDAO;
import dao.RatingDAO;
import dao.UserDAO;
import model.Movie;
import model.User;
import model.UserMovieRating;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FamilyRatingsPanel extends JPanel {

    private User currentUser;
    private MovieDAO movieDAO;
    private RatingDAO ratingDAO;
    private UserDAO userDAO;
    private JTable movieTable, ratingsTable;
    private DefaultTableModel movieModel, ratingsModel;

    public FamilyRatingsPanel(User currentUser) {
        this.currentUser = currentUser;
        this.movieDAO = new MovieDAO();
        this.ratingDAO = new RatingDAO();
        this.userDAO = new UserDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
        loadMovies();
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("Family Ratings", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel desc = UITheme.createLabel(
            "See how all family members rated each movie. Select a movie to view its ratings.",
            UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(desc, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(360);
        split.setBackground(UITheme.BG_DARK);
        split.setBorder(null);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(UITheme.BG_DARK);
        leftPanel.add(UITheme.createSectionTitle("Select a Movie"), BorderLayout.NORTH);

        String[] movCols = {"ID", "Title", "Avg. Rating"};
        movieModel = new DefaultTableModel(movCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        movieTable = UITheme.createStyledTable();
        movieTable.setModel(movieModel);
        movieTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        JScrollPane mvSP = UITheme.createStyledScrollPane(movieTable);
        leftPanel.add(mvSP, BorderLayout.CENTER);

        movieTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { loadRatingsForSelectedMovie(); }
        });

        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(UITheme.BG_DARK);
        rightPanel.setBorder(new EmptyBorder(0, 12, 0, 0));
        rightPanel.add(UITheme.createSectionTitle("Family Reviews"), BorderLayout.NORTH);

        String[] ratCols = {"User", "Rating (Stars)", "Comment"};
        ratingsModel = new DefaultTableModel(ratCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return (c == 1) ? Integer.class : String.class;
            }
        };
        ratingsTable = UITheme.createStyledTable();
        ratingsTable.setModel(ratingsModel);
        ratingsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        ratingsTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        ratingsTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        ratingsTable.setRowHeight(36);
        ratingsTable.getColumnModel().getColumn(1).setCellRenderer(new UITheme.StarRatingCellRenderer(true));
        JScrollPane ratSP = UITheme.createStyledScrollPane(ratingsTable);
        rightPanel.add(ratSP, BorderLayout.CENTER);

        split.setLeftComponent(leftPanel);
        split.setRightComponent(rightPanel);
        add(split, BorderLayout.CENTER);
    }

    private void loadMovies() {
        movieModel.setRowCount(0);
        boolean childOnly = currentUser.isChild();
        List<Movie> movies = childOnly ? movieDAO.getAllMoviesForChild() : movieDAO.getAllMovies();

        for (Movie m : movies) {
            List<UserMovieRating> ratings = ratingDAO.getRatingsForMovie(m.getMovieID());
            String avgStr;
            if (ratings.isEmpty()) {
                avgStr = "No ratings";
            } else {
                double avg = ratings.stream().mapToInt(r -> r.getRating()).average().orElse(0);
                avgStr = String.format("%.1f / 5", avg);
            }
            movieModel.addRow(new Object[]{m.getMovieID(), m.getTitle(), avgStr});
        }
    }

    private void loadRatingsForSelectedMovie() {
        int row = movieTable.getSelectedRow();
        if (row < 0) return;
        int movieId = (int) movieModel.getValueAt(row, 0);

        ratingsModel.setRowCount(0);
        List<UserMovieRating> ratings = ratingDAO.getRatingsForMovie(movieId);
        List<User> allUsers = userDAO.getAllUsers();

        for (UserMovieRating r : ratings) {
            String username = "Unknown";
            for (User u : allUsers) {
                if (u.getUserId() == r.getUserID()) { username = u.getUsername(); break; }
            }
            String ratingText = r.getRating() + " / 5";
            ratingsModel.addRow(new Object[]{username, r.getRating(), r.getComment() != null ? r.getComment() : ""});
        }

        if (ratings.isEmpty()) {
            ratingsModel.addRow(new Object[]{"—", "No ratings yet", "Be the first to rate!"});
        }
    }
}
