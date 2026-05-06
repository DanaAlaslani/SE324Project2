package gui;

import auth.SessionManager;
import gui.panels.*;
import gui.util.AppColors;
import gui.util.AppFonts;
import gui.util.UIHelper;
import user.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final int SIDEBAR_W = 210;

    private JPanel    contentArea;
    private CardLayout cards;
    private JButton   activeBtn;
    private JLabel    pageTitleLabel;

    public MainFrame() {
        User user = SessionManager.getCurrentUser();
        setTitle("Gymanice – " + user.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(960, 620));
        buildUI(user.getRole());
    }

    private void buildUI(String role) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppColors.BG_PRIMARY);
        root.add(buildTopBar(),        BorderLayout.NORTH);
        root.add(buildSidebar(role),   BorderLayout.WEST);
        root.add(buildContent(role),   BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Top bar ───────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.SIDEBAR);
        bar.setBorder(new MatteBorder(0, 0, 1, 0, AppColors.BORDER));
        bar.setPreferredSize(new Dimension(0, 52));

        // Left: app name
        JLabel appName = UIHelper.label("  GYMANICE", AppFonts.SUBHEADING, AppColors.ACCENT);
        appName.setBorder(new EmptyBorder(0, 16, 0, 0));

        // Center: current page title
        pageTitleLabel = UIHelper.label("Profile", AppFonts.BODY, AppColors.TEXT_MUTED);
        pageTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Right: user info + logout
        User user = SessionManager.getCurrentUser();
        Color roleColor = switch (user.getRole()) {
            case "TRAINER"      -> AppColors.WARNING;
            case "NUTRITIONIST" -> AppColors.SUCCESS;
            default             -> AppColors.ACCENT;
        };

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(AppColors.SIDEBAR);

        JLabel nameLbl = UIHelper.label(user.getName(), AppFonts.BODY, AppColors.TEXT_PRIMARY);
        JLabel roleLbl = UIHelper.badge(user.getRole(), roleColor);

        JButton logoutBtn = UIHelper.dangerButton("Logout");
        logoutBtn.setFont(AppFonts.SMALL);
        logoutBtn.setBorder(new EmptyBorder(5, 12, 5, 12));
        logoutBtn.addActionListener(e -> logout());

        right.add(nameLbl);
        right.add(roleLbl);
        right.add(logoutBtn);

        bar.add(appName,       BorderLayout.WEST);
        bar.add(pageTitleLabel, BorderLayout.CENTER);
        bar.add(right,         BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar ───────────────────────────────────────────────

    private JPanel buildSidebar(String role) {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        sidebar.setBackground(AppColors.SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, AppColors.BORDER));

        sidebar.add(Box.createVerticalStrut(20));

        // User avatar area
        User user = SessionManager.getCurrentUser();
        JPanel avatarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        avatarRow.setBackground(AppColors.SIDEBAR);
        avatarRow.setMaximumSize(new Dimension(SIDEBAR_W, 56));

        JLabel avatar = new JLabel(String.valueOf(user.getName().charAt(0)).toUpperCase());
        avatar.setFont(AppFonts.HEADING);
        avatar.setForeground(AppColors.TEXT_DARK);
        avatar.setBackground(AppColors.ACCENT);
        avatar.setOpaque(true);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(38, 38));

        JPanel nameBlock = new JPanel();
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setBackground(AppColors.SIDEBAR);
        JLabel nameLbl = UIHelper.label(user.getName(), AppFonts.LABEL, AppColors.TEXT_PRIMARY);
        JLabel roleLbl = UIHelper.label(user.getRole(), AppFonts.SMALL, AppColors.TEXT_MUTED);
        nameBlock.add(nameLbl);
        nameBlock.add(roleLbl);

        avatarRow.add(avatar);
        avatarRow.add(nameBlock);
        sidebar.add(avatarRow);
        sidebar.add(Box.createVerticalStrut(16));
        sidebar.add(sidebarDivider());
        sidebar.add(Box.createVerticalStrut(8));

        String[][] items = navItems(role);
        for (String[] item : items) {
            JButton btn = navButton(item[0], item[1]);
            sidebar.add(btn);
            btn.addActionListener(e -> switchPanel(item[0], item[1], btn));
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private String[][] navItems(String role) {
        return switch (role) {
            case "TRAINER"      -> new String[][]{
                {"Profile",          "My Profile"},
                {"My Schedules",     "Workout Schedules"},
                {"Payments Received","Payments"}
            };
            case "NUTRITIONIST" -> new String[][]{
                {"Profile",   "My Profile"},
                {"Meal Plans","Meal Plans"}
            };
            default             -> new String[][]{
                {"Profile",   "My Profile"},
                {"Workout",   "Workout Schedule"},
                {"Nutrition", "Nutrition & Meals"},
                {"Progress",  "Progress Tracker"},
                {"Payment",   "Payments"}
            };
        };
    }

    private JButton navButton(String panelKey, String displayText) {
        JButton btn = new JButton(displayText);
        btn.setFont(AppFonts.NAV);
        btn.setForeground(AppColors.TEXT_MUTED);
        btn.setBackground(AppColors.SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(11, 22, 11, 22));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(SIDEBAR_W, 44));
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeBtn) btn.setBackground(AppColors.SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeBtn) btn.setBackground(AppColors.SIDEBAR);
            }
        });
        return btn;
    }

    private JSeparator sidebarDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppColors.BORDER);
        sep.setMaximumSize(new Dimension(SIDEBAR_W, 1));
        return sep;
    }

    // ── Content ───────────────────────────────────────────────

    private JPanel buildContent(String role) {
        cards       = new CardLayout();
        contentArea = new JPanel(cards);
        contentArea.setBackground(AppColors.BG_PRIMARY);

        switch (role) {
            case "TRAINER" -> {
                contentArea.add(new ProfilePanel(),           "Profile");
                contentArea.add(new TrainerSchedulesPanel(),  "My Schedules");
                contentArea.add(new PaymentPanel(),           "Payments Received");
            }
            case "NUTRITIONIST" -> {
                contentArea.add(new ProfilePanel(),   "Profile");
                contentArea.add(new NutritionPanel(), "Meal Plans");
            }
            default -> {
                contentArea.add(new ProfilePanel(),   "Profile");
                contentArea.add(new WorkoutPanel(),   "Workout");
                contentArea.add(new NutritionPanel(), "Nutrition");
                contentArea.add(new ProgressPanel(),  "Progress");
                contentArea.add(new PaymentPanel(),   "Payment");
            }
        }

        cards.show(contentArea, "Profile");
        return contentArea;
    }

    private void switchPanel(String cardKey, String displayText, JButton btn) {
        cards.show(contentArea, cardKey);
        pageTitleLabel.setText(displayText);
        if (activeBtn != null) {
            activeBtn.setBackground(AppColors.SIDEBAR);
            activeBtn.setForeground(AppColors.TEXT_MUTED);
            activeBtn.setFont(AppFonts.NAV);
        }
        activeBtn = btn;
        btn.setBackground(AppColors.SIDEBAR_ACTIVE);
        btn.setForeground(AppColors.ACCENT);
        btn.setFont(AppFonts.NAV.deriveFont(Font.BOLD));
    }

    private void logout() {
        if (UIHelper.confirm(this, "Are you sure you want to logout?")) {
            SessionManager.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
