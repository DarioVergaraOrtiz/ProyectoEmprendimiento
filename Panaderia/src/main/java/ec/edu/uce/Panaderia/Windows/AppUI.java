package ec.edu.uce.Panaderia.Windows;

import ec.edu.uce.Panaderia.Model.Product;
import ec.edu.uce.Panaderia.Embedded.EmbeddedDatabase;
import ec.edu.uce.Panaderia.Observer.InventoryUpdateListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUI extends JFrame implements KeyListener, InventoryUpdateListener {
    private JTextArea productDetailsArea;
    private JButton buyButton;
    private JButton resetButton;
    private double totalCompra;

    // Mapa para almacenar los productos y sus precios
    private Map<String, Double> products;

    public AppUI() throws HeadlessException {
        setTitle("Dasten App");
        setSize(1000, 700); // Tamaño ajustado para una interfaz más amplia
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); // Utilizando GridBagLayout para más control

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Margen

        // Panel para los productos
        JPanel productsPanel = new JPanel(new GridLayout(2, 3, 20, 20)); // Grid de 2 filas y 3 columnas para productos con más separación
        products = new HashMap<>();
        updateProductMap(new EmbeddedDatabase().getAllProducts());

        // Agregar botones de productos con imágenes y etiquetas
        for (String productName : products.keySet()) {
            JPanel productPanel = new JPanel(new BorderLayout());
            JButton productButton = createProductButton(productName);
            productPanel.add(productButton, BorderLayout.CENTER);
            JLabel nameLabel = new JLabel(productName);
            nameLabel.setHorizontalAlignment(JLabel.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Cambiar la fuente y tamaño
            productPanel.add(nameLabel, BorderLayout.SOUTH);
            productsPanel.add(productPanel);
        }

        add(productsPanel, gbc);

        // Panel para mostrar detalles del producto
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 1.0; // Aumenta el peso vertical para ocupar más espacio en la parte inferior
        productDetailsArea = new JTextArea(8, 40); // Reducir el número de filas y ajustar el tamaño
        productDetailsArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(productDetailsArea);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(detailsScrollPane, gbc);

        // Panel para botones de acción (comprar y reiniciar)
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.0; // Reinicia el peso horizontal
        gbc.fill = GridBagConstraints.NONE; // Restablece el relleno
        gbc.anchor = GridBagConstraints.PAGE_END; // Alinea hacia abajo

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buyButton = new JButton("Comprar");
        resetButton = new JButton("Reiniciar Compra");
        buyButton.setFont(new Font("Arial", Font.BOLD, 16)); // Cambiar la fuente y tamaño
        resetButton.setFont(new Font("Arial", Font.BOLD, 16)); // Cambiar la fuente y tamaño
        actionPanel.add(buyButton);
        actionPanel.add(resetButton);
        add(actionPanel, gbc);

        // Listener para el botón de comprar
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí podrías enviar la orden de compra o guardar la compra realizada
                productDetailsArea.append("\nTotal de la compra: $" + totalCompra);
                totalCompra = 0; // Reiniciar el total de la compra
            }
        });

        // Listener para el botón de reiniciar compra
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                totalCompra = 0; // Reiniciar el total de la compra
                productDetailsArea.setText(""); // Limpiar el área de detalles
            }
        });

        // Hacer visible la interfaz
        setVisible(true);
        setFocusable(true); // Hacer el JFrame foco para recibir eventos de teclado
        addKeyListener(this); // Agregar el KeyListener a la ventana
    }

    // Método para crear un botón de producto con su nombre y acción de añadir al carrito
    private JButton createProductButton(String productName) {
        JButton button = new JButton(); // Botón sin texto inicialmente

        // Cargar imagen del producto (ajusta la ruta a donde tengas tus imágenes)
        ImageIcon icon = createImageIcon("/images/" + productName + ".jpg");
        if (icon != null) {
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Ajusta el tamaño de la imagen
            ImageIcon scaledIcon = new ImageIcon(scaledImg);
            button.setIcon(scaledIcon);
        } else {
            button.setText("Image not found");
        }

        // Listener para añadir al carrito cuando se hace clic en la imagen
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double price = products.get(productName);
                totalCompra += price;
                productDetailsArea.append("\nAñadido al carrito: " + productName + " - $" + price);
            }
        });
        return button;
    }

    // Método para crear un ImageIcon desde un archivo de imagen en cualquier formato
    private ImageIcon createImageIcon(String path) {
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("No se puede encontrar el archivo de imagen: " + path);
            return null;
        }
    }

    // Método para actualizar el mapa de productos
    private void updateProductMap(List<Product> productList) {
        for (Product product : productList) {
            products.put(product.getName(), product.getPrice());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No usado
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_I) { // Si se presiona la tecla 'I'
            SwingUtilities.invokeLater(() -> {
                InventoryUI inventoryUI = new InventoryUI();
                inventoryUI.addInventoryUpdateListener(this); // Registrar AppUI como oyente
                inventoryUI.setVisible(true);
                inventoryUI.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        inventoryUI.setVisible(false); // Oculta la ventana en lugar de cerrarla
                    }
                });
            });
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        // No usado
    }

    @Override
    public void onInventoryUpdated() {
        // Actualiza los productos en AppUI cuando se actualiza el inventario en InventoryUI
        updateProductMap(new EmbeddedDatabase().getAllProducts());
    }
}
