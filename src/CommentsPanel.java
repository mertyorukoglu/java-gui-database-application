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

public class CommentsPanel extends JPanel {

    private User currentUser;
    private MovieDAO movieDAO;
    private RatingDAO ratingDAO;
    private JTable movieTable;
    private DefaultTableModel movieTableModel;
    private JTextArea commentsArea, myCommentArea;
    private int selectedMovieId = -1;

    public CommentsPanel(User currentUser) {
        this.currentUser = currentUser;
        this.movieDAO = new MovieDAO();
        this.ratingDAO = new RatingDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
        loadMovies();
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("View & Add Comments", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(350);
        mainSplit.setBackground(UITheme.BG_DARK);
        mainSplit.setBorder(null);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(UITheme.BG_DARK);
        leftPanel.add(UITheme.createSectionTitle("Movies"), BorderLayout.NORTH);

        String[] cols = {"ID", "Title"};
        movieTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        movieTable = UITheme.createStyledTable();
        movieTable.setModel(movieTableModel);
        movieTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        JScrollPane sp = UITheme.createStyledScrollPane(movieTable);
        leftPanel.add(sp, BorderLayout.CENTER);

        movieTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { loadCommentsForSelectedMovie(); }
        });

        JPanel rightPanel = new JPanel(new BorderLayout(0, 12));
        rightPanel.setBackground(UITheme.BG_DARK);
        rightPanel.setBorder(new EmptyBorder(0, 16, 0, 0));

        JPanel allCommentsCard = UITheme.createCardPanel();
        allCommentsCard.setLayout(new BorderLayout(0, 8));
        allCommentsCard.add(UITheme.createSectionTitle("All Comments for This Movie"), BorderLayout.NORTH);
        commentsArea = UITheme.createStyledTextArea();
        commentsArea.setEditable(false);
        commentsArea.setText("Select a movie to see comments...");
        JScrollPane commentsSP = UITheme.createStyledScrollPane(commentsArea);
        commentsSP.setPreferredSize(new Dimension(0, 180));
        allCommentsCard.add(commentsSP, BorderLayout.CENTER);

        JPanel myCommentCard = UITheme.createCardPanel();
        myCommentCard.setLayout(new BorderLayout(0, 8));
        myCommentCard.add(UITheme.createSectionTitle("Write Your Review"), BorderLayout.NORTH);
        myCommentArea = UITheme.createStyledTextArea();
        myCommentArea.setRows(4);
        JScrollPane myCommentSP = UITheme.createStyledScrollPane(myCommentArea);
        JButton saveBtn = UITheme.createPrimaryButton("Save Comment");
        saveBtn.addActionListener(e -> saveComment());
        myCommentCard.add(myCommentSP, BorderLayout.CENTER);
        myCommentCard.add(saveBtn, BorderLayout.SOUTH);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, allCommentsCard, myCommentCard);
        rightSplit.setDividerLocation(240);
        rightSplit.setBackground(UITheme.BG_DARK);
        rightSplit.setBorder(null);

        rightPanel.add(rightSplit, BorderLayout.CENTER);

        mainSplit.setLeftComponent(leftPanel);
        mainSplit.setRightComponent(rightPanel);
        add(mainSplit, BorderLayout.CENTER);
    }

    private void loadMovies() {
        movieTableModel.setRowCount(0);
        List<Movie> movies = movieDAO.getAllMoviesForChild();
        for (Movie m : movies) {
            movieTableModel.addRow(new Object[]{m.getMovieID(), m.getTitle()});
        }
    }

    private void loadCommentsForSelectedMovie() {
        int row = movieTable.getSelectedRow();
        if (row < 0) return;
        selectedMovieId = (int) movieTableModel.getValueAt(row, 0);
        String movieTitle = (String) movieTableModel.getValueAt(row, 1);

        List<UserMovieRating> ratings = ratingDAO.getRatingsForMovie(selectedMovieId);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Comments for: ").append(movieTitle).append(" ===\n\n");
        if (ratings.isEmpty()) {
            sb.append("No comments yet. Be the first to review!");
        } else {
            for (UserMovieRating r : ratings) {
                String ratingText = "Rating: " + r.getRating() + "/5";
                sb.append(ratingText).append("  (User ID: ").append(r.getUserID()).append(")\n");
                sb.append(r.getComment() != null && !r.getComment().isEmpty() ? r.getComment() : "(No comment)");
                sb.append("\n----------------\n");
            }
        }
        commentsArea.setText(sb.toString());

        UserMovieRating mine = ratingDAO.getUserRatingForMovie(currentUser.getUserId(), selectedMovieId);
        myCommentArea.setText(mine != null && mine.getComment() != null ? mine.getComment() : "");
    }

    private void saveComment() {
        if (selectedMovieId < 0) {
            JOptionPane.showMessageDialog(this, "Please select a movie first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String comment = myCommentArea.getText().trim();
        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please write a comment before saving.");
            return;
        }
        
        UserMovieRating existing = ratingDAO.getUserRatingForMovie(currentUser.getUserId(), selectedMovieId);
        int rating = existing != null ? existing.getRating() : 3;
        if (ratingDAO.addOrUpdateRating(currentUser.getUserId(), selectedMovieId, rating, comment)) {
            JOptionPane.showMessageDialog(this, "Comment saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadCommentsForSelectedMovie();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save comment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
