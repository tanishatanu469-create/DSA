
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class InventoryPrototype extends JFrame {

    static class Product {
        int id, quantity;
        String name;
        double price;

        Product(int id, String name, int quantity, double price) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }
    }

    ArrayList<Product> products = new ArrayList<>();

    DefaultTableModel model;
    JTable table;
    JLabel totalLabel, stockLabel, valueLabel, alertLabel;
    JTextField searchField;

    Color blue = new Color(35, 85, 160);

    InventoryPrototype() {
        setTitle("Inventory Management System");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        addSamples();
        createHeader();
        createSidebar();
        createDashboard();

        setVisible(true);
    }

    void addSamples() {
        products.add(new Product(101, "Laptop", 15, 45000));
        products.add(new Product(102, "Keyboard", 3, 1200));
        products.add(new Product(103, "Mouse", 25, 600));
        products.add(new Product(104, "Monitor", 8, 12000));
        products.add(new Product(105, "Headphones", 2, 1800));
    }

    void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(blue);
        header.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("INVENTORY MANAGEMENT SYSTEM");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 23));

        JLabel subtitle = new JLabel("Smart Stock Management Dashboard");
        subtitle.setForeground(Color.WHITE);

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(title);
        text.add(subtitle);

        header.add(text, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
    }

    void createSidebar() {
        JPanel sidebar = new JPanel(new GridLayout(6, 1, 5, 12));
        sidebar.setBackground(new Color(30, 40, 60));
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 12, 25, 12));

        String[] names = {
            "Dashboard", "Add Product", "Delete Product",
            "Search Product", "Sort by Price", "Low Stock"
        };

        for (String name : names) {
            JButton button = new JButton(name);
            button.setBackground(new Color(48, 65, 90));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            sidebar.add(button);

            button.addActionListener(e -> {
                switch (name) {
                    case "Dashboard":
                        refreshTable();
                        break;
                    case "Add Product":
                        addProduct();
                        break;
                    case "Delete Product":
                        deleteProduct();
                        break;
                    case "Search Product":
                        searchField.requestFocus();
                        searchProducts();
                        break;
                    case "Sort by Price":
                        sortProducts();
                        break;
                    case "Low Stock":
                        showLowStock();
                        break;
                }
            });
        }

        add(sidebar, BorderLayout.WEST);
    }

    void createDashboard() {
        JPanel main = new JPanel(new BorderLayout(15, 20));
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
        cards.setOpaque(false);

        totalLabel = new JLabel();
        stockLabel = new JLabel();
        valueLabel = new JLabel();
        alertLabel = new JLabel();

        cards.add(createCard("Total Products", totalLabel));
        cards.add(createCard("Total Units", stockLabel));
        cards.add(createCard("Stock Value", valueLabel));
        cards.add(createCard("Low Stock Items", alertLabel));

        JPanel center = new JPanel(new BorderLayout(10, 15));
        center.setOpaque(false);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 15));

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchProducts());

        JButton resetButton = new JButton("Show All");
        resetButton.addActionListener(e -> refreshTable());

        JPanel searchActions = new JPanel(new FlowLayout());
        searchActions.setOpaque(false);
        searchActions.add(searchButton);
        searchActions.add(resetButton);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchActions, BorderLayout.EAST);

        String[] columns = {"ID", "Product Name", "Quantity", "Price"};

        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 14)
        );

        JScrollPane scroll = new JScrollPane(table);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);

        JButton update = new JButton("Update Selected");
        update.addActionListener(e -> updateProduct());

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshTable());

        actions.add(update);
        actions.add(refresh);

        center.add(searchPanel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        main.add(cards, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        refreshTable();
    }

    JPanel createCard(String title, JLabel value) {
        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230)),
            BorderFactory.createEmptyBorder(15, 12, 15, 12)
        ));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Arial", Font.BOLD, 13));

        value.setFont(new Font("Arial", Font.BOLD, 23));
        value.setForeground(blue);

        card.add(heading);
        card.add(value);

        return card;
    }

    void refreshTable() {
        model.setRowCount(0);

        int units = 0;
        int alerts = 0;
        double totalValue = 0;

        for (Product p : products) {
            model.addRow(new Object[]{
                p.id, p.name, p.quantity, p.price
            });

            units += p.quantity;
            totalValue += p.quantity * p.price;

            if (p.quantity < 5) {
                alerts++;
            }
        }

        totalLabel.setText(String.valueOf(products.size()));
        stockLabel.setText(String.valueOf(units));
        valueLabel.setText(String.format("Rs. %.0f", totalValue));
        alertLabel.setText(String.valueOf(alerts));
    }

    void addProduct() {
        try {
            JTextField id = new JTextField();
            JTextField name = new JTextField();
            JTextField quantity = new JTextField();
            JTextField price = new JTextField();

            Object[] fields = {
                "Product ID:", id,
                "Product Name:", name,
                "Quantity:", quantity,
                "Price:", price
            };

            int result = JOptionPane.showConfirmDialog(
                this, fields, "Add Product",
                JOptionPane.OK_CANCEL_OPTION
            );

            if (result != JOptionPane.OK_OPTION) return;

            int productId = Integer.parseInt(id.getText());
            int qty = Integer.parseInt(quantity.getText());
            double cost = Double.parseDouble(price.getText());

            if (productId <= 0 || qty < 0 || cost < 0 ||
                name.getText().trim().isEmpty()) {
                throw new IllegalArgumentException();
            }

            for (Product p : products) {
                if (p.id == productId) {
                    JOptionPane.showMessageDialog(
                        this, "Product ID already exists!");
                    return;
                }
            }

            products.add(new Product(
                productId, name.getText(), qty, cost
            ));

            refreshTable();

            JOptionPane.showMessageDialog(
                this, "Product Added Successfully!"
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "Enter valid numeric values!"
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this, "Enter valid product details!"
            );
        }
    }

    void deleteProduct() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Select a product first!");
            return;
        }

        int id = Integer.parseInt(
            model.getValueAt(row, 0).toString()
        );

        int confirm = JOptionPane.showConfirmDialog(
            this, "Delete selected product?",
            "Confirm", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            products.removeIf(p -> p.id == id);
            refreshTable();
        }
    }

    void searchProducts() {
        String query = searchField.getText()
            .trim().toLowerCase();

        model.setRowCount(0);

        for (Product p : products) {
            if (p.name.toLowerCase().contains(query) ||
                String.valueOf(p.id).contains(query)) {
                model.addRow(new Object[]{
                    p.id, p.name, p.quantity, p.price
                });
            }
        }
    }

    void updateProduct() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Select a product first!");
            return;
        }

        int id = Integer.parseInt(
            model.getValueAt(row, 0).toString()
        );

        for (Product p : products) {
            if (p.id == id) {
                String qty = JOptionPane.showInputDialog(
                    this, "Enter new quantity:", p.quantity
                );

                if (qty == null) return;

                String price = JOptionPane.showInputDialog(
                    this, "Enter new price:", p.price
                );

                if (price == null) return;

                try {
                    int newQty = Integer.parseInt(qty);
                    double newPrice = Double.parseDouble(price);

                    if (newQty < 0 || newPrice < 0) {
                        throw new NumberFormatException();
                    }

                    p.quantity = newQty;
                    p.price = newPrice;

                    refreshTable();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        this, "Invalid quantity or price!");
                }

                return;
            }
        }
    }

    void sortProducts() {
        products.sort(
            Comparator.comparingDouble(p -> p.price)
        );

        refreshTable();
    }

    void showLowStock() {
        model.setRowCount(0);

        for (Product p : products) {
            if (p.quantity < 5) {
                model.addRow(new Object[]{
                    p.id, p.name, p.quantity, p.price
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            () -> new InventoryPrototype()
        );
    }
}

