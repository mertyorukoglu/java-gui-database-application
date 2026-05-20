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

public class ModerateContentPanel extends JPanel {

    private User currentUser;
    private RatingDAO ratingDAO;
    private MovieDAO movieDAO;
    private UserDAO userDAO;
    private JTable ratingsTable;
    private DefaultTableModel tableModel;

    public ModerateContentPanel(User currentUser) {
        this.currentUser = currentUser;
        this.ratingDAO = new RatingDAO();
        this.movieDAO = new MovieDAO();
        this.userDAO = new UserDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
        loadRatings();
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("Moderate Content", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel desc  = UITheme.createLabel(
            "Review all user ratings and comments. Select an entry and click 'Remove' to moderate it.",
            UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(desc, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Rating ID", "Movie", "User", "Rating", "Comment"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return (c == 3) ? Integer.class : String.class;
            }
        };
        ratingsTable = UITheme.createStyledTable();
        ratingsTable.setModel(tableModel);
        ratingsTable.setRowHeight(36);
        ratingsTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        ratingsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        ratingsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        ratingsTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        ratingsTable.getColumnModel().getColumn(4).setPreferredWidth(300);
        ratingsTable.getColumnModel().getColumn(3).setCellRenderer(new UITheme.StarRatingCellRenderer(true));

        JScrollPane sp = UITheme.createStyledScrollPane(ratingsTable);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton removeBtn  = UITheme.createDangerButton("Remove Selected");
        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");

        removeBtn.addActionListener(e -> removeSelectedRating());
        refreshBtn.addActionListener(e -> loadRatings());

        btnRow.add(removeBtn);
        btnRow.add(refreshBtn);

        add(sp, BorderLayout.CENTER);
        add(btnRow, BorderLayout.SOUTH);
    }

    private void loadRatings() {
        tableModel.setRowCount(0);
        List<UserMovieRating> ratings = ratingDAO.getAllRatings();
        for (UserMovieRating r : ratings) {
            Movie movie = movieDAO.getMovieById(r.getMovieID());
            User user   = null;
            List<User> allUsers = userDAO.getAllUsers();
            for (User u : allUsers) {
                if (u.getUserId() == r.getUserID()) { user = u; break; }
            }
            String stars = "★".repeat(r.getRating()) + "☆".repeat(Math.max(0, 5 - r.getRating()));
            tableModel.addRow(new Object[]{
                r.getRatingID(),
                movie != null ? movie.getTitle() : "ID:" + r.getMovieID(),
                user  != null ? user.getUsername()  : "ID:" + r.getUserID(),
                r.getRating(),               
                r.getComment() != null ? r.getComment() : ""
            });
        }
    }

    private void removeSelectedRating() {
        int row = ratingsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a rating/comment to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ratingId = (int) tableModel.getValueAt(row, 0);
        String movieName = (String) tableModel.getValueAt(row, 1);
        String userName  = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove rating by \"" + userName + "\" for \"" + movieName + "\"?",
            "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (ratingDAO.deleteRating(ratingId)) {
                JOptionPane.showMessageDialog(this, "Rating removed successfully.");
                loadRatings();
            }
        }
    }
}
