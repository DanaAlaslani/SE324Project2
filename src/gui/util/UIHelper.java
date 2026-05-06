package gui.util;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class UIHelper {

    private UIHelper() {}

    // ── Buttons ──────────────────────────────────────────────

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AppFonts.BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(AppColors.ACCENT);
        btn.setBorder(new EmptyBorder(11, 24, 11, 24));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(AppColors.ACCENT_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(AppColors.ACCENT);
            }
        });
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AppFonts.BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(AppColors.DANGER);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AppFonts.BUTTON);
        btn.setForeground(AppColors.ACCENT);
        btn.setBackground(new Color(56, 189, 248, 20));
        btn.setBorder(new CompoundBorder(
                new LineBorder(AppColors.ACCENT, 1),
                new EmptyBorder(10, 22, 10, 22)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(56, 189, 248, 40));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(56, 189, 248, 20));
            }
        });
        return btn;
    }

    public static JButton linkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AppFonts.SMALL);
        btn.setForeground(AppColors.ACCENT);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setBorder(new EmptyBorder(2, 0, 2, 0));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        return btn;
    }

    // ── Labels ────────────────────────────────────────────────

    public static JLabel label(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    public static JLabel heading(String text) {
        return label(text, AppFonts.HEADING, AppColors.TEXT_PRIMARY);
    }

    public static JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppFonts.LABEL);
        lbl.setForeground(AppColors.TEXT_LABEL);
        return lbl;
    }

    public static JLabel muted(String text) {
        return label(text, AppFonts.SMALL, AppColors.TEXT_MUTED);
    }

    public static JLabel badge(String text, Color bg) {
        JLabel lbl = new JLabel("  " + text + "  ");
        lbl.setFont(AppFonts.SMALL);
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(new EmptyBorder(3, 8, 3, 8));
        return lbl;
    }

    // ── Inputs ────────────────────────────────────────────────

    public static JTextField inputField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(AppFonts.INPUT);
        field.setForeground(AppColors.TEXT_PRIMARY);
        field.setBackground(AppColors.INPUT_BG);
        field.setCaretColor(AppColors.ACCENT);
        applyFieldBorder(field, false);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { applyFieldBorder(field, true); }
            public void focusLost(FocusEvent e)   { applyFieldBorder(field, false); }
        });
        return field;
    }

    public static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(AppFonts.INPUT);
        field.setForeground(AppColors.TEXT_PRIMARY);
        field.setBackground(AppColors.INPUT_BG);
        field.setCaretColor(AppColors.ACCENT);
        applyFieldBorder(field, false);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { applyFieldBorder(field, true); }
            public void focusLost(FocusEvent e)   { applyFieldBorder(field, false); }
        });
        return field;
    }

    private static void applyFieldBorder(JComponent field, boolean focused) {
        Color borderColor = focused ? AppColors.ACCENT : AppColors.BORDER;
        field.setBorder(new CompoundBorder(
                new LineBorder(borderColor, 1),
                new EmptyBorder(9, 13, 9, 13)));
    }

    // ── ComboBox with proper dark-theme renderer ───────────────

    public static JComboBox<String> comboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(AppFonts.INPUT);
        box.setBackground(AppColors.INPUT_BG);
        box.setForeground(AppColors.TEXT_PRIMARY);
        box.setBorder(new LineBorder(AppColors.BORDER, 1));
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                lbl.setFont(AppFonts.INPUT);
                lbl.setForeground(AppColors.TEXT_PRIMARY);
                lbl.setBackground(isSelected ? AppColors.ACCENT_DARK : AppColors.INPUT_BG);
                lbl.setBorder(new EmptyBorder(8, 14, 8, 14));
                return lbl;
            }
        });
        // Fix the popup list colors
        Object child = box.getAccessibleContext().getAccessibleChild(0);
        if (child instanceof JPopupMenu popup) {
            JScrollPane sp = (JScrollPane) popup.getComponent(0);
            sp.getViewport().getView().setBackground(AppColors.INPUT_BG);
        }
        return box;
    }

    // ── Table ─────────────────────────────────────────────────

    public static JTable styledTable(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? AppColors.BG_CARD : AppColors.ROW_ALT);
                } else {
                    c.setBackground(AppColors.ACCENT_DARK);
                }
                c.setForeground(AppColors.TEXT_PRIMARY);
                return c;
            }
        };
        table.setFont(AppFonts.TABLE);
        table.setForeground(AppColors.TEXT_PRIMARY);
        table.setBackground(AppColors.BG_CARD);
        table.setGridColor(AppColors.BORDER);
        table.setRowHeight(34);
        table.setSelectionBackground(AppColors.ACCENT_DARK);
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(AppFonts.TABLE_HDR);
        header.setForeground(AppColors.ACCENT);
        header.setBackground(new Color(10, 15, 30));
        header.setBorder(new MatteBorder(0, 0, 2, 0, AppColors.ACCENT));
        header.setReorderingAllowed(false);
        return table;
    }

    // ── Panels ────────────────────────────────────────────────

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(AppColors.BG_CARD);
        p.setBorder(new CompoundBorder(
                new LineBorder(AppColors.BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)));
        return p;
    }

    public static JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(AppColors.BG_PRIMARY);
        return p;
    }

    public static JPanel sectionHeader(String title) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(AppColors.BG_PRIMARY);
        p.setBorder(new MatteBorder(0, 0, 2, 0, AppColors.ACCENT));
        p.add(label("  " + title, AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY));
        return p;
    }

    // ── Scroll pane ───────────────────────────────────────────

    public static JScrollPane scrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBackground(AppColors.BG_CARD);
        sp.getViewport().setBackground(AppColors.BG_CARD);
        sp.setBorder(new LineBorder(AppColors.BORDER, 1));
        sp.getVerticalScrollBar().setBackground(AppColors.BG_PRIMARY);
        sp.getHorizontalScrollBar().setBackground(AppColors.BG_PRIMARY);
        return sp;
    }

    // ── Stat card ─────────────────────────────────────────────

    public static JPanel statCard(String title, String value, Color valueColor) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.BG_CARD);
        p.setBorder(new CompoundBorder(
                new LineBorder(AppColors.BORDER, 1),
                new EmptyBorder(14, 18, 14, 18)));
        JLabel t = label(title, AppFonts.SMALL, AppColors.TEXT_MUTED);
        JLabel v = label(value, AppFonts.HEADING, valueColor);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(t);
        p.add(Box.createVerticalStrut(6));
        p.add(v);
        return p;
    }

    // ── Dialogs ───────────────────────────────────────────────

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ── Separator ─────────────────────────────────────────────

    public static JSeparator separator() {
        JSeparator s = new JSeparator();
        s.setForeground(AppColors.BORDER);
        s.setBackground(AppColors.BORDER);
        return s;
    }
}
