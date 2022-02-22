package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SrvTcpAdivina_Obj {
    /* Servidor TCP que genera un número perquè ClientTcpAdivina_Obj.java jugui a encertar-lo
     * i on la comunicació dels diferents jugadors la gestionaran els Threads : ThreadServidorAdivina_Obj.java
     * */

    private int port;
    private SecretNum ns;
    private Tauler t;
    private int turno;
    private int numeroDeJugadores;

    private SrvTcpAdivina_Obj(int port ) {
        this.port = port;
        ns = new SecretNum(100);

    }

    private void listen() {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while(true) { //esperar connexió del client i llançar thread
                clientSocket = serverSocket.accept();
                //Llançar Thread per establir la comunicació
                //sumem 1 al numero de jugadors
                numeroDeJugadores++;

                if(numeroDeJugadores % 2 !=0 ){
                    turno = 1;
                    t = new Tauler();
                }else{
                    turno = 2;
                }

                t.addNUmPlayers();

                ThreadServidorAdivina_Obj FilServidor = new ThreadServidorAdivina_Obj(clientSocket, ns, t,turno);
                Thread client = new Thread(FilServidor);
                client.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(SrvTcpAdivina_Obj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        SrvTcpAdivina_Obj srv = new SrvTcpAdivina_Obj(5558);
        srv.listen();
    }
}