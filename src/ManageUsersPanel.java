package ui.panels;

import dao.UserDAO;
import model.User;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {

    private User currentUser;
    private UserDAO userDAO;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField usernameField, emailField, passwordField;
    private JComboBox<String> roleCombo;
    private User selectedUser;

    public ManageUsersPanel(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 16));
        buildUI();
        loadUsers();
    }

    private void buildUI() {
        JLabel title = UITheme.createLabel("Manage User Accounts", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(450);
        split.setBackground(UITheme.BG_DARK);
        split.setBorder(null);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(UITheme.BG_DARK);

        String[] cols = {"ID", "Username", "Role", "Email"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = UITheme.createStyledTable();
        userTable.setModel(tableModel);
        JScrollPane sp = UITheme.createStyledScrollPane(userTable);
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { loadUserIntoForm(); }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setOpaque(false);
        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");
        JButton deleteBtn  = UITheme.createDangerButton("Delete User");
        refreshBtn.addActionListener(e -> loadUsers());
        deleteBtn.addActionListener(e -> deleteSelectedUser());
        btnRow.add(refreshBtn);
        btnRow.add(deleteBtn);

        leftPanel.add(UITheme.createSectionTitle("All Users"), BorderLayout.NORTH);
        leftPanel.add(sp, BorderLayout.CENTER);
        leftPanel.add(btnRow, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(UITheme.BG_DARK);
        rightPanel.setBorder(new EmptyBorder(0, 12, 0, 0));
        rightPanel.add(UITheme.createSectionTitle("Add / Edit User"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = UITheme.createStyledTextField();
        emailField    = UITheme.createStyledTextField();
        passwordField = UITheme.createStyledTextField();
        passwordField.setToolTipText("Leave blank to keep existing password");
        roleCombo = UITheme.createStyledComboBox(new String[]{"Parent/Adult", "Child"});

        String[][] rows = {{"Username *", null}, {"Email", null}, {"Password", null}, {"Role *", null}};
        JComponent[] fields = {usernameField, emailField, passwordField, roleCombo};
        for (int i = 0; i < fields.length; i++) {
            gbc.gridy = i; gbc.gridx = 0; gbc.weightx = 0.3;
            form.add(UITheme.createLabel(rows[i][0] + ":", UITheme.FONT_SMALL, UITheme.TEXT_MUTED), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(fields[i], gbc);
        }

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel resetRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        resetRow.setOpaque(false);
        JLabel resetHint = UITheme.createLabel("Quick reset password:", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        JTextField resetPwField = UITheme.createStyledTextField();
        resetPwField.setPreferredSize(new Dimension(120, 30));
        JButton resetBtn = UITheme.createSecondaryButton("Reset Password");
        resetRow.add(resetHint);
        resetRow.add(resetPwField);
        resetRow.add(resetBtn);
        form.add(resetRow, gbc);

        resetBtn.addActionListener(e -> {
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this, "Select a user first.");
                return;
            }
            String pw = resetPwField.getText().trim();
            if (pw.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter new password."); return; }

            // Mevcut şifreyi doğrula
            JPasswordField currentPwField = new JPasswordField(20);
            int result = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"Enter CURRENT password to confirm:", currentPwField},
                "Confirm Password Reset",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (result != JOptionPane.OK_OPTION) return;
            String enteredCurrent = new String(currentPwField.getPassword()).trim();
            if (!enteredCurrent.equals(selectedUser.getPassword())) {
                JOptionPane.showMessageDialog(this,
                    "Current password is incorrect! Password was NOT reset.",
                    "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userDAO.resetPassword(selectedUser.getUserId(), pw)) {
                selectedUser.setPassword(pw); // yerel nesneyi güncelle
                JOptionPane.showMessageDialog(this, "Password reset for " + selectedUser.getUsername());
                resetPwField.setText("");
            }
        });

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionRow.setOpaque(false);
        JButton clearBtn = UITheme.createSecondaryButton("Clear");
        JButton saveBtn  = UITheme.createPrimaryButton("Save User");
        clearBtn.addActionListener(e -> clearForm());
        saveBtn.addActionListener(e -> saveUser());
        actionRow.add(clearBtn);
        actionRow.add(saveBtn);

        rightPanel.add(form, BorderLayout.CENTER);
        rightPanel.add(actionRow, BorderLayout.SOUTH);

        split.setLeftComponent(leftPanel);
        split.setRightComponent(rightPanel);
        add(split, BorderLayout.CENTER);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getUserId(), u.getUsername(), u.getRoleName(), u.getEmail()
            });
        }
        selectedUser = null;
    }

    private void loadUserIntoForm() {
        int row = userTable.getSelectedRow();
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        List<User> all = userDAO.getAllUsers();
        for (User u : all) {
            if (u.getUserId() == id) { selectedUser = u; break; }
        }
        if (selectedUser == null) return;
        usernameField.setText(selectedUser.getUsername());
        emailField.setText(selectedUser.getEmail());
        passwordField.setText("");
        roleCombo.setSelectedIndex(selectedUser.getUserType() == 1 ? 0 : 1);
    }

    private void saveUser() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int userType = roleCombo.getSelectedIndex() == 0 ? 1 : 2;
        String pw = passwordField.getText().trim();

        if (selectedUser == null) {
            
            if (pw.isEmpty()) { JOptionPane.showMessageDialog(this, "Password is required for new users."); return; }
            User nu = new User(0, username, pw, userType, emailField.getText().trim());
            if (userDAO.addUser(nu)) {
                JOptionPane.showMessageDialog(this, "User created successfully!");
                clearForm(); loadUsers();
            }
        } else {
            // Edit existing user
            selectedUser.setUsername(username);
            selectedUser.setEmail(emailField.getText().trim());
            selectedUser.setUserType(userType);

            if (!pw.isEmpty()) {
                // Require current password confirmation before changing
                JPasswordField currentPwField = new JPasswordField(20);
                int result = JOptionPane.showConfirmDialog(
                    this,
                    new Object[]{"Enter current password to confirm change:", currentPwField},
                    "Confirm Password Change",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (result != JOptionPane.OK_OPTION) return;
                String enteredCurrent = new String(currentPwField.getPassword()).trim();
                if (!enteredCurrent.equals(selectedUser.getPassword())) {
                    JOptionPane.showMessageDialog(this,
                        "Current password is incorrect! Password was not changed.",
                        "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                selectedUser.setPassword(pw);
            }

            if (userDAO.updateUser(selectedUser)) {
                JOptionPane.showMessageDialog(this, "User updated successfully!");
                clearForm(); loadUsers();
            }
        }
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user to delete."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        if (id == currentUser.getUserId()) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String uname = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete user \"" + uname + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && userDAO.deleteUser(id)) {
            JOptionPane.showMessageDialog(this, "User deleted.");
            loadUsers();
        }
    }

    private void clearForm() {
        usernameField.setText(""); emailField.setText(""); passwordField.setText("");
        roleCombo.setSelectedIndex(0); selectedUser = null;
    }
}
