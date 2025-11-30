package view.components;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SeatSelectionPanel extends JPanel {
    private static final String[] ROWS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private static final int COLS = 15;

    private static final Color AVAILABLE_COLOR = new Color(0xEEEEEE);
    private static final Color UNAVAILABLE_COLOR = new Color(0xBDBDBD);
    private static final Color SELECTED_COLOR = new Color(0x2F7ED8);
    private static final Color BORDER_COLOR = new Color(0x999999);
    private final Color COLOR = new Color(255, 255, 224);

    private final Set<String> selectedSeats = new HashSet<>();
    private final Map<String, JToggleButton> seatButtons = new HashMap<>();
    private final JLabel selectedSeatsLabel;

    public SeatSelectionPanel(Set<String> unavailableSeats) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(COLOR);

        JPanel grid = createSeatGrid(unavailableSeats);
        grid.setBackground(COLOR);
        // Left align both components
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(grid);
        add(Box.createVerticalStrut(10)); // 10px gap


        selectedSeatsLabel = new JLabel("Selected Seats: ");
        selectedSeatsLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        add(selectedSeatsLabel);
    }


    private JPanel createSeatGrid(Set<String> unavailableSeats) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        for (int r = 0; r < ROWS.length; r++) {
            gbc.gridx = 0;
            gbc.gridy = r;
            panel.add(new JLabel(ROWS[r]), gbc);

            for (int c = 1; c <= COLS; c++) {
                gbc.gridx = c;
                gbc.gridy = r;
                String seatName = ROWS[r] + c;
                boolean unavailable = unavailableSeats.contains(seatName);

                JToggleButton btn = new JToggleButton(String.valueOf(c));
                btn.setPreferredSize(new Dimension(48, 34));
                btn.setFocusPainted(false);
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
                btn.setBackground(unavailable ? UNAVAILABLE_COLOR : AVAILABLE_COLOR);
                btn.setEnabled(!unavailable);

                if (!unavailable) {
                    btn.addActionListener(e -> {
                        if (btn.isSelected()) {
                            btn.setBackground(SELECTED_COLOR);
                            btn.setForeground(Color.WHITE);
                            selectedSeats.add(seatName);
                        } else {
                            btn.setBackground(AVAILABLE_COLOR);
                            btn.setForeground(Color.BLACK);
                            selectedSeats.remove(seatName);
                        }
                        updateSelectedSeatsLabel();
                    });
                }

                seatButtons.put(seatName, btn);
                panel.add(btn, gbc);
            }
        }

        return panel;
    }

    private void updateSelectedSeatsLabel() {
        if (selectedSeats.isEmpty()) {
            selectedSeatsLabel.setText("Selected Seats: None");
        } else {
            List<String> sortedSeats = new ArrayList<>(selectedSeats);
            Collections.sort(sortedSeats);
            selectedSeatsLabel.setText("Selected Seats: " + String.join(", ", sortedSeats));
        }
    }

    public Set<String> getSelectedSeats() {
        return selectedSeats;
    }

    public void markSeatsAsUnavailable(Set<String> bookedSeats) {
        for (String seat : bookedSeats) {
            JToggleButton btn = seatButtons.get(seat);
            if (btn != null) {
                btn.setSelected(false);
                btn.setEnabled(false);
                btn.setBackground(UNAVAILABLE_COLOR);
                selectedSeats.remove(seat);
            }
        }
        updateSelectedSeatsLabel();
    }
}