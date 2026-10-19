import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.PriorityQueue;
import java.util.Comparator;

public class Bus {
    private static final int CAPACIDAD_MAXIMA = 20;
    private static final int MAX_BOLETOS_CEDULA = 5;

    // Atributos del bus
    private String ruta;
    private double precioBoleto;
    private int boletosVendidos;
    private double totalRecaudado;

    // Estructuras de datos
    private Queue<Pasajeros> colaPasajeros;
    private Queue<Pasajeros> historialCompras;
    
    private Stack<Pasajeros> pilaCompras;

    // Constructor
    public Bus(String ruta, double precioBoleto) {

        this.ruta = ruta;
        this.precioBoleto = precioBoleto;
        this.boletosVendidos = 0;
        this.totalRecaudado = 0;

        colaPasajeros = new LinkedList<>();
        historialCompras = new LinkedList<>();
        pilaCompras = new Stack<>();
    }

    // Obtener la ruta
    public String getRuta() {
        return ruta;
    }

    // Obtener el precio del boleto
    public double getPrecioBoleto() {
        return precioBoleto;
    }

    // Obtener boletos vendidos
    public int getBoletosVendidos() {
        return boletosVendidos;
    }

    // Obtener boletos disponibles
    public int getBoletosDisponibles() {
        return CAPACIDAD_MAXIMA - boletosVendidos;
    }

    // Obtener el total recaudado
    public double getTotalRecaudado() {
        return totalRecaudado;
    }

    // Contar boletos comprados por una cedula
    public int contarBoletosCedula(String cedula) {

        int total = 0;

        for (Pasajeros pasajero : historialCompras) {

            if (pasajero.getCedula().equals(cedula)) {
                total += pasajero.getNumBoletos();
            }
        }

        return total;
    }

    // Devuelve el nombre con el que se registro la cedula, o "" si nunca compro en esta ruta
    public String nombreDeCedula(String cedula) {

        for (Pasajeros compra : historialCompras) {

            if (compra.getCedula().equals(cedula)) {
                return compra.getNombre();
            }
        }

        return "";
    }

    // Si la cedula sigue en la cola, le suma los boletos y actualiza su prioridad (conserva su posicion) y devuelve true
    public boolean actualizarCompra(String cedula, int cantidadVendida, int prioridad) {

        for (Pasajeros pasajero : colaPasajeros) {

            if (pasajero.getCedula().equals(cedula)) {

                int nuevaCantidad = pasajero.getNumBoletos() + cantidadVendida;
                pasajero.setNumBoletos(nuevaCantidad);
                pasajero.setPrecioTotal(nuevaCantidad * precioBoleto);
                pasajero.setPrioridad(prioridad);
                return true;
            }
        }

        return false;
    }

    // Revisa que la venta se pueda hacer, si no lanza la excepcion con el motivo
    public void validarVenta(int cantidad, int boletosAcumulados) {

        if (cantidad < 1 || cantidad > MAX_BOLETOS_CEDULA) {
            throw new IllegalArgumentException(
                    "La cantidad debe estar entre 1 y 5.");
        }

        if (getBoletosDisponibles() <= 0) {
            throw new IllegalArgumentException(
                    "No existen boletos disponibles.");
        }

        if (boletosAcumulados >= MAX_BOLETOS_CEDULA) {
            throw new IllegalArgumentException(
                    "La cedula ya tiene 5 boletos comprados.");
        }
    }

    // Calcula cuantos boletos se pueden vender (venta parcial si no alcanza)
    public int calcularCantidadVendida(int cantidad, int boletosAcumulados) {

        int permitidos = MAX_BOLETOS_CEDULA - boletosAcumulados;
        int cantidadVendida = cantidad;

        if (cantidadVendida > permitidos) {
            cantidadVendida = permitidos;
        }

        if (cantidadVendida > getBoletosDisponibles()) {
            cantidadVendida = getBoletosDisponibles();
        }

        return cantidadVendida;
    }

