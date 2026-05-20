package util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class UITheme {
    
    public static final Color BG_DARK       = new Color(15, 17, 26);
    public static final Color BG_CARD       = new Color(25, 28, 45);
    public static final Color BG_PANEL      = new Color(20, 23, 36);
    public static final Color ACCENT_PURPLE = new Color(123, 97, 255);
    public static final Color ACCENT_PINK   = new Color(255, 79, 131);
    public static final Color ACCENT_BLUE   = new Color(56, 189, 248);
    public static final Color TEXT_PRIMARY  = new Color(240, 240, 255);
    public static final Color TEXT_MUTED    = new Color(140, 145, 175);
    public static final Color SUCCESS       = new Color(52, 211, 153);
    public static final Color WARNING       = new Color(251, 191, 36);
    public static final Color DANGER        = new Color(248, 81, 73);
    public static final Color BORDER_COLOR  = new Color(45, 50, 75);

    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    public static void applyGlobalLook() {
        try {
            
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",              BG_DARK);
        UIManager.put("Button.background",             BG_CARD);
        UIManager.put("Button.foreground",             TEXT_PRIMARY);
        UIManager.put("Button.select",                 BG_PANEL);
        UIManager.put("Button.border",                 BorderFactory.createLineBorder(BORDER_COLOR, 1));
        UIManager.put("ToggleButton.background",       BG_CARD);
        UIManager.put("ToggleButton.foreground",       TEXT_PRIMARY);
        UIManager.put("Label.foreground",              TEXT_PRIMARY);
        UIManager.put("TextField.background",          BG_DARK);
        UIManager.put("TextField.foreground",          TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",     TEXT_PRIMARY);
        UIManager.put("TextArea.background",           BG_DARK);
        UIManager.put("TextArea.foreground",           TEXT_PRIMARY);
        UIManager.put("ComboBox.background",           BG_DARK);
        UIManager.put("ComboBox.foreground",           TEXT_PRIMARY);
        UIManager.put("Table.background",              BG_CARD);
        UIManager.put("Table.foreground",              TEXT_PRIMARY);
        UIManager.put("Table.gridColor",               BORDER_COLOR);
        UIManager.put("TableHeader.background",        BG_PANEL);
        UIManager.put("TableHeader.foreground",        TEXT_MUTED);
        UIManager.put("ScrollPane.background",         BG_DARK);
        UIManager.put("Viewport.background",           BG_DARK);
        UIManager.put("OptionPane.background",         BG_CARD);
        UIManager.put("OptionPane.messageForeground",  TEXT_PRIMARY);
        UIManager.put("SplitPane.background",          BG_DARK);
        UIManager.put("SplitPane.dividerSize",         6);
        UIManager.put("CheckBox.background",           BG_DARK);
        UIManager.put("CheckBox.foreground",           TEXT_PRIMARY);
        UIManager.put("Spinner.background",            BG_DARK);
        UIManager.put("Spinner.foreground",            TEXT_PRIMARY);
        UIManager.put("TabbedPane.background",         BG_DARK);
        UIManager.put("TabbedPane.foreground",         TEXT_PRIMARY);
        UIManager.put("ToolTip.background",            BG_CARD);
        UIManager.put("ToolTip.foreground",            TEXT_PRIMARY);
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(ACCENT_PURPLE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        addHoverEffect(btn, ACCENT_PURPLE, ACCENT_PURPLE.darker());
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        addHoverEffect(btn, DANGER, DANGER.darker());
        return btn;
    }

    public static JButton createSuccessButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(SUCCESS);
        btn.setForeground(new Color(10, 30, 20));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        addHoverEffect(btn, SUCCESS, SUCCESS.darker());
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(BG_CARD);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        addHoverEffect(btn, BG_CARD, BORDER_COLOR);
        return btn;
    }

    private static void addHoverEffect(JButton btn, Color normal, Color hover) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(normal); }
        });
    }

    public static JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setBackground(BG_DARK);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setBackground(BG_DARK);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    public static JTextArea createStyledTextArea() {
        JTextArea ta = new JTextArea();
        ta.setFont(FONT_BODY);
        ta.setBackground(BG_DARK);
        ta.setForeground(TEXT_PRIMARY);
        ta.setCaretColor(TEXT_PRIMARY);
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        return ta;
    }

    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_BODY);
        cb.setBackground(BG_DARK);
        cb.setForeground(TEXT_PRIMARY);
        return cb;
    }

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    public static JTable createStyledTable() {
        JTable table = new JTable();
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.getTableHeader().setBackground(BG_PANEL);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setFont(FONT_BUTTON);
        table.setSelectionBackground(ACCENT_PURPLE.darker());
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        return table;
    }

    public static JScrollPane createStyledScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(BG_CARD);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return sp;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    public static void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(BG_DARK);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
    }

    public static JLabel createSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADER);
        lbl.setForeground(ACCENT_PURPLE);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    public static ImageIcon createFilmIcon(int size) {
        BufferedImage img = new BufferedImage(size + 4, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(ACCENT_PURPLE);
        g.fillRoundRect(0, 2, size + 2, size - 4, 4, 4);
        
        g.setColor(BG_DARK);
        int hs = Math.max(2, size / 7);
        int gap = (size - 3 * hs) / 4;
        for (int i = 0; i < 3; i++) {
            int x = gap + i * (hs + gap);
            g.fillRoundRect(x, 2, hs, hs, 1, 1);
            g.fillRoundRect(x, size - 2 - hs, hs, hs, 1, 1);
        }
        g.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon createSearchIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int r = size / 2 - 3;
        g.drawOval(1, 1, r * 2, r * 2);
        int lx1 = 1 + r + (int)(r * 0.65);
        int ly1 = 1 + r + (int)(r * 0.65);
        g.drawLine(lx1, ly1, size - 2, size - 2);
        g.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon createStarIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        int[] xp = new int[10], yp = new int[10];
        double cx = size / 2.0, cy = size / 2.0;
        double outer = size / 2.0 - 1, inner = outer * 0.42;
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2 + i * Math.PI / 5;
            double radius = (i % 2 == 0) ? outer : inner;
            xp[i] = (int) Math.round(cx + radius * Math.cos(angle));
            yp[i] = (int) Math.round(cy + radius * Math.sin(angle));
        }
        g.fillPolygon(xp, yp, 10);
        g.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon createDotIcon(Color color, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillOval(0, 0, size - 1, size - 1);
        g.dispose();
        return new ImageIcon(img);
    }

    public static JPanel createStarRatingPanel(int rating, int maxStars, int starSize) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        row.setOpaque(false);
        Color filled = new Color(255, 180, 0);
        Color empty  = new Color(80, 80, 100);
        for (int i = 1; i <= maxStars; i++) {
            JLabel star = new JLabel(createStarIcon(starSize, i <= rating ? filled : empty));
            row.add(star);
        }
        return row;
    }

    public static class StatusCellRenderer extends DefaultTableCellRenderer {
        private final String trueText, falseText;
        private final Color  trueColor, falseColor;

        public StatusCellRenderer(String trueText, String falseText, Color trueColor, Color falseColor) {
            this.trueText = trueText; this.falseText = falseText;
            this.trueColor = trueColor; this.falseColor = falseColor;
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(FONT_SMALL);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            boolean b = value instanceof Boolean && (Boolean) value;
            setText(b ? trueText : falseText);
            setForeground(isSelected ? Color.WHITE : (b ? trueColor : falseColor));
            setBackground(isSelected ? ACCENT_PURPLE.darker() : BG_CARD);
            return this;
        }
    }

    public static class StarRatingCellRenderer extends DefaultTableCellRenderer {
        private static final Color FILLED = new Color(255, 180, 0);
        private static final Color EMPTY  = new Color(65, 68, 95);
        private final boolean outOf5;

        public StarRatingCellRenderer()           { this.outOf5 = false; }
        public StarRatingCellRenderer(boolean o5) { this.outOf5 = o5; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
            panel.setBackground(isSelected ? ACCENT_PURPLE.darker() : BG_CARD);
            panel.setBorder(new EmptyBorder(0, 4, 0, 0));
            if (value instanceof Integer) {
                int raw = (Integer) value;
                if (raw == -1) {
                    JLabel lbl = new JLabel("Not rated");
                    lbl.setFont(FONT_SMALL);
                    lbl.setForeground(TEXT_MUTED);
                    panel.add(lbl);
                } else {
                    int stars5 = outOf5 ? raw : Math.round(raw / 2.0f);
                    String lbl = outOf5 ? raw + "/5" : raw + "/10";
                    for (int i = 1; i <= 5; i++) {
                        panel.add(new JLabel(createStarIcon(13, i <= stars5 ? FILLED : EMPTY)));
                    }
                    JLabel num = new JLabel(" " + lbl);
                    num.setFont(FONT_SMALL);
                    num.setForeground(TEXT_MUTED);
                    panel.add(num);
                }
            }
            return panel;
        }
    }
}
