package ui.panels;

import dao.MovieDAO;
import model.Movie;
import model.User;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewingRulesPanel extends JPanel {

    private User currentUser;
    private MovieDAO movieDAO;
    private JTable movieTable;
    private DefaultTableModel tableModel;

    public ViewingRulesPanel(User currentUser) {
        this.currentUser = currentUser;
        this.movieDAO = new MovieDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
        loadMovies();
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("Set Viewing Rules", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel desc = UITheme.createLabel(
            "Toggle the Parental Restriction flag for movies. Restricted movies are hidden from Children.",
            UITheme.FONT_BODY, UITheme.TEXT_MUTED);

        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(desc, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Genre", "Parental Restriction"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return (c == 3) ? Boolean.class : String.class;
            }
        };
        movieTable = UITheme.createStyledTable();
        movieTable.setModel(tableModel);
        movieTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        movieTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        movieTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        movieTable.getColumnModel().getColumn(3).setCellRenderer(
            new UITheme.StatusCellRenderer("Restricted", "Allowed", UITheme.WARNING, UITheme.SUCCESS));
        JScrollPane sp = UITheme.createStyledScrollPane(movieTable);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton enableBtn  = UITheme.createDangerButton("Enable Restriction");
        JButton disableBtn = UITheme.createSuccessButton("Disable Restriction");
        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");

        enableBtn.addActionListener(e -> setRestriction(true));
        disableBtn.addActionListener(e -> setRestriction(false));
        refreshBtn.addActionListener(e -> loadMovies());

        btnRow.add(enableBtn);
        btnRow.add(disableBtn);
        btnRow.add(refreshBtn);

        JLabel hint = UITheme.createLabel("Select one or multiple movies, then click a button to apply the rule.", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        movieTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(sp, BorderLayout.CENTER);
        center.add(btnRow, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
        add(hint, BorderLayout.SOUTH);
    }

    private void loadMovies() {
        tableModel.setRowCount(0);
        List<Movie> movies = movieDAO.getAllMovies();
        for (Movie m : movies) {
            tableModel.addRow(new Object[]{
                m.getMovieID(), m.getTitle(), m.getGenre(),
                m.isParentalRestriction()    
            });
        }
    }

    private void setRestriction(boolean value) {
        int[] rows = movieTable.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one movie.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int count = 0;
        for (int row : rows) {
            int id = (int) tableModel.getValueAt(row, 0);
            if (movieDAO.setParentalRestriction(id, value)) count++;
        }
        JOptionPane.showMessageDialog(this,
            count + " movie(s) " + (value ? "marked as Restricted." : "marked as Allowed."),
            "Done", JOptionPane.INFORMATION_MESSAGE);
        loadMovies();
    }
}
