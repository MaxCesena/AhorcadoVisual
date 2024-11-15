import java.util.*;

public class Frases {
    private static final List<String> FRASES = Arrays.asList(
            "mas vale solo que mal acompañado",
            "el tiempo es oro",
            "a mal tiempo buena cara",
            "mas vale tarde que nunca",
            "el que mucho abarca poco aprieta",
            "no hay mal que por bien no venga",
            "el perro que ladra no muerde",
            "mas vale prevenir que lamentar",
            "a caballo regalado no le mires el diente"
    );

    public static String obtenerFraseAleatoria() {
        Random random = new Random();
        return FRASES.get(random.nextInt(FRASES.size()));
    }
}
