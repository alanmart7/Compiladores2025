package Tareas;

import java.util.ArrayList;
import java.util.Iterator;

public class Parser {
    private Iterator<Token> tokens;
    private Token currentToken;
    private StringBuilder xmlOutput;
    private int indentLevel;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens.iterator();
        this.xmlOutput = new StringBuilder();
        this.indentLevel = 0;
        this.advance(); // Inicializa el primer token
    }

    public String getXmlOutput() {
        return xmlOutput.toString();
    }

    public void parse() {
        try {
            parseJson(); // Inicia desde la raíz del JSON
            if (currentToken.getTipo() == Token.Tipos.EOF) {
                System.out.println("El archivo JSON es sintácticamente correcto.");
            } else {
                throw new RuntimeException("Error: Símbolos inesperados después del final del JSON.");
            }
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    private void parseJson() {
        if (currentToken.getTipo() == Token.Tipos.L_LLAVE) {
            parseObject();
        } else if (currentToken.getTipo() == Token.Tipos.L_CORCHETE) {
            parseArray();
        } else {
            throw new RuntimeException("Error: Se esperaba un objeto o un arreglo JSON.");
        }
    }

    private void parseObject() {
        appendIndented("<object>");
        indentLevel++;

        match(Token.Tipos.L_LLAVE);

        if (currentToken.getTipo() != Token.Tipos.R_LLAVE) {
            parseMembers();
        }

        match(Token.Tipos.R_LLAVE);

        indentLevel--;
        //appendIndented("</object>");
    }

    private void parseMembers() {
        parsePair();

        while (currentToken.getTipo() == Token.Tipos.COMA) {
            match(Token.Tipos.COMA);
            parsePair();
        }
    }

    private void parsePair() {
        if (currentToken.getTipo() == Token.Tipos.LITERAL_CADENA) {
            String key = currentToken.getValue().replace("\"", "");
            match(Token.Tipos.LITERAL_CADENA);
            match(Token.Tipos.DOS_PUNTOS);

            appendIndented("<" + key + ">");
            indentLevel++;
            parseValue();
            indentLevel--;
            appendIndented("</" + key + ">");
        } else {
            throw new RuntimeException("Error: Se esperaba una cadena como clave del objeto JSON.");
        }
    }

    private void parseArray() {
        appendIndented("<item>");
        indentLevel++;

        match(Token.Tipos.L_CORCHETE);

        if (currentToken.getTipo() != Token.Tipos.R_CORCHETE) {
            parseElements();
        }

        match(Token.Tipos.R_CORCHETE);

        indentLevel--;
        appendIndented("</item>");
    }

    private void parseElements() {
        parseValue();

        while (currentToken.getTipo() == Token.Tipos.COMA) {
            match(Token.Tipos.COMA);
            parseValue();
        }
    }

    private void parseValue() {
        switch (currentToken.getTipo()) {
            case L_LLAVE:
                parseObject();
                break;
            case L_CORCHETE:
                parseArray();
                break;
            case LITERAL_CADENA:
            case LITERAL_NUM:
            case PR_TRUE:
            case PR_FALSE:
            case PR_NULL:
                appendIndented(currentToken.getValue());
                advance();
                break;
            default:
                throw new RuntimeException("Error: Valor inesperado en JSON.");
        }
    }

    private void match(Token.Tipos expected) {
        if (currentToken.getTipo() == expected) {
            advance();
        } else {
            System.err.println("Error: Se esperaba " + expected + " pero se encontró " + currentToken.getTipo());
            panicMode();
        }
    }

    private void advance() {
        if (tokens.hasNext()) {
            currentToken = tokens.next();
        } else {
            currentToken = new Token();
            currentToken.setTipo(Token.Tipos.EOF);
        }
    }

    private void appendIndented(String text) {
        for (int i = 0; i < indentLevel; i++) {
            xmlOutput.append("  ");
        }
        xmlOutput.append(text).append("\n");
    }

    private void panicMode() {
        while (tokens.hasNext()) {
            advance();
            if (currentToken.getTipo() == Token.Tipos.COMA || 
                currentToken.getTipo() == Token.Tipos.R_LLAVE || 
                currentToken.getTipo() == Token.Tipos.R_CORCHETE || 
                currentToken.getTipo() == Token.Tipos.EOF) {
                break;
            }
        }
    }
}
