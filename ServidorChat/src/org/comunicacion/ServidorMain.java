package org.comunicacion;

public class ServidorMain {

    public static void main(String[] args) {
        int puerto = 8818;

        Servidor server = new Servidor(puerto);
        server.start();
    }


}
