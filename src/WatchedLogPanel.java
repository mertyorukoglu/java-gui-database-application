package ui.panels;

import dao.MovieDAO;
import dao.WatchedLogDAO;
import model.Movie;
import model.User;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WatchedLogPanel extends JPanel {

    private User currentUser;
    private MovieDAO movieDAO;
    private WatchedLogDAO watchedLogDAO;
    private JTable movieTable;
    private DefaultTableModel tableModel;

    public WatchedLogPanel(User currentUser) {
        this.currentUser = currentUser;
        this.movieDAO = new MovieDAO();
        this.watchedLogDAO = new WatchedLogDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
        loadMovies();
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("Log Watched Movies", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel desc = UITheme.createLabel(
            "Mark movies as 'Watched' or 'Not Watched'. Select a movie and click the appropriate button.",
            UITheme.FONT_BODY, UITheme.TEXT_MUTED);

        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(desc, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Genre", "Release Year", "Watched by Me"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        movieTable = UITheme.createStyledTable();
        movieTable.setModel(tableModel);
        movieTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        movieTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        movieTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        movieTable.getColumnModel().getColumn(4).setCellRenderer(
            new UITheme.StatusCellRenderer("Yes", "No", UITheme.SUCCESS, UITheme.DANGER)
        );
        JScrollPane sp = UITheme.createStyledScrollPane(movieTable);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton watchedBtn   = UITheme.createSuccessButton("Mark as Watched");
        JButton unwatchedBtn = UITheme.createSecondaryButton("Mark as Not Watched");
        JButton refreshBtn   = UITheme.createSecondaryButton("Refresh");

        watchedBtn.addActionListener(e -> setWatched(true));
        unwatchedBtn.addActionListener(e -> setWatched(false));
        refreshBtn.addActionListener(e -> loadMovies());

        btnRow.add(watchedBtn);
        btnRow.add(unwatchedBtn);
        btnRow.add(refreshBtn);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(sp, BorderLayout.CENTER);
        center.add(btnRow, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JPanel statsBox = UITheme.createCardPanel();
        statsBox.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
        int watchedCount = watchedLogDAO.countWatchedByUser(currentUser.getUserId());
        JLabel watchedCountLbl = UITheme.createLabel("Movies I've Watched: " + watchedCount, UITheme.FONT_HEADER, UITheme.SUCCESS);
        statsBox.add(watchedCountLbl);
        add(statsBox, BorderLayout.SOUTH);
    }

    private void loadMovies() {
        tableModel.setRowCount(0);
        List<Movie> movies = movieDAO.getAllMoviesForChild();
        for (Movie m : movies) {
            boolean myWatched = watchedLogDAO.isWatched(currentUser.getUserId(), m.getMovieID());
            String year = m.getReleaseDate() != null && m.getReleaseDate().length() >= 4
                ? m.getReleaseDate().substring(0, 4) : "N/A";
            tableModel.addRow(new Object[]{
                m.getMovieID(), m.getTitle(), m.getGenre(), year,
                myWatched
            });
        }
    }

    private void setWatched(boolean watched) {
        int row = movieTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a movie first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int movieId = (int) tableModel.getValueAt(row, 0);
        String movieTitle = (String) tableModel.getValueAt(row, 1);
        boolean result;
        if (watched) {
            result = watchedLogDAO.logWatched(currentUser.getUserId(), movieId);
            if (!result) {
                JOptionPane.showMessageDialog(this, "\"" + movieTitle + "\" is already marked as watched.");
                return;
            }
        } else {
            result = watchedLogDAO.removeWatched(currentUser.getUserId(), movieId);
        }
        if (result) {
            loadMovies();
        }
    }
}
