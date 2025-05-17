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
        String outputFilePath = "C:/Analizador/salida.xml"; // Ruta del archivo XML de salida

        try {
            String entrada = new String(Files.readAllBytes(Paths.get(filePath)));
            ArrayList<Token> tokens = lex(entrada);

            // Proceso de análisis sintáctico y traducción a XML
            Parser parser = new Parser(tokens);
            parser.parse();

            // Guardar la salida XML
            String xmlOutput = parser.getXmlOutput();
            Files.write(Paths.get(outputFilePath), xmlOutput.getBytes(), StandardOpenOption.CREATE);

            System.out.println("Traducción completada. La salida XML se ha guardado en " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
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