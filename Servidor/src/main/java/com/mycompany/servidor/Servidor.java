/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
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
import java.net.ServerSocket;
import java.net.Socket;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Servidor {
    public static void main(String[] args) {
        int puerto = 5000;
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado en puerto " + puerto);

            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado: " + socket.getInetAddress());

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Cliente dice: " + mensaje);

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
                } else {
                    salida.println("Servidor recibió: " + mensaje);
                }
            }

            socket.close();
            System.out.println("Servidor cerrado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método simple para evaluar expresiones matemáticas
    public static double evaluarExpresion(String expresion) {
     Expression e = new ExpressionBuilder(expresion).build();
    return e.evaluate();
}
}

