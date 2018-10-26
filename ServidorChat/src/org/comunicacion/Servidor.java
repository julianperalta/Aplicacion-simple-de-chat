package org.comunicacion;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.System.out;

public class Servidor extends Thread {

    private final int LOGIN = 1;
    private final int LOGOFF = 2;

    private int puertoServidor;
    private ArrayList<Cliente> listaClientes;

    public Servidor(int puerto) {
        this.puertoServidor = puerto;
        this.listaClientes = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(puertoServidor);
            int nroCliente = 0;

            while(true) {

                out.println("Se esta esperando la conexion de un cliente...");
                Socket clienteSocket = serverSocket.accept();
                nroCliente++;
                out.println("Se acepto a un nuevo cliente");

                Cliente cliente = new Cliente(this, clienteSocket, nroCliente);
                listaClientes.add(cliente);
                cliente.start();
            }

            // serverSocket.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    /*public ArrayList<Cliente> getListaClientes() {
        return listaClientes;
    }*/

    public void removerCliente(Cliente cliente) {
        listaClientes.remove(cliente);
    }

    public synchronized void enviarBroadcast(Cliente sender, int tipo) {
        for (Cliente c : listaClientes) {
            if(!c.equals(sender)) {
                try {
                    if (tipo == LOGIN)
                        c.recibirMsj(sender.getNombre() + " se unio a la sala\n");
                    else
                        c.recibirMsj(sender.getNombre() + " salio de la sala\n");
                } catch (IOException e) {
                    out.println("Hubo un error al hacer el broadcast. Error: " + e);
                }
            }
        }
    }

    public synchronized void enviarMsj(Cliente sender, String msj) {
        for (Cliente c : listaClientes) {
            if(!c.equals(sender)) {
                try {
                    c.recibirMsj(sender.getNombre() + ": " + msj + "\n");
                } catch (IOException e) {
                    out.println("Hubo un error al enviar el mensaje. Error: " + e);
                }
            }
        }
    }
}
