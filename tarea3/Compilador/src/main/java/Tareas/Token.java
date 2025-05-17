package Tareas;

public class Token {

    private String value;
    private Tipos tipo;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Tipos getTipo() {
        return tipo;
    }

    public void setTipo(Tipos tipo) {
        this.tipo = tipo;
    }

    enum Tipos {
        L_CORCHETE("\\["),
        R_CORCHETE("\\]"),
        L_LLAVE("\\{"),
        R_LLAVE("\\}"),
        COMA(","),
        DOS_PUNTOS(":"),
        LITERAL_CADENA("\"(?:\\\\.|[^\"\\\\])*\""),
        LITERAL_NUM("-?[0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?"),
        PR_TRUE("true|TRUE"),
        PR_FALSE("false|FALSE"),
        PR_NULL(null),
        EOF("EOF");

        public final String patron;

        Tipos(String s) {
            this.patron = s;
        }
    }
}