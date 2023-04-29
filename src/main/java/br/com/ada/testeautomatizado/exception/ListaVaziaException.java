package br.com.ada.testeautomatizado.exception;

public class ListaVaziaException extends RuntimeException {
    public ListaVaziaException() {
        super("Nao ha veiculos a serem listados");
    }
}