    // Encola al pasajero (o actualiza su compra) y guarda la compra en el historial y en la pila
    public void registrarVenta(String nombre, String cedula,
                               int cantidadVendida, int prioridad) {

        double precio = cantidadVendida * precioBoleto;

        // Cola FIFO: si no estaba en la cola, se agrega al final
        if (!actualizarCompra(cedula, cantidadVendida, prioridad)) {
            colaPasajeros.offer(new Pasajeros(
                    ruta, nombre, cedula, cantidadVendida, precio, prioridad));
        }

        // Registro independiente de la compra
        Pasajeros compra = new Pasajeros(
                ruta, nombre, cedula, cantidadVendida, precio, prioridad);

        historialCompras.offer(compra);   // historial (cola)
        pilaCompras.push(compra);         // pila LIFO

        boletosVendidos += cantidadVendida;
        totalRecaudado += precio;
    }

    // Realiza la venta y devuelve cuantos boletos se vendieron realmente
    public int venderBoletos(String nombre, String cedula,
                             int cantidad, int boletosAcumulados,
                             int prioridad) {

        validarVenta(cantidad, boletosAcumulados);
        int cantidadVendida = calcularCantidadVendida(cantidad, boletosAcumulados);
        registrarVenta(nombre, cedula, cantidadVendida, prioridad);
        return cantidadVendida;
    }

    // Consultar primero de la cola FIFO
    public Pasajeros consultarSiguiente() {
        return colaPasajeros.peek();
    }

    // Embarcar al primer pasajero FIFO
    public Pasajeros embarcarPasajero() {

        if (colaPasajeros.isEmpty()) {
            throw new IllegalArgumentException(
                    "No existen pasajeros para embarcar.");
        }

        return colaPasajeros.poll();
    }

    // Mostrar pasajeros pendientes en orden FIFO
    public String mostrarCola() {

        if (colaPasajeros.isEmpty()) {
            return "No existen pasajeros en la cola.";
        }

        String resultado = "";

        for (Pasajeros pasajero : colaPasajeros) {
            resultado += pasajero.toString() + "\n\n";
        }

        return resultado;
    }

    // Consultar ultima compra de la pila
    public Pasajeros consultarUltimaCompra() {

        if (pilaCompras.isEmpty()) {
            throw new IllegalArgumentException(
                    "No existen compras registradas.");
        }

        return pilaCompras.peek();
    }

    // Buscar la posicion de llegada en la cola FIFO
    private int posicionEnCola(Pasajeros buscado) {

        int posicion = 0;

        for (Pasajeros pasajero : colaPasajeros) {

            if (pasajero == buscado) {
                return posicion;
            }

            posicion++;
        }

        return -1;
    }

    // Crea una cola de prioridad auxiliar con una copia de los pasajeros en espera
    // (primero los preferenciales; si empatan, el que llego antes)
    private PriorityQueue<Pasajeros> crearColaPrioridad() {

        PriorityQueue<Pasajeros> colaAuxiliar =
                new PriorityQueue<>(
                        new Comparator<Pasajeros>() {

                            @Override
                            public int compare(Pasajeros p1,
                                               Pasajeros p2) {

                                // Comparar las prioridades
                                int comparacion = Integer.compare(
                                        p1.getPrioridad(),
                                        p2.getPrioridad()
                                );

                                if (comparacion != 0) {
                                    return comparacion;
                                }

                                // Si tienen igual prioridad,
                                // respetar el orden de llegada
                                return Integer.compare(
                                        posicionEnCola(p1),
                                        posicionEnCola(p2)
                                );
                            }
                        }
                );

        // Copiar pasajeros pendientes a la cola auxiliar
        for (Pasajeros pasajero : colaPasajeros) {
            colaAuxiliar.offer(pasajero);
        }

        return colaAuxiliar;
    }

    // Mostrar los pasajeros en espera ordenados por prioridad (solo consulta, el embarque es FIFO)
    public String mostrarColaPrioridad() {

        if (colaPasajeros.isEmpty()) {
            return "No existen pasajeros en la cola.";
        }

        PriorityQueue<Pasajeros> colaAuxiliar = crearColaPrioridad();

        String resultado = "";

        // Sacar en orden de prioridad
        while (!colaAuxiliar.isEmpty()) {
            resultado += colaAuxiliar.poll().toString() + "\n\n";
        }

        return resultado;
    }
}
