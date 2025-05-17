package Tareas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Tareas.Token.Tipos;

public class Principal {
    public static void main(String[] args) {
        String filePath = "C:/Analizador/fuente.json"; // Ruta de archivo de entrada
        String outputFilePath = "C:/Analizador/salida.txt"; // Ruta del archivo de salida

        try {
            String entrada = new String(Files.readAllBytes(Paths.get(filePath)));
            ArrayList<Token> tokens = lex(entrada);
            
            //Proceso de parser - Tarea 2
            Parser parser = new Parser(tokens);
            parser.parse();
            
            StringBuilder outputContent = new StringBuilder();
            int indentLevel = 0;

            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);

                if (token.getTipo() != null) {
                    if (token.getTipo() == Tipos.L_LLAVE || token.getTipo() == Tipos.L_CORCHETE) {
                        appendIndented(outputContent, token.getTipo().toString(), indentLevel);
                        indentLevel++;
                    } else if (token.getTipo() == Tipos.R_LLAVE || token.getTipo() == Tipos.R_CORCHETE) {
                        indentLevel--;
                        appendIndented(outputContent, token.getTipo().toString(), indentLevel);
                    } else {
                        appendIndentedSameLine(outputContent, token.getTipo().toString(), indentLevel);
                    }
                }
            }

            Files.write(Paths.get(outputFilePath), outputContent.toString().getBytes(), StandardOpenOption.CREATE);
            System.out.println("\nAnálisis léxico completado. Los resultados se han guardado en " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void appendIndented(StringBuilder sb, String text, int indentLevel) {
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
            sb.append("\n");
        }
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  "); // Usar dos espacios para cada nivel de indentación
        }
        sb.append(text).append("\n");
    }

    private static void appendIndentedSameLine(StringBuilder sb, String text, int indentLevel) {
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            for (int i = 0; i < indentLevel; i++) {
                sb.append("  "); // Usar dos espacios para cada nivel de indentación
            }
        }
        sb.append(text).append(" ");
    }

    private static ArrayList<Token> lex(String entrada) {
        final ArrayList<Token> tokens = new ArrayList<>();
        final String tokenPatterns = "\\{|\\}|\\[|\\]|,|:|\"(?:\\\\.|[^\"\\\\])*\"|-?[0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?|true|false|null";

        Pattern patron = Pattern.compile(tokenPatterns);
        Matcher matcher = patron.matcher(entrada);

        while (matcher.find()) {
            String texto = matcher.group();

            boolean flag = false;
            for (Tipos tipoToken : Tipos.values()) {
                Pattern tipoPatron = Pattern.compile(tipoToken.patron);
                Matcher buscar = tipoPatron.matcher(texto);

                if (buscar.matches()) {
                    Token token = new Token();
                    token.setTipo(tipoToken);
                    token.setValue(texto);
                    tokens.add(token);
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                throw new RuntimeException("Token Invalido: " + texto);
            }
        }

        return tokens;
    }
}
