public class Jugador {
    String nombre;
    int puntos;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.puntos = 0;
    }

    public void sumarPuntos(int puntos) {
        this.puntos += puntos;
    }
}
