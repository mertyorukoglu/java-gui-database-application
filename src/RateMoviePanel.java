package ui.panels;

import dao.MovieDAO;
import dao.RatingDAO;
import model.Movie;
import model.User;
import model.UserMovieRating;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RateMoviePanel extends JPanel {

    private User currentUser;
    private MovieDAO movieDAO;
    private RatingDAO ratingDAO;
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JSpinner ratingSpinner;
    private JTextArea commentArea;
    private JLabel selectedMovieLbl;
    private int selectedMovieId = -1;

    public RateMoviePanel(User currentUser) {
        this.currentUser = currentUser;
        this.movieDAO = new MovieDAO();
        this.ratingDAO = new RatingDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
        loadMovies();
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("Rate Movies", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(500);
        split.setBackground(UITheme.BG_DARK);
        split.setBorder(null);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(UITheme.BG_DARK);
        leftPanel.add(UITheme.createSectionTitle("Available Movies"), BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Genre", "My Rating"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        movieTable = UITheme.createStyledTable();
        movieTable.setModel(tableModel);
        movieTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        movieTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        movieTable.getColumnModel().getColumn(3).setCellRenderer(new UITheme.StarRatingCellRenderer(true));
        JScrollPane sp = UITheme.createStyledScrollPane(movieTable);
        leftPanel.add(sp, BorderLayout.CENTER);

        movieTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { selectMovie(); }
        });

        JPanel rightPanel = new JPanel(new BorderLayout(0, 12));
        rightPanel.setBackground(UITheme.BG_DARK);
        rightPanel.setBorder(new EmptyBorder(0, 16, 0, 0));
        rightPanel.add(UITheme.createSectionTitle("Submit Your Rating"), BorderLayout.NORTH);

        JPanel form = UITheme.createCardPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        selectedMovieLbl = UITheme.createLabel("Select a movie from the list →", UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        selectedMovieLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ratingLbl = UITheme.createLabel("Your Rating (1-5 stars):", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        ratingLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel spinnerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        spinnerRow.setOpaque(false);
        spinnerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        ratingSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        ratingSpinner.setMaximumSize(new Dimension(70, 32));
        ratingSpinner.setPreferredSize(new Dimension(70, 32));

        final int[] currentRating = {3};
        JPanel starIconPanel = UITheme.createStarRatingPanel(3, 5, 22);
        starIconPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ratingSpinner.addChangeListener(e -> {
            int v = (int) ratingSpinner.getValue();
            currentRating[0] = v;
            starIconPanel.removeAll();
            
            Color filled = new Color(255, 180, 0);
            Color empty  = new Color(80, 80, 100);
            for (int i = 1; i <= 5; i++) {
                starIconPanel.add(new JLabel(UITheme.createStarIcon(22, i <= v ? filled : empty)));
            }
            starIconPanel.revalidate();
            starIconPanel.repaint();
        });
        spinnerRow.add(ratingSpinner);

        JLabel commentLbl = UITheme.createLabel("Your Comment:", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        commentLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        commentArea = UITheme.createStyledTextArea();
        commentArea.setRows(4);
        commentArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane commentSP = new JScrollPane(commentArea);
        commentSP.setAlignmentX(Component.LEFT_ALIGNMENT);
        commentSP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JButton submitBtn = UITheme.createPrimaryButton("⭐  Submit Rating");
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> submitRating());

        form.add(selectedMovieLbl);
        form.add(Box.createVerticalStrut(12));
        form.add(ratingLbl);
        form.add(Box.createVerticalStrut(4));
        form.add(spinnerRow);
        form.add(Box.createVerticalStrut(4));
        form.add(starIconPanel);
        form.add(Box.createVerticalStrut(12));
        form.add(commentLbl);
        form.add(Box.createVerticalStrut(4));
        form.add(commentSP);
        form.add(Box.createVerticalStrut(16));
        form.add(submitBtn);

        rightPanel.add(form, BorderLayout.CENTER);

        split.setLeftComponent(leftPanel);
        split.setRightComponent(rightPanel);
        add(split, BorderLayout.CENTER);
    }

    private void loadMovies() {
        tableModel.setRowCount(0);
        List<Movie> movies = movieDAO.getAllMoviesForChild();
        for (Movie m : movies) {
            UserMovieRating myRating = ratingDAO.getUserRatingForMovie(currentUser.getUserId(), m.getMovieID());
            int ratingVal = myRating != null ? myRating.getRating() : -1;
            tableModel.addRow(new Object[]{m.getMovieID(), m.getTitle(), m.getGenre(), ratingVal});
        }
    }

    private void selectMovie() {
        int row = movieTable.getSelectedRow();
        if (row < 0) return;
        selectedMovieId = (int) tableModel.getValueAt(row, 0);
        String movieTitle = (String) tableModel.getValueAt(row, 1);

        selectedMovieLbl.setText("Selected: " + movieTitle);
        selectedMovieLbl.setForeground(UITheme.ACCENT_PURPLE);

        UserMovieRating existing = ratingDAO.getUserRatingForMovie(currentUser.getUserId(), selectedMovieId);
        if (existing != null) {
            ratingSpinner.setValue(existing.getRating());
            commentArea.setText(existing.getComment());
        } else {
            ratingSpinner.setValue(3);
            commentArea.setText("");
        }
    }

    private void submitRating() {
        if (selectedMovieId < 0) {
            JOptionPane.showMessageDialog(this, "Please select a movie first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int rating = (int) ratingSpinner.getValue();
        String comment = commentArea.getText().trim();
        if (ratingDAO.addOrUpdateRating(currentUser.getUserId(), selectedMovieId, rating, comment)) {
            JOptionPane.showMessageDialog(this, "Rating saved successfully! " + "★".repeat(rating), "Success", JOptionPane.INFORMATION_MESSAGE);
            selectedMovieId = -1;
            selectedMovieLbl.setText("Select a movie from the list →");
            selectedMovieLbl.setForeground(UITheme.TEXT_MUTED);
            commentArea.setText("");
            ratingSpinner.setValue(3);
            loadMovies();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save rating.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
