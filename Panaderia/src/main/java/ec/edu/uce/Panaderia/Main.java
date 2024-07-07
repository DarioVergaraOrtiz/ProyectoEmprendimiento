package ec.edu.uce.Panaderia;

import ec.edu.uce.Panaderia.Windows.AppUI;
import ec.edu.uce.Panaderia.Windows.InventoryUI;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        // Verifica si el entorno es headless antes de iniciar la interfaz gráfica
        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(() -> {
                try {
                    AppUI appUI = new AppUI();
                    appUI.setVisible(true);

                } catch (HeadlessException e) {
                    System.err.println("Error: El entorno gráfico no está disponible.");
                }
            });
        } else {
            System.err.println("Error: El entorno gráfico no está disponible.");
            return; // Salir del programa si el entorno gráfico no está disponible
        }
    }
}
