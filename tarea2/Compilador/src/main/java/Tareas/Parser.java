package Tareas;

import java.util.ArrayList;
import java.util.Iterator;

public class Parser {
    private Iterator<Token> tokens;
    private Token currentToken;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens.iterator();
        this.advance(); // Inicializa el primer token
    }

    // Método principal para iniciar el análisis
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

    // Avanza al siguiente token
    private void advance() {
        if (tokens.hasNext()) {
            currentToken = tokens.next();
        } else {
            currentToken = new Token(); // Token EOF implícito si no hay más tokens
            currentToken.setTipo(Token.Tipos.EOF);
        }
    }

    // Método para validar la estructura JSON
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
        match(Token.Tipos.L_LLAVE);

        if (currentToken.getTipo() != Token.Tipos.R_LLAVE) {
            parseMembers();
        }

        match(Token.Tipos.R_LLAVE);
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
            match(Token.Tipos.LITERAL_CADENA);
            match(Token.Tipos.DOS_PUNTOS);
            parseValue();
        } else {
            throw new RuntimeException("Error: Se esperaba una cadena como clave del objeto JSON.");
        }
    }

    private void parseArray() {
        match(Token.Tipos.L_CORCHETE);

        if (currentToken.getTipo() != Token.Tipos.R_CORCHETE) {
            parseElements();
        }

        match(Token.Tipos.R_CORCHETE);
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
                advance();
                break;
            default:
                throw new RuntimeException("Error: Valor inesperado en JSON.");
        }
    }

    // Método para verificar y avanzar al siguiente token
    private void match(Token.Tipos expected) {
        if (currentToken.getTipo() == expected) {
            advance();
        } else {
            System.err.println("Error: Se esperaba " + expected + " pero se encontró " + currentToken.getTipo());
            panicMode();
        }
    }

    // Método de sincronización para Panic Mode
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

