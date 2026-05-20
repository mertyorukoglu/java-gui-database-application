package ui.panels;

import dao.MovieDAO;
import dao.WatchlistDAO;
import model.Movie;
import model.User;
import model.Watchlist;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WatchlistPanel extends JPanel {

    private User currentUser;
    private MovieDAO movieDAO;
    private WatchlistDAO watchlistDAO;
    private JTable watchlistTable, movieTable;
    private DefaultTableModel watchlistModel, movieModel;

    public WatchlistPanel(User currentUser) {
        this.currentUser = currentUser;
        this.movieDAO = new MovieDAO();
        this.watchlistDAO = new WatchlistDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
        loadWatchlist();
        loadAllMovies();
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("My Watchlist", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(420);
        split.setBackground(UITheme.BG_DARK);
        split.setBorder(null);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(UITheme.BG_DARK);
        leftPanel.add(UITheme.createSectionTitle("🔖 My Watchlist"), BorderLayout.NORTH);

        String[] wlCols = {"Watchlist ID", "Movie Title"};
        watchlistModel = new DefaultTableModel(wlCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        watchlistTable = UITheme.createStyledTable();
        watchlistTable.setModel(watchlistModel);
        JScrollPane wlSP = UITheme.createStyledScrollPane(watchlistTable);

        JPanel wlBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        wlBtnRow.setOpaque(false);
        JButton removeBtn = UITheme.createDangerButton("Remove from Watchlist");
        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");
        removeBtn.addActionListener(e -> removeFromWatchlist());
        refreshBtn.addActionListener(e -> loadWatchlist());
        wlBtnRow.add(removeBtn);
        wlBtnRow.add(refreshBtn);

        leftPanel.add(wlSP, BorderLayout.CENTER);
        leftPanel.add(wlBtnRow, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(UITheme.BG_DARK);
        rightPanel.setBorder(new EmptyBorder(0, 12, 0, 0));
        rightPanel.add(UITheme.createSectionTitle("Browse & Add to Watchlist"), BorderLayout.NORTH);

        String[] mvCols = {"ID", "Title", "Genre"};
        movieModel = new DefaultTableModel(mvCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        movieTable = UITheme.createStyledTable();
        movieTable.setModel(movieModel);
        movieTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        JScrollPane mvSP = UITheme.createStyledScrollPane(movieTable);

        JButton addBtn = UITheme.createSuccessButton("➕  Add to Watchlist");
        addBtn.addActionListener(e -> addToWatchlist());

        rightPanel.add(mvSP, BorderLayout.CENTER);
        rightPanel.add(addBtn, BorderLayout.SOUTH);

        split.setLeftComponent(leftPanel);
        split.setRightComponent(rightPanel);
        add(split, BorderLayout.CENTER);
    }

    private void loadWatchlist() {
        watchlistModel.setRowCount(0);
        List<Watchlist> wl = watchlistDAO.getWatchlistForUser(currentUser.getUserId());
        for (Watchlist w : wl) {
            watchlistModel.addRow(new Object[]{w.getWatchlistID(), w.getMovieTitle()});
        }
    }

    private void loadAllMovies() {
        movieModel.setRowCount(0);
        List<Movie> movies = movieDAO.getAllMoviesForChild();
        for (Movie m : movies) {
            movieModel.addRow(new Object[]{m.getMovieID(), m.getTitle(), m.getGenre()});
        }
    }

    private void addToWatchlist() {
        int row = movieTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a movie to add.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int movieId = (int) movieModel.getValueAt(row, 0);
        String movieTitle = (String) movieModel.getValueAt(row, 1);
        if (watchlistDAO.addToWatchlist(currentUser.getUserId(), movieId)) {
            JOptionPane.showMessageDialog(this, "\"" + movieTitle + "\" added to your watchlist!");
            loadWatchlist();
        } else {
            JOptionPane.showMessageDialog(this, "\"" + movieTitle + "\" is already in your watchlist.", "Already Added", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeFromWatchlist() {
        int row = watchlistTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int watchlistId = (int) watchlistModel.getValueAt(row, 0);
        String movieTitle = (String) watchlistModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove \"" + movieTitle + "\" from your watchlist?",
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && watchlistDAO.removeFromWatchlist(watchlistId)) {
            loadWatchlist();
        }
    }
}
