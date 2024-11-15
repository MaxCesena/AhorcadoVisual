import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Ahorcado {
    private final List<Jugador> jugadores;
    private final int puntosObjetivo;
    private HashSet<Character> letrasAdivinadas;
    private HashMap<Character, Integer> letrasFrecuencia;
    private String frase;
    private int turnoActual;

    // Componentes de la GUI
    private JFrame frame;
    private JLabel lblFrase;
    private JTextArea txtPuntajes;
    private JLabel lblMensaje;
    private JPanel panelLetras;

    public Ahorcado(List<Jugador> jugadores, int puntosObjetivo) {
        this.jugadores = jugadores;
        this.puntosObjetivo = puntosObjetivo;
        this.turnoActual = 0;
        inicializarGUI();
        iniciarNuevaRonda();
    }

    private void inicializarGUI() {
        frame = new JFrame("Juego del Ahorcado");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panel superior: Frase oculta
        lblFrase = new JLabel("", SwingConstants.CENTER);
        lblFrase.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(lblFrase, BorderLayout.NORTH);

        // Panel lateral: Puntajes
        txtPuntajes = new JTextArea(10, 20);
        txtPuntajes.setEditable(false);
        JScrollPane scrollPuntajes = new JScrollPane(txtPuntajes);
        frame.add(scrollPuntajes, BorderLayout.EAST);

        // Panel inferior: Mensajes del juego
        lblMensaje = new JLabel("¡Bienvenido al juego del ahorcado!");
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(lblMensaje, BorderLayout.SOUTH);

        // Panel central: Letras del abecedario
        panelLetras = new JPanel(new GridLayout(4, 7, 5, 5));
        agregarBotonesLetras();
        frame.add(panelLetras, BorderLayout.CENTER);

        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private void agregarBotonesLetras() {
        for (char letra = 'a'; letra <= 'z'; letra++) {
            JButton btnLetra = new JButton(String.valueOf(letra));
            btnLetra.setFont(new Font("Arial", Font.BOLD, 16));
            char finalLetra = letra;
            btnLetra.addActionListener(e -> manejarTurno(finalLetra));
            panelLetras.add(btnLetra);
        }
    }

    private void iniciarNuevaRonda() {
        solicitarFrase();
        letrasAdivinadas = new HashSet<>();
        letrasFrecuencia = contarLetrasFrase();
        turnoActual = 0;
        actualizarFraseOculta();
        actualizarPuntajes();
        lblMensaje.setText("¡Nueva ronda! Turno de " + jugadores.get(turnoActual).nombre);
    }

    private void solicitarFrase() {
        do {
            frase = Frases.obtenerFraseAleatoria();
        } while (frase.split(" ").length < 4);
    }

    private HashMap<Character, Integer> contarLetrasFrase() {
        HashMap<Character, Integer> frecuencia = new HashMap<>();
        for (char c : frase.toCharArray()) {
            if (Character.isLetter(c)) {
                frecuencia.put(c, frecuencia.getOrDefault(c, 0) + 1);
            }
        }
        return frecuencia;
    }

    private void manejarTurno(char letra) {
        Jugador jugador = jugadores.get(turnoActual);

        if (letrasAdivinadas.contains(letra)) {
            // Si la letra ya fue intentada, se pierden 3 puntos y termina el turno
            lblMensaje.setText("La letra " + letra + " ya fue intentada. Pierdes 3 puntos.");
            jugador.sumarPuntos(-3);
            avanzarTurno(); // El turno termina
        } else if (letrasFrecuencia.containsKey(letra)) {
            // Si la letra está en la frase, sumamos puntos según la frecuencia
            int frecuencia = letrasFrecuencia.get(letra);
            jugador.sumarPuntos(3 * frecuencia);
            letrasAdivinadas.add(letra);
            lblMensaje.setText("¡Correcto! La letra " + letra + " aparece " + frecuencia + " veces.");

            // Si la frase está completa, no termina el turno, pero se debe continuar jugando
            if (fraseCompleta()) {
                jugador.sumarPuntos(5); // Gana 5 puntos por completar la frase
                lblMensaje.setText(jugador.nombre + " ha adivinado la frase y gana la ronda.");
                verificarFinDeJuego(); // Verificar si el juego terminó después de la ronda
            }
        } else {
            // Si la letra no está en la frase, se pierden 1 punto y termina el turno
            lblMensaje.setText("La letra " + letra + " no está en la frase. Pierdes 1 punto.");
            jugador.sumarPuntos(-1);
            letrasAdivinadas.add(letra); // Añadimos la letra a las ya intentadas
            avanzarTurno(); // El turno termina
        }

        // Actualizamos la frase oculta y los puntajes
        actualizarFraseOculta();
        actualizarPuntajes();
    }

    private void mostrarClasificacionFinal() {
        // Ordenamos los jugadores por puntos de forma descendente
        jugadores.sort((j1, j2) -> Integer.compare(j2.puntos, j1.puntos));

        // Mostramos la clasificación final con JOptionPane
        StringBuilder clasificacion = new StringBuilder("Clasificación Final:\n");
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            clasificacion.append((i + 1)).append(". ").append(jugador.nombre).append(" - ").append(jugador.puntos).append(" puntos\n");
        }

        JOptionPane.showMessageDialog(frame, clasificacion.toString(), "Clasificación Final", JOptionPane.INFORMATION_MESSAGE);
    }





    private boolean fraseCompleta() {
        for (char c : frase.toCharArray()) {
            if (Character.isLetter(c) && !letrasAdivinadas.contains(c)) {
                return false;
            }
        }
        return true;
    }

    private void avanzarTurno() {
        turnoActual = (turnoActual + 1) % jugadores.size();
        lblMensaje.setText("Turno de " + jugadores.get(turnoActual).nombre);
    }

    private void actualizarFraseOculta() {
        StringBuilder oculta = new StringBuilder();
        for (char c : frase.toCharArray()) {
            if (Character.isLetter(c) && !letrasAdivinadas.contains(c)) {
                oculta.append("_ ");
            } else {
                oculta.append(c).append(" ");
            }
        }
        lblFrase.setText(oculta.toString().trim());
    }

    private void actualizarPuntajes() {
        StringBuilder puntajes = new StringBuilder();
        for (Jugador jugador : jugadores) {
            puntajes.append(jugador.nombre).append(": ").append(jugador.puntos).append(" puntos\n");
        }
        txtPuntajes.setText(puntajes.toString());
    }

    private void verificarFinDeJuego() {
        if (jugadores.stream().anyMatch(j -> j.puntos >= puntosObjetivo)) {
            Jugador ganador = jugadores.stream().max(Comparator.comparingInt(j -> j.puntos)).orElse(null);
            JOptionPane.showMessageDialog(frame, ganador.nombre + " ha ganado el juego con " + ganador.puntos + " puntos.", "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            mostrarClasificacionFinal();
        } else {
            iniciarNuevaRonda();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int numJugadores = solicitarEntero("Ingresa el número de jugadores (2-4):", 2, 4);
            List<Jugador> jugadores = new ArrayList<>();
            for (int i = 1; i <= numJugadores; i++) {
                String nombre = JOptionPane.showInputDialog(null, "Nombre del Jugador " + i + ":", "Configuración", JOptionPane.QUESTION_MESSAGE);
                jugadores.add(new Jugador(nombre == null || nombre.isBlank() ? "Jugador " + i : nombre));
            }
            int puntosObjetivo = solicitarEntero("Ingresa los puntos necesarios para ganar el juego:", 1, Integer.MAX_VALUE);
            new Ahorcado(jugadores, puntosObjetivo);
        });
    }

    private static int solicitarEntero(String mensaje, int min, int max) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, mensaje, "Configuración", JOptionPane.QUESTION_MESSAGE);
            if (input == null) System.exit(0);
            try {
                int valor = Integer.parseInt(input);
                if (valor >= min && valor <= max) return valor;
            } catch (NumberFormatException ignored) {}
            JOptionPane.showMessageDialog(null, "Por favor ingresa un número válido entre " + min + " y " + max + ".", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
