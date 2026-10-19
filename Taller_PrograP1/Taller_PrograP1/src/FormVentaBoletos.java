import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormVentaBoletos {
    private JComboBox cmbRuta;
    private JLabel lblTitulo;
    private JPanel panelPrincipal;
    private JLabel lblNombre;
    private JTextField txtNombre;
    private JLabel lblCedula;
    private JTextField txtCedula;
    private JTextField txtCantidad;
    private JButton btnComprar;
    private JButton btnVerSiguiente;
    private JButton btnMostrarCola;
    private JButton btnEmbarcar;
    private JLabel lblVendidosGuayaquil;
    private JLabel lblDisponiblesGuayaquil;
    private JLabel lblVendidosCuenca;
    private JLabel lblDisponiblesCuenca;
    private JLabel lblVendidosLoja;
    private JLabel lblDisponiblesLoja;
    private JLabel lblTotalRecaudado;
    private JTextArea txtArea;
    private JButton btnUltimaCompra;
    private JLabel lblPrioridad;
    private JComboBox cmbPrioridad;
    private JButton btnMostrarPrioridad;

    private Bus busGuayaquil;
    private Bus busCuenca;
    private Bus busLoja;

    public FormVentaBoletos() {

        busGuayaquil = new Bus("QUITO - GUAYAQUIL", 10.50);
        busCuenca = new Bus("QUITO - CUENCA", 12.75);
        busLoja = new Bus("QUITO - LOJA", 15.00);

        txtArea.setEditable(false);
        actualizarLabels();

        // Boton comprar
        btnComprar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comprarBoletos();
            }
        });

        // Boton consultar
        btnVerSiguiente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consultarSiguiente();
            }
        });

        // Boton embarcar
        btnEmbarcar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                embarcarPasajero();
            }
        });

        // Boton mostrar cola
        btnMostrarCola.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarCola();
            }
        });
        btnUltimaCompra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Bus bus = obtenerBusSeleccionado();

                    Pasajeros compra = bus.consultarUltimaCompra();

                    JOptionPane.showMessageDialog(
                            panelPrincipal,
                            "ULTIMA COMPRA REALIZADA\n\n"
                                    + compra.toString()
                    );

                } catch (IllegalArgumentException ex) {

                    JOptionPane.showMessageDialog(
                            panelPrincipal,
                            ex.getMessage()
                    );
                }
            }
        });
        btnMostrarPrioridad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Bus bus = obtenerBusSeleccionado();

                txtArea.append(
                        "\nCOLA DE PRIORIDAD (solo consulta: el embarque es por orden de llegada)\n"
                                + "Ruta: " + bus.getRuta() + "\n\n"
                                + bus.mostrarColaPrioridad()
                                + "-----------------------------\n\n"
                );

            }
        });
    }

    // Obtener el bus seleccionado
    private Bus obtenerBusSeleccionado() {

        int opcion = cmbRuta.getSelectedIndex();

        if (opcion == 0) {
            return busGuayaquil;
        }

        if (opcion == 1) {
            return busCuenca;
        }

        return busLoja;
    }

    // Indica si el caracter es una letra (incluye tildes y ñ)
    private boolean esLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                || c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú'
                || c == 'Á' || c == 'É' || c == 'Í' || c == 'Ó' || c == 'Ú'
                || c == 'ñ' || c == 'Ñ' || c == 'ü' || c == 'Ü';
    }

    // El nombre solo puede tener letras y espacios, y al menos una letra
    private void validarNombre(String nombre) {
        int letras = 0;
        for (int i = 0; i < nombre.length(); i++) {
            char c = nombre.charAt(i);
            if (esLetra(c)) {
                letras++;
            } else if (c != ' ') {
                throw new IllegalArgumentException("El nombre solo debe contener letras.");
            }
        }
        if (letras == 0) {
            throw new IllegalArgumentException("Debe ingresar el nombre del pasajero.");
        }
    }

    // La cedula no puede estar vacia, solo numeros y exactamente 10 digitos
    private void validarCedula(String cedula) {
        if (cedula.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar la cedula.");
        }
        for (int i = 0; i < cedula.length(); i++) {
            char c = cedula.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException("La cedula solo debe contener numeros (sin espacios).");
            }
        }
        if (cedula.length() != 10) {
            throw new IllegalArgumentException("La cedula debe tener 10 digitos.");
        }
    }

    // Busca en las tres rutas el nombre con el que se registro la cedula ("" si nunca compro)
    private String nombreRegistrado(String cedula) {
        if (!busGuayaquil.nombreDeCedula(cedula).isEmpty()) {
            return busGuayaquil.nombreDeCedula(cedula);
        }
        if (!busCuenca.nombreDeCedula(cedula).isEmpty()) {
            return busCuenca.nombreDeCedula(cedula);
        }
        return busLoja.nombreDeCedula(cedula);
    }

    // Una misma cedula no puede comprar con nombres distintos
    private void validarMismoNombre(String cedula, String nombre) {
        String registrado = nombreRegistrado(cedula);
        if (!registrado.isEmpty() && !registrado.equalsIgnoreCase(nombre)) {
            throw new IllegalArgumentException("La cedula " + cedula + " ya esta registrada a nombre de " + registrado + ".");
        }
    }

    private void comprarBoletos() {

        try {

            // Obtener los datos del formulario
            String nombre = txtNombre.getText();
            String cedula = txtCedula.getText();

            // Validar nombre y cedula
            validarNombre(nombre);
            validarCedula(cedula);
            validarMismoNombre(cedula, nombre);

            int cantidad = Integer.parseInt(
                    txtCantidad.getText().trim());

            // Obtener el bus seleccionado
            Bus busSeleccionado = obtenerBusSeleccionado();

            // Contar los boletos de la cedula en las tres rutas
            int acumulados =
                    busGuayaquil.contarBoletosCedula(cedula)
                            + busCuenca.contarBoletosCedula(cedula)
                            + busLoja.contarBoletosCedula(cedula);

            // Obtener la prioridad seleccionada
            int prioridad;

            if (cmbPrioridad.getSelectedIndex() == 1) {
                prioridad = 1;
            } else {
                prioridad = 2;
            }

            // Registrar la compra con su prioridad
            int vendidos = busSeleccionado.venderBoletos(
                    nombre,
                    cedula,
                    cantidad,
                    acumulados,
                    prioridad
            );

            // Calcular el precio total de la compra
            double precioTotal =
                    vendidos * busSeleccionado.getPrecioBoleto();

            // Mostrar la compra en el JTextArea
            txtArea.append(
                    "COMPRA REALIZADA\n"
                            + "Ruta: " + busSeleccionado.getRuta() + "\n"
                            + "Nombre: " + nombre + "\n"
                            + "Cedula: " + cedula + "\n"
                            + "Boletos vendidos: " + vendidos + "\n"
                            + "Precio total: $"
                            + String.format("%.2f", precioTotal) + "\n"
                            + "Prioridad: "
                            + (prioridad == 1 ? "Preferencial" : "Normal")
                            + "\n-----------------------------\n\n"
            );

            // Actualizar los indicadores
            actualizarLabels();

            // Mostrar el resultado de la compra
            if (vendidos < cantidad) {

                JOptionPane.showMessageDialog(
                        panelPrincipal,
                        "Solo se vendieron " + vendidos
                                + " boletos por los limites establecidos.");

            } else {

                JOptionPane.showMessageDialog(
                        panelPrincipal,
                        "Compra realizada correctamente.");
            }

            // Limpiar los campos
            txtNombre.setText("");
            txtCedula.setText("");
            txtCantidad.setText("");

            // Restablecer la prioridad a Normal
            cmbPrioridad.setSelectedIndex(0);

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    panelPrincipal,
                    "Ingrese una cantidad numerica valida.");

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    panelPrincipal,
                    ex.getMessage());
        }
    }

    // Consultar siguiente pasajero
    private void consultarSiguiente() {

        Bus busSeleccionado = obtenerBusSeleccionado();

        Pasajeros pasajero =
                busSeleccionado.consultarSiguiente();

        if (pasajero == null) {

            JOptionPane.showMessageDialog(
                    panelPrincipal,
                    "No existen pasajeros en la cola.");

        } else {

            JOptionPane.showMessageDialog(
                    panelPrincipal,
                    "SIGUIENTE PASAJERO\n\n"
                            + pasajero.toString());
        }
    }

    // Embarcar pasajero
    private void embarcarPasajero() {

        try {
            Bus busSeleccionado = obtenerBusSeleccionado();

            Pasajeros pasajero =
                    busSeleccionado.embarcarPasajero();

            JOptionPane.showMessageDialog(
                    panelPrincipal,
                    "PASAJERO EMBARCADO\n\n"
                            + pasajero.toString());

            actualizarLabels();

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    panelPrincipal,
                    ex.getMessage());
        }
    }

    // Mostrar cola de pasajeros
    private void mostrarCola() {

        Bus busSeleccionado = obtenerBusSeleccionado();

        txtArea.append(
                "\nCOLA DE PASAJEROS\n"
                        + "Ruta: " + busSeleccionado.getRuta() + "\n\n"
                        + busSeleccionado.mostrarCola()
                        + "\n-----------------------------\n\n"
        );
    }

    // Actualizar los indicadores
    private void actualizarLabels() {

        lblVendidosGuayaquil.setText(
                "Guayaquil - Vendidos: "
                        + busGuayaquil.getBoletosVendidos());

        lblDisponiblesGuayaquil.setText(
                "Guayaquil - Disponibles: "
                        + busGuayaquil.getBoletosDisponibles());

        lblVendidosCuenca.setText(
                "Cuenca - Vendidos: "
                        + busCuenca.getBoletosVendidos());

        lblDisponiblesCuenca.setText(
                "Cuenca - Disponibles: "
                        + busCuenca.getBoletosDisponibles());

        lblVendidosLoja.setText(
                "Loja - Vendidos: "
                        + busLoja.getBoletosVendidos());

        lblDisponiblesLoja.setText(
                "Loja - Disponibles: "
                        + busLoja.getBoletosDisponibles());

        double total =
                busGuayaquil.getTotalRecaudado()
                        + busCuenca.getTotalRecaudado()
                        + busLoja.getTotalRecaudado();

        lblTotalRecaudado.setText(
                "TOTAL RECAUDADO: $"
                        + String.format("%.2f", total));
    }

    // Metodo principal
    public static void main(String[] args) {

        JFrame ventana =
                new JFrame("Sistema de Venta de Boletos");

        ventana.setContentPane(
                new FormVentaBoletos().panelPrincipal);

        ventana.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
