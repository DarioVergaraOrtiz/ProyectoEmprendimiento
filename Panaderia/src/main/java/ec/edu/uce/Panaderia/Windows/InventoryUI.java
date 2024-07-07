package ec.edu.uce.Panaderia.Windows;

import ec.edu.uce.Panaderia.Model.Product;
import ec.edu.uce.Panaderia.Embedded.EmbeddedDatabase;
import ec.edu.uce.Panaderia.Observer.InventoryUpdateListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class InventoryUI extends JFrame {
    private EmbeddedDatabase embeddedDatabase;

    private JTextArea inventoryDetailsArea;
    private JTextField productNameField;
    private JTextField productPriceField;
    private JTextField productQuantityField;
    private JButton addButton;
    private JButton viewButton;
    private JButton deleteButton;

    // Lista de oyentes de actualización de inventario
    private List<InventoryUpdateListener> listeners = new ArrayList<>();

    public InventoryUI() throws HeadlessException {
        embeddedDatabase = new EmbeddedDatabase();

        setTitle("Inventory Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cambiado a DISPOSE_ON_CLOSE
        setLayout(new BorderLayout());

        // Panel para agregar productos
        JPanel addProductPanel = new JPanel(new FlowLayout());
        JLabel nameLabel = new JLabel("Nombre:");
        productNameField = new JTextField(15);
        JLabel priceLabel = new JLabel("Precio:");
        productPriceField = new JTextField(10);
        JLabel quantityLabel = new JLabel("Cantidad:");
        productQuantityField = new JTextField(10);
        addButton = new JButton("Agregar");
        addProductPanel.add(nameLabel);
        addProductPanel.add(productNameField);
        addProductPanel.add(priceLabel);
        addProductPanel.add(productPriceField);
        addProductPanel.add(quantityLabel);
        addProductPanel.add(productQuantityField);
        addProductPanel.add(addButton);
        add(addProductPanel, BorderLayout.NORTH);

        // Panel para ver y eliminar productos
        JPanel viewDeletePanel = new JPanel(new BorderLayout());
        inventoryDetailsArea = new JTextArea(15, 40);
        inventoryDetailsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(inventoryDetailsArea);
        viewButton = new JButton("Ver Inventario");
        deleteButton = new JButton("Eliminar Producto");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        viewDeletePanel.add(scrollPane, BorderLayout.CENTER);
        viewDeletePanel.add(buttonPanel, BorderLayout.SOUTH);
        add(viewDeletePanel, BorderLayout.CENTER);

        // Listener para el botón de agregar producto
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = productNameField.getText();
                double price = Double.parseDouble(productPriceField.getText());
                int quantity = Integer.parseInt(productQuantityField.getText());
                embeddedDatabase.addProduct(name, price, quantity);
                productNameField.setText("");
                productPriceField.setText("");
                productQuantityField.setText("");
                updateInventoryDetails();
                notifyInventoryUpdated(); // Notificar a los oyentes que el inventario ha sido actualizado
            }
        });

        // Listener para el botón de eliminar producto
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del producto a eliminar:"));
                embeddedDatabase.deleteProductById(productId);
                updateInventoryDetails();
                notifyInventoryUpdated(); // Notificar a los oyentes que el inventario ha sido actualizado
            }
        });


        // Listener para el botón de ver inventario
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInventoryDetails();
            }
        });

        // Listener para el botón de eliminar producto
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del producto a eliminar:"));
                embeddedDatabase.deleteProductById(productId);
                updateInventoryDetails();
                notifyInventoryUpdated(); // Notificar a los oyentes que el inventario ha sido actualizado
            }
        });


        // Hacer visible la interfaz
        setVisible(true);
    }

    // Método para actualizar los detalles del inventario en el JTextArea
    private void updateInventoryDetails() {
        List<Product> products = embeddedDatabase.getAllProducts();
        inventoryDetailsArea.setText("");
        for (Product product : products) {
            inventoryDetailsArea.append("ID: " + product.getId() + ", Nombre: " + product.getName() + ", Precio: $" + product.getPrice() + ", Cantidad: " + product.getQuantity() + "\n");
        }
    }

    // Método para registrar oyentes de actualización de inventario
    public void addInventoryUpdateListener(InventoryUpdateListener listener) {
        listeners.add(listener);
    }

    // Método para notificar a los oyentes de actualización de inventario
    private void notifyInventoryUpdated() {
        for (InventoryUpdateListener listener : listeners) {
            listener.onInventoryUpdated();
        }
    }
}
