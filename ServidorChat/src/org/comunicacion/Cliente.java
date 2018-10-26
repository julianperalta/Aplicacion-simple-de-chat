package org.comunicacion;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;

public class Cliente extends Thread {

    private final int LOGIN = 1;
    private final int LOGOFF = 2;

    private final Socket clienteSocket;
    private final Servidor servidor;
    private String nombre;
    private final int id;
    private DataInputStream streamEntrada;
    private DataOutputStream streamSalida;

    public Cliente(Servidor server, Socket clienteSocket, int clientesConectados) {
        this.servidor = server;
        this.clienteSocket = clienteSocket;
        this.id = clientesConectados;
    }

    @Override
    public void run() {
        try {
            setearCliente();
            manejarCliente();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manejarCliente() throws IOException {

        String msj;

        //Envio que me conecte a la sala a todos los clientes presentes
        servidor.enviarBroadcast(this, LOGIN);

        BufferedReader lector = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
        //Uso BufferedReader porque usar InputStream.readLine() esta obsoleto segun la documentacion

        while((msj = lector.readLine()) != null) {
            if("salir".equalsIgnoreCase(msj)) {
                lector.close();
                cerrarSocketCliente();
                return;
            }

            servidor.enviarMsj(this, msj);
        }
    }

    public void recibirMsj(String msj) throws IOException {
        streamSalida.write(msj.getBytes());
    }

    private void cerrarSocketCliente() throws IOException {
        servidor.enviarBroadcast(this, LOGOFF);
        streamSalida.write(("Se cerrara tu conexion al servidor, hasta luego!\n").getBytes());

        servidor.removerCliente(this);
        streamEntrada.close();
        streamSalida.close();
        clienteSocket.close();
    }

    private void setearCliente() throws IOException {
        this.streamSalida = new DataOutputStream(clienteSocket.getOutputStream());
        this.streamEntrada = new DataInputStream(clienteSocket.getInputStream());

        setNombre();
    }

    private void setNombre() throws IOException {
        streamSalida.write(("Escribi tu nombre: ").getBytes());
        BufferedReader lector = new BufferedReader(new InputStreamReader(streamEntrada));
        nombre = lector.readLine();
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(this.clienteSocket, cliente.clienteSocket);
    }
}
