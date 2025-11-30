package view.components;

import com.toedter.calendar.JYearChooser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class FilterPanel extends JPanel {

    public interface SearchHandler { void onSearch(String query); }

    private final Color COLOR = new Color(255, 255, 224);

    private final JYearChooser yearChooser;
    private final RangeSlider ratingSlider; // 0..10 stored as 0..100
    private final JLabel ratingLabel;
    private final JComboBox<String> genreDropdown;
    private final JButton filterButton;

    private final JTextField searchField;
    private final JButton searchButton;

    private Runnable onFilter = () -> {};
    private SearchHandler onSearch = q -> {};

    private static final int SEARCH_FIELD_PREF_W = 260;
    private static final int SEARCH_FIELD_MAX_W  = 360;

    public FilterPanel() {
        super(new GridBagLayout());
        setBackground(COLOR);

        setPreferredSize(new Dimension(0, 54));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        // base constraints for normal components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        int x = 0;

        addGlue(x++);

        addAt(new JLabel("Browse by:"), gbc, x++);

        yearChooser = new JYearChooser();
        yearChooser.setStartYear(1900);
        yearChooser.setEndYear(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + 1);
        lockYearChooserTyping(yearChooser);
        yearChooser.setPreferredSize(new Dimension(62, 28));
        addAt(yearChooser, gbc, x++);

        ratingLabel = new JLabel("Rating: Any");
        addAt(ratingLabel, gbc, x++);

        ratingSlider = new RangeSlider(0, 100);
        ratingSlider.setLowValue(0);
        ratingSlider.setHighValue(100);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        ratingSlider.setMajorTickSpacing(10);
        ratingSlider.setMinorTickSpacing(5);
        ratingSlider.setLabelTable(buildRatingLabels());
        ratingSlider.addChangeListener(e -> updateRatingLabel());
        ratingSlider.setPreferredSize(new Dimension(220, 44));
        ratingSlider.setMinimumSize(new Dimension(220, 44));
        updateRatingLabel();
        addAt(ratingSlider, gbc, x++);

        genreDropdown = new JComboBox<>(new String[]{"All Genres"});
        genreDropdown.setPreferredSize(new Dimension(130, 28));
        addAt(genreDropdown, gbc, x++);

        filterButton = new JButton("Filter");
        filterButton.setPreferredSize(new Dimension(72, 28));
        filterButton.addActionListener(e -> onFilter.run());
        addAt(filterButton, gbc, x++);

        addAt(new JLabel("Find a Film:"), gbc, x++);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(SEARCH_FIELD_PREF_W, 28));
        searchField.setMinimumSize(new Dimension(180, 28));
        searchField.setMaximumSize(new Dimension(SEARCH_FIELD_MAX_W, 28));
        searchField.addActionListener(e -> triggerSearch());
        addAt(searchField, gbc, x++);

        searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(82, 28));
        searchButton.addActionListener(e -> triggerSearch());
        addAt(searchButton, gbc, x++);

        addGlue(x);
    }

    private void addGlue(int gridx) {
        GridBagConstraints glue = new GridBagConstraints();
        glue.gridx = gridx;
        glue.gridy = 0;
        glue.weightx = 1.0;
        glue.fill = GridBagConstraints.HORIZONTAL;
        add(Box.createHorizontalGlue(), glue);
    }

    private void addAt(Component c, GridBagConstraints base, int gridx) {
        GridBagConstraints copy = (GridBagConstraints) base.clone();
        copy.gridx = gridx;
        add(c, copy);
    }

    // ---- wiring
    public void setOnFilter(Runnable onFilter) {
        this.onFilter = (onFilter == null) ? () -> {} : onFilter;
    }
    public void setOnSearch(SearchHandler onSearch) {
        this.onSearch = (onSearch == null) ? (q) -> {} : onSearch;
    }

    private void triggerSearch() {
        String q = getSearchQuery();
        if (q.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a film name.", "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        onSearch.onSearch(q);
    }

    // ---- getters
    public int getSelectedYear() { return yearChooser.getYear(); }

    public String getSelectedGenre() {
        Object sel = genreDropdown.getSelectedItem();
        return sel == null ? "All Genres" : sel.toString();
    }

    public String getRatingString() {
        if (ratingSlider.getLowValue() == 0 && ratingSlider.getHighValue() == 100) return "All Ratings";
        double lo = ratingSlider.getLowValue() / 10.0;
        double hi = ratingSlider.getHighValue() / 10.0;
        return String.format("%.1f-%.1f", lo, hi);
    }

    public String getSearchQuery() {
        String s = searchField.getText();
        return (s == null) ? "" : s.trim();
    }

    public void setGenres(List<String> genres) {
        List<String> list = new ArrayList<>();
        list.add("All Genres");
        if (genres != null) {
            List<String> copy = new ArrayList<>(genres);
            copy.removeIf(g -> g == null || g.isBlank());
            Collections.sort(copy, String.CASE_INSENSITIVE_ORDER);
            list.addAll(copy);
        }
        String prev = getSelectedGenre();
        genreDropdown.setModel(new DefaultComboBoxModel<>(list.toArray(new String[0])));
        genreDropdown.setSelectedItem(list.contains(prev) ? prev : "All Genres");
    }

    private void updateRatingLabel() {
        if (ratingSlider.getLowValue() == 0 && ratingSlider.getHighValue() == 100) {
            ratingLabel.setText("Rating: Any");
        } else {
            double lo = ratingSlider.getLowValue() / 10.0;
            double hi = ratingSlider.getHighValue() / 10.0;
            ratingLabel.setText(String.format("Rating: %.1f–%.1f", lo, hi));
        }
    }

    private static Dictionary<Integer, JLabel> buildRatingLabels() {
        Hashtable<Integer, JLabel> t = new Hashtable<>();
        t.put(0, new JLabel("0"));
        t.put(10, new JLabel("1"));
        t.put(20, new JLabel("2"));
        t.put(30, new JLabel("3"));
        t.put(40, new JLabel("4"));
        t.put(50, new JLabel("5"));
        t.put(60, new JLabel("6"));
        t.put(70, new JLabel("7"));
        t.put(80, new JLabel("8"));
        t.put(90, new JLabel("9"));
        t.put(100, new JLabel("10"));
        return t;
    }

    private static void lockYearChooserTyping(JYearChooser chooser) {
        for (Component c : chooser.getComponents()) {
            if (c instanceof JSpinner spinner) {
                JComponent editor = spinner.getEditor();
                if (editor instanceof JSpinner.DefaultEditor def) {
                    def.getTextField().setEditable(false);
                    def.getTextField().setFocusable(false);
                }
            }
        }
    }
}
