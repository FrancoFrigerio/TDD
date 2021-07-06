package ar.com.frigerio.springboot.app.exceptions;

public class DineroInsuficienteException extends  RuntimeException{

    public DineroInsuficienteException(String message) {
        super(message);
    }
}
