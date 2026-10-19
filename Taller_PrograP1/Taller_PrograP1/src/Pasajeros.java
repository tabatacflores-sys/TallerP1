public class Pasajeros {
    private String ruta;
    private String nombre;
    private String cedula;
    private int numBoletos;
    private double precioTotal;
    private int prioridad;

    public Pasajeros(String ruta, String nombre, String cedula,
                     int numBoletos, double precioTotal,
                     int prioridad) {

        this.ruta = ruta;
        this.nombre = nombre;
        this.cedula = cedula;
        this.numBoletos = numBoletos;
        this.precioTotal = precioTotal;
        setPrioridad(prioridad);
    }

    public String getRuta() {
        return ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public int getNumBoletos() {
        return numBoletos;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setNumBoletos(int numBoletos) {
        this.numBoletos = numBoletos;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public void setPrioridad(int prioridad) {
        if (prioridad != 1 && prioridad != 2) {
            throw new IllegalArgumentException(
                    "La prioridad debe ser 1 o 2.");
        }
        this.prioridad = prioridad;
    }


    @Override
    public String toString() {
        return "Ruta: " + ruta
                + "\nNombre: " + nombre
                + "\nCédula: " + cedula
                + "\nCantidad de boletos: " + numBoletos
                + "\nPrecio total: $" + precioTotal
                + "\nPrioridad: "
                + (prioridad == 1 ? "Preferencial" : "Normal");
    }

}
