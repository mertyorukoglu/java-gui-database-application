package ui.panels;

import dao.MovieDAO;
import dao.PersonDAO;
import model.Movie;
import model.Person;
import model.User;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BrowseMoviesPanel extends JPanel {

    private User currentUser;
    private MovieDAO movieDAO;
    private PersonDAO personDAO;
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JTextField genreField, directorField, yearField;

    public BrowseMoviesPanel(User currentUser) {
        this.currentUser = currentUser;
        this.movieDAO = new MovieDAO();
        this.personDAO = new PersonDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
        loadMovies(null, null, null);
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("Browse Movies", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        if (currentUser.isChild()) {
            JLabel note = UITheme.createLabel("  (Age-restricted content is hidden)", UITheme.FONT_SMALL, UITheme.WARNING);
            JPanel tp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            tp.setOpaque(false);
            tp.add(title);
            tp.add(note);
            add(tp, BorderLayout.NORTH);
        } else {
            add(title, BorderLayout.NORTH);
        }

        JPanel searchCard = UITheme.createCardPanel();
        searchCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel genreLbl    = UITheme.createLabel("Genre:",    UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        genreField         = UITheme.createStyledTextField();
        genreField.setPreferredSize(new Dimension(130, 32));

        JLabel dirLbl      = UITheme.createLabel("Director:", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        directorField      = UITheme.createStyledTextField();
        directorField.setPreferredSize(new Dimension(130, 32));

        JLabel yearLbl     = UITheme.createLabel("Year:",     UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        yearField          = UITheme.createStyledTextField();
        yearField.setPreferredSize(new Dimension(80, 32));

        JButton searchBtn = UITheme.createPrimaryButton("Search");
        searchBtn.setIcon(UITheme.createSearchIcon(14));
        searchBtn.setIconTextGap(6);
        JButton clearBtn = UITheme.createSecondaryButton("Clear");

        gbc.gridx = 0; gbc.gridy = 0; searchCard.add(genreLbl, gbc);
        gbc.gridx = 1; searchCard.add(genreField, gbc);
        gbc.gridx = 2; searchCard.add(dirLbl, gbc);
        gbc.gridx = 3; searchCard.add(directorField, gbc);
        gbc.gridx = 4; searchCard.add(yearLbl, gbc);
        gbc.gridx = 5; searchCard.add(yearField, gbc);
        gbc.gridx = 6; gbc.fill = GridBagConstraints.NONE; searchCard.add(searchBtn, gbc);
        gbc.gridx = 7; searchCard.add(clearBtn, gbc);

        searchBtn.addActionListener(e -> loadMovies(genreField.getText(), directorField.getText(), yearField.getText()));
        clearBtn.addActionListener(e -> { genreField.setText(""); directorField.setText(""); yearField.setText(""); loadMovies(null, null, null); });

        String[] cols = {"ID", "Title", "Genre", "Year", "Language", "Country", "Director", "Rating", "Watched", "Restricted"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 7) return Integer.class;
                if (c == 8 || c == 9) return Boolean.class;
                return String.class;
            }
        };
        movieTable = UITheme.createStyledTable();
        movieTable.setModel(tableModel);
        movieTable.setRowHeight(36);

        movieTable.getColumnModel().getColumn(0).setPreferredWidth(35);
        movieTable.getColumnModel().getColumn(1).setPreferredWidth(190);
        movieTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        movieTable.getColumnModel().getColumn(3).setPreferredWidth(55);
        movieTable.getColumnModel().getColumn(6).setPreferredWidth(130);
        movieTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        movieTable.getColumnModel().getColumn(8).setPreferredWidth(75);
        movieTable.getColumnModel().getColumn(9).setPreferredWidth(80);

        movieTable.getColumnModel().getColumn(7).setCellRenderer(new UITheme.StarRatingCellRenderer(true));
        movieTable.getColumnModel().getColumn(8).setCellRenderer(new UITheme.StatusCellRenderer("Watched", "Not Yet", UITheme.SUCCESS, UITheme.TEXT_MUTED));
        movieTable.getColumnModel().getColumn(9).setCellRenderer(new UITheme.StatusCellRenderer("Restricted", "Open", UITheme.WARNING, UITheme.SUCCESS));

        JScrollPane sp = UITheme.createStyledScrollPane(movieTable);

        movieTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) showMovieDetail();
            }
        });

        JPanel galleryInner = new JPanel();
        galleryInner.setBackground(UITheme.BG_DARK);
        JScrollPane gallerySP = new JScrollPane(galleryInner);
        gallerySP.setBackground(UITheme.BG_DARK);
        gallerySP.getViewport().setBackground(UITheme.BG_DARK);
        gallerySP.setBorder(null);
        gallerySP.getVerticalScrollBar().setUnitIncrement(16);

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.add(sp, "table");
        cardPanel.add(gallerySP, "gallery");

        JButton toggleBtn = new JButton("Gallery View");
        toggleBtn.setFont(UITheme.FONT_SMALL);
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setBackground(UITheme.BG_CARD);
        toggleBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            new EmptyBorder(6, 12, 6, 12)));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleBtn.setIcon(UITheme.createFilmIcon(14));
        toggleBtn.setIconTextGap(6);
        toggleBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                toggleBtn.setBackground(new Color(40, 35, 70));
                toggleBtn.setForeground(UITheme.ACCENT_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                toggleBtn.setBackground(UITheme.BG_CARD);
                toggleBtn.setForeground(Color.WHITE);
            }
        });

        final boolean[] galleryMode = {false};
        toggleBtn.addActionListener(e -> {
            galleryMode[0] = !galleryMode[0];
            if (galleryMode[0]) {
                buildGallery(galleryInner);
                cardLayout.show(cardPanel, "gallery");
                toggleBtn.setText("Table View");
            } else {
                cardLayout.show(cardPanel, "table");
                toggleBtn.setText("Gallery View");
            }
        });

        JLabel hint = UITheme.createLabel("Double-click a row to view full details", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchCard, BorderLayout.CENTER);
        searchRow.add(toggleBtn, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(searchRow, BorderLayout.NORTH);
        center.add(cardPanel, BorderLayout.CENTER);
        center.add(hint, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
    }

    private void buildGallery(JPanel galleryPanel) {
        galleryPanel.removeAll();
        int cols = 4;
        galleryPanel.setLayout(new GridLayout(0, cols, 12, 12));
        galleryPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        galleryPanel.setBackground(UITheme.BG_DARK);

        boolean childOnly = currentUser.isChild();
        java.util.List<model.Movie> movies = childOnly
            ? movieDAO.getAllMoviesForChild()
            : movieDAO.getAllMovies();

        for (model.Movie m : movies) {
            JPanel card = new JPanel(new BorderLayout(0, 4));
            card.setBackground(UITheme.BG_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                new EmptyBorder(8, 8, 8, 8)));

            JLabel nameLabel = UITheme.createLabel(m.getTitle(), UITheme.FONT_BUTTON, UITheme.TEXT_PRIMARY);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
            card.add(nameLabel, BorderLayout.NORTH);

            JPanel posterPanel = new JPanel(new BorderLayout());
            posterPanel.setBackground(UITheme.BG_CARD);
            posterPanel.setPreferredSize(new Dimension(140, 200));
            boolean loaded = false;
            String path = m.getPoster();
            if (path != null && !path.trim().isEmpty()) {
                try {
                    java.io.File f = new java.io.File(path.trim());
                    java.net.URL url = f.exists() ? f.toURI().toURL() : new java.net.URL(path.trim());
                    ImageIcon raw = new ImageIcon(url);
                    if (raw.getIconWidth() > 0) {
                        Image scaled = raw.getImage().getScaledInstance(140, 200, Image.SCALE_SMOOTH);
                        posterPanel.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);
                        loaded = true;
                    }
                } catch (Exception ex) { loaded = false; }
            }
            if (!loaded) {
                JLabel icon = new JLabel(UITheme.createFilmIcon(36), SwingConstants.CENTER);
                icon.setHorizontalAlignment(SwingConstants.CENTER);
                posterPanel.add(icon, BorderLayout.CENTER);
            }
            card.add(posterPanel, BorderLayout.CENTER);

            JPanel starRow = UITheme.createStarRatingPanel(m.getRating(), 5, 12);
            starRow.setOpaque(false);
            starRow.setBackground(UITheme.BG_CARD);
            ((FlowLayout) starRow.getLayout()).setAlignment(FlowLayout.CENTER);
            card.add(starRow, BorderLayout.SOUTH);

            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final int movieId = m.getMovieID();
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    
                    for (int row = 0; row < tableModel.getRowCount(); row++) {
                        if ((int) tableModel.getValueAt(row, 0) == movieId) {
                            movieTable.setRowSelectionInterval(row, row);
                            break;
                        }
                    }
                    showMovieDetail();
                }
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    card.setBackground(new Color(40, 35, 70));
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.ACCENT_PURPLE, 2),
                        new EmptyBorder(8, 8, 8, 8)));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    card.setBackground(UITheme.BG_CARD);
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                        new EmptyBorder(8, 8, 8, 8)));
                }
            });

            galleryPanel.add(card);
        }
        galleryPanel.revalidate();
        galleryPanel.repaint();
    }

    private void loadMovies(String genre, String director, String year) {
        tableModel.setRowCount(0);
        boolean childOnly = currentUser.isChild();
        List<Movie> movies;
        if ((genre == null || genre.isEmpty()) && (director == null || director.isEmpty()) && (year == null || year.isEmpty())) {
            movies = childOnly ? movieDAO.getAllMoviesForChild() : movieDAO.getAllMovies();
        } else {
            movies = movieDAO.searchMovies(genre, director, year, childOnly);
        }
        for (Movie m : movies) {
            Person dir = personDAO.getPersonById(m.getDirectorId());
            String dirName = dir != null ? dir.toString() : "N/A";
            String yearStr = m.getReleaseDate() != null && m.getReleaseDate().length() >= 4
                ? m.getReleaseDate().substring(0, 4) : "N/A";
            tableModel.addRow(new Object[]{
                m.getMovieID(),
                m.getTitle(),
                m.getGenre(),
                yearStr,
                m.getLanguage(),
                m.getCountryOfOrigin(),
                dirName,
                m.getRating(),               
                m.isWatched(),               
                m.isParentalRestriction()    
            });
        }
    }

    private void showMovieDetail() {
        int row = movieTable.getSelectedRow();
        if (row < 0) return;
        int movieId = (int) tableModel.getValueAt(row, 0);
        Movie m = movieDAO.getMovieById(movieId);
        if (m == null) return;

        Person dir     = personDAO.getPersonById(m.getDirectorId());
        Person lead    = personDAO.getPersonById(m.getLeadActorId());
        Person support = personDAO.getPersonById(m.getSupportActorId());

        JPanel posterPanel = new JPanel(new BorderLayout());
        posterPanel.setBackground(UITheme.BG_CARD);
        posterPanel.setPreferredSize(new Dimension(180, 260));
        posterPanel.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));

        String posterPath = m.getPoster();
        boolean hasPoster = posterPath != null && !posterPath.trim().isEmpty();
        if (hasPoster) {
            try {
                java.net.URL url;
                java.io.File f = new java.io.File(posterPath.trim());
                url = f.exists() ? f.toURI().toURL() : new java.net.URL(posterPath.trim());
                ImageIcon raw = new ImageIcon(url);
                if (raw.getIconWidth() > 0) {
                    Image scaled = raw.getImage().getScaledInstance(178, 258, Image.SCALE_SMOOTH);
                    posterPanel.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);
                } else hasPoster = false;
            } catch (Exception ex) { hasPoster = false; }
        }
        if (!hasPoster) {
            JLabel filmIcon = new JLabel(UITheme.createFilmIcon(48), SwingConstants.CENTER);
            JLabel noImg    = UITheme.createLabel("No Poster", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
            noImg.setHorizontalAlignment(SwingConstants.CENTER);
            posterPanel.setLayout(new BoxLayout(posterPanel, BoxLayout.Y_AXIS));
            posterPanel.add(Box.createVerticalGlue());
            filmIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            noImg.setAlignmentX(Component.CENTER_ALIGNMENT);
            posterPanel.add(filmIcon);
            posterPanel.add(Box.createVerticalStrut(8));
            posterPanel.add(noImg);
            posterPanel.add(Box.createVerticalGlue());
        }

        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBackground(UITheme.BG_CARD);
        detailPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 4, 3, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int stars5 = m.getRating();  
        JPanel starRow = UITheme.createStarRatingPanel(stars5, 5, 16);
        starRow.setOpaque(false);
        JLabel ratingNum = UITheme.createLabel("  (" + m.getRating() + "/5)", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);

        Object[][] fields = {
            {"Title:",               m.getTitle()},
            {"Genre:",               m.getGenre()},
            {"Release Date:",        m.getReleaseDate()},
            {"Language:",            m.getLanguage()},
            {"Country:",             m.getCountryOfOrigin()},
            {"Director:",            dir     != null ? dir.toString()     : "N/A"},
            {"Lead Actor:",          lead    != null ? lead.toString()    : "N/A"},
            {"Supporting Actor:",    support != null ? support.toString() : "N/A"},
            {"Watched:",             m.isWatched()            ? "Yes" : "No"},
            {"Parental Restriction:", m.isParentalRestriction() ? "Yes" : "No"},
        };

        int r = 0;
        for (Object[] fld : fields) {
            gbc.gridy = r; gbc.gridx = 0; gbc.weightx = 0;
            detailPanel.add(UITheme.createLabel((String) fld[0], UITheme.FONT_BUTTON, UITheme.TEXT_MUTED), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            JLabel val = UITheme.createLabel((String) fld[1], UITheme.FONT_BODY, UITheme.TEXT_PRIMARY);
            if (fld[0].equals("Watched:"))
                val.setForeground(m.isWatched() ? UITheme.SUCCESS : UITheme.TEXT_MUTED);
            if (fld[0].equals("Parental Restriction:"))
                val.setForeground(m.isParentalRestriction() ? UITheme.WARNING : UITheme.SUCCESS);
            detailPanel.add(val, gbc);
            r++;
        }
        
        gbc.gridy = r; gbc.gridx = 0; gbc.weightx = 0;
        detailPanel.add(UITheme.createLabel("Rating:", UITheme.FONT_BUTTON, UITheme.TEXT_MUTED), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ratingRow.setOpaque(false);
        ratingRow.add(starRow);
        ratingRow.add(ratingNum);
        detailPanel.add(ratingRow, gbc);

        JTextArea aboutArea = UITheme.createStyledTextArea();
        aboutArea.setText(m.getAbout());
        aboutArea.setEditable(false);
        aboutArea.setRows(3);
        JScrollPane aboutSP = new JScrollPane(aboutArea);
        aboutSP.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR), "About",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        aboutSP.setBackground(UITheme.BG_CARD);

        JPanel infoColumn = new JPanel(new BorderLayout(0, 10));
        infoColumn.setBackground(UITheme.BG_CARD);
        infoColumn.add(detailPanel, BorderLayout.CENTER);
        infoColumn.add(aboutSP, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 0));
        mainPanel.setBackground(UITheme.BG_CARD);
        mainPanel.add(posterPanel, BorderLayout.WEST);
        mainPanel.add(infoColumn, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, mainPanel, m.getTitle() + " - Details", JOptionPane.PLAIN_MESSAGE);
    }
}
