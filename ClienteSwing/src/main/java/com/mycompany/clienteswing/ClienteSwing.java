/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.clienteswing;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
/**
 *
 * @author Agustin Diaz
 */


public class ClienteSwing extends JFrame {
    private JTextField txtUsuario, txtMensaje;
    private JTextArea areaChat;
    private JButton btnConectar, btnEnviar, btnSalir;
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private Thread listenerThread;

    public ClienteSwing() {
        super("Chat Cliente");
        setSize(500,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        txtUsuario = new JTextField(15);
        txtMensaje = new JTextField(30);
        areaChat = new JTextArea();
        areaChat.setEditable(false);

        btnConectar = new JButton("Conectar");
        btnEnviar = new JButton("Enviar");
        btnSalir = new JButton("Salir");

        JPanel panelTop = new JPanel();
        panelTop.add(new JLabel("Usuario:"));
        panelTop.add(txtUsuario);
        panelTop.add(btnConectar);

        JPanel panelBottom = new JPanel();
        panelBottom.add(txtMensaje);
        panelBottom.add(btnEnviar);
        panelBottom.add(btnSalir);

        add(panelTop, BorderLayout.NORTH);
        add(new JScrollPane(areaChat), BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);

        configurarEventos();
    }

    private void configurarEventos() {
        btnConectar.addActionListener(e -> conectar());
        btnEnviar.addActionListener(e -> enviarMensaje());
        btnSalir.addActionListener(e -> salir());

        // Validación: no permitir espacios en el nombre
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isWhitespace(c)) e.consume();
            }
        });
    }

    private void conectar() {
        try {
            socket = new Socket("localhost", 5000);
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            areaChat.append("Conectado al servidor.\n");

            listenerThread = new Thread(() -> {
                try {
                    String respuesta;
                    while ((respuesta = entrada.readLine()) != null) {
                        areaChat.append("Servidor: " + respuesta + "\n");
                    }
                } catch (IOException ex) {
                    areaChat.append("Conexión cerrada.\n");
                }
            });
            listenerThread.start();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + ex.getMessage());
        }
    }

    private void enviarMensaje() {
        if (salida != null) {
            String msg = txtMensaje.getText().trim();
            if (!msg.isEmpty()) {
                salida.println(msg);
                areaChat.append("Yo: " + msg + "\n");
                txtMensaje.setText("");
            }
        }
    }

    private void salir() {
        if (salida != null) salida.println("salir");
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        System.exit(0);
    }

    public static void main(String[] args) {
        new ClienteSwing().setVisible(true);
    }
}

