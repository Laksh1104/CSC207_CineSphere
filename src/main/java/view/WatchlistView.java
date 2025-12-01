package view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


import interface_adapter.watchlist.watchlistController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class WatchlistView {
    private final watchlistController controller;

    private void refresh(JPanel panel) {
        panel.removeAll();

        for (String movieUrl : controller.loadPage()) {
            try {
                ImageIcon icon = new ImageIcon(new URL(movieUrl));
                JLabel label = new JLabel(icon);
                panel.add(label);
                panel.revalidate();
                panel.repaint();
            } catch (Exception ignored) {}
        }

    }

    public WatchlistView(watchlistController controller) {
        this.controller = controller;
        JFrame frame = new JFrame("Watchlist");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 800);

            JPanel backgroundPanel = new JPanel();
            backgroundPanel.setBackground(new Color(255, 255, 224));
            backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = getJPanel();

        JPanel watchlistedfilms = new JPanel(new FlowLayout(FlowLayout.CENTER));
            watchlistedfilms.setLayout(new GridLayout(10, 4, 10, 10));
            watchlistedfilms.setBackground(new Color(255, 255, 224));
            watchlistedfilms.setPreferredSize(new Dimension(300, 30));
            watchlistedfilms.setMaximumSize(new Dimension(300, 30));
            watchlistedfilms.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel watchlistFilmTitle = new JLabel("Your Watchlist: ");
            watchlistFilmTitle.setFont(new Font("Open Sans", Font.BOLD, 15));
            watchlistedfilms.add(watchlistFilmTitle);

            JPanel moviePanel = new JPanel();
            moviePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
            moviePanel.setBackground(new Color(255, 255, 224));

            for (String movie : controller.loadPage())
                try {
                    ImageIcon icon = new ImageIcon(new URL(movie));
                    Image scaled = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                    JLabel movieLabel = new JLabel(new ImageIcon(scaled));
                    moviePanel.add(movieLabel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

        JPanel forwardbackbuttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton forwardButton = new JButton("Forward");
        JButton backButton = new JButton("Back");

        forwardButton.addActionListener(e -> {
                controller.next();
                refresh(moviePanel);
            });

        backButton.addActionListener(e -> {
            controller.previous();
            refresh(moviePanel);
        });


        forwardbackbuttons.setLayout(new BoxLayout(forwardbackbuttons, BoxLayout.X_AXIS));
        forwardbackbuttons.add(backButton);
        forwardbackbuttons.add(forwardButton);


        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            backgroundPanel.add(buttonPanel);
            backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            backgroundPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            backgroundPanel.add(watchlistedfilms);
            backgroundPanel.add(moviePanel);
            backgroundPanel.add(forwardbackbuttons);
            frame.add(backgroundPanel);

            frame.setVisible(true);
        }

    @NotNull
    private static JPanel getJPanel() {
        JLabel title = new JLabel("CineSphere", SwingConstants.CENTER);
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setPreferredSize(new Dimension(800, 50));
        buttonPanel.setMaximumSize(new Dimension(800, 50));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel watchlistlabel = new JLabel("Watchlist");
        JButton bookButton = new JButton("Book");
        JButton logoutButton = new JButton("Logout");
        JButton homeButton = new JButton("Home");
        buttonPanel.add(title);
        buttonPanel.add(homeButton);
        buttonPanel.add(watchlistlabel);
        buttonPanel.add(bookButton);
        buttonPanel.add(logoutButton);
        return buttonPanel;
    }
}
