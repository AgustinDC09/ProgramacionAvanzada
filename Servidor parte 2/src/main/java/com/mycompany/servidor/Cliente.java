/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.servidor;

/**
 *
 * @author Agustin Diaz
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 5000;

        try (Socket socket = new Socket(host, puerto);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado al servidor en " + host + ":" + puerto);

            String mensaje;
            while (true) {
                System.out.print("Escribe mensaje (o 'salir' para terminar): ");
                mensaje = scanner.nextLine();
                salida.println(mensaje);

                if (mensaje.equalsIgnoreCase("salir")) {
                    System.out.println("Finalizando conexión...");
                    break;
                }

                // Leer todas las respuestas que envíe el servidor
                leerRespuestas(entrada);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para leer todas las líneas que envía el servidor
    private static void leerRespuestas(BufferedReader entrada) throws IOException {
        String respuesta;
        while ((respuesta = entrada.readLine()) != null) {
            if (!respuesta.trim().isEmpty()) { // evita imprimir vacío
                System.out.println("Servidor responde: " + respuesta);
            }
            if (!entrada.ready()) {
                break;
            }
        }
    }

}

