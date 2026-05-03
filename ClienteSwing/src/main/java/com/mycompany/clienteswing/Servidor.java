/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.clienteswing;

/**
 *
 * @author Agustin Diaz
 */


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Servidor {
    private static Map<String, ClienteHandler> clientes = new ConcurrentHashMap<>();
    private static int contadorClientes = 1;

    public static void main(String[] args) {
        int puerto = 5000;
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado en puerto " + puerto);

            while (true) {
                Socket socket = serverSocket.accept();
                String nombreCliente = "C" + contadorClientes++;
                ClienteHandler handler = new ClienteHandler(socket, nombreCliente);
                clientes.put(nombreCliente, handler);
                new Thread(handler).start();
                System.out.println("Cliente conectado: " + nombreCliente + " desde " + socket.getInetAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para enviar mensaje a todos
    public static void broadcast(String mensaje, String remitente) {
        for (ClienteHandler cliente : clientes.values()) {
            cliente.enviarMensaje(remitente + " dice a TODOS: " + mensaje);
        }
    }

    // Método para enviar mensaje a un cliente específico
    public static void enviarPrivado(String destino, String mensaje, String remitente) {
        ClienteHandler cliente = clientes.get(destino);
        if (cliente != null) {
            cliente.enviarMensaje(remitente + " dice a " + destino + ": " + mensaje);
        } else {
            ClienteHandler remitenteCliente = clientes.get(remitente);
            if (remitenteCliente != null) {
                remitenteCliente.enviarMensaje("Error: el cliente " + destino + " no existe.");
            }
        }
    }

    // Método para evaluar expresiones matemáticas
    public static double evaluarExpresion(String expresion) {
        Expression e = new ExpressionBuilder(expresion).build();
        return e.evaluate();
    }

    // Clase interna para manejar cada cliente
    static class ClienteHandler implements Runnable {
        private Socket socket;
        private String nombre;
        private PrintWriter salida;
        private BufferedReader entrada;

        public ClienteHandler(Socket socket, String nombre) {
            this.socket = socket;
            this.nombre = nombre;
        }

        @Override
        public void run() {
            try {
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                salida = new PrintWriter(socket.getOutputStream(), true);

                salida.println("Bienvenido " + nombre);
                salida.println("Comandos disponibles:");
                salida.println("*ALL <mensaje> → enviar a todos");
                salida.println("*C# <mensaje> → enviar a cliente específico");
                salida.println("RESOLVE <expresión> → resolver operación matemática");
                salida.println("LIST → listar clientes conectados");
                salida.println("DATE → consultar fecha y hora");
                salida.println("salir → cerrar conexión");

                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    System.out.println(nombre + " dice: " + mensaje);

                    if (mensaje.equalsIgnoreCase("salir")) {
                        salida.println("Conexión finalizada.");
                        break;
                    } else if (mensaje.startsWith("RESOLVE")) {
                        try {
                            String expresion = mensaje.replace("RESOLVE", "").trim();
                            double resultado = evaluarExpresion(expresion);
                            salida.println("Resultado: " + resultado);
                        } catch (Exception e) {
                            salida.println("Error al resolver la expresión.");
                        }
                    } else if (mensaje.equalsIgnoreCase("LIST")) {
                        salida.println("Clientes conectados: " + clientes.keySet());
                    } else if (mensaje.equalsIgnoreCase("DATE")) {
                        salida.println("Fecha y hora actual: " + new Date());
                    } else if (mensaje.startsWith("*ALL")) {
                        String msg = mensaje.replace("*ALL", "").trim();
                        broadcast(msg, nombre);
                    } else if (mensaje.startsWith("*C")) {
                        String[] partes = mensaje.split(" ", 2);
                        if (partes.length == 2) {
                            String destino = partes[0].substring(1); // Ej: *C2 → C2
                            String msg = partes[1];
                            enviarPrivado(destino, msg, nombre);
                        } else {
                            salida.println("Formato incorrecto. Usa: *C# <mensaje>");
                        }
                    } else {
                        salida.println("Servidor recibió: " + mensaje);
                    }
                }

                socket.close();
                clientes.remove(nombre);
                System.out.println("Cliente " + nombre + " desconectado.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void enviarMensaje(String mensaje) {
            salida.println(mensaje);
        }
    }
}
