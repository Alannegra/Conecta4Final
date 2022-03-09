package com.company;

import java.io.*;
import java.net.Socket;

public class ThreadServidorAdivina_Obj implements Runnable {
    /* Thread que gestiona la comunicació de SrvTcPAdivina_Obj.java i un cllient ClientTcpAdivina_Obj.java */

    private Socket clientSocket = null;
    private InputStream in = null;
    private OutputStream out = null;
    private SecretNum ns;
    private Tauler tauler;
    private boolean acabat;
    private boolean ganar = false;

    public ThreadServidorAdivina_Obj(Socket clientSocket, SecretNum ns, Tauler t, int turno) throws IOException {
        this.clientSocket = clientSocket;
        this.ns = ns;
        tauler = t;
        //Al inici de la comunicació el resultat ha de ser diferent de 0(encertat)
        tauler.resultat = 3;
        acabat = false;
        //Enllacem els canals de comunicació
        in = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();
        System.out.println("canals i/o creats amb un nou jugador");
    }

    @Override
    public void run() {
        Jugada j = null;
        try {
            while(!acabat) {

                //Enviem tauler al jugador
                ObjectOutputStream oos = new ObjectOutputStream(out);
                oos.writeObject(tauler);
                oos.flush();

                //Llegim la jugada
                ObjectInputStream ois = new ObjectInputStream(in);
                try {
                    j = (Jugada) ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("jugada: " + j.Nom + "->" + j.num);
                if(!tauler.map_jugadors.containsKey(j.Nom)){
                    tauler.map_jugadors.put(j.Nom, Integer.parseInt(j.numeroDeJugador));
                }
                else {
                    //Si el judador ja esxiteix, actualitzem la quatitat de tirades
                    int tirades = tauler.map_jugadors.get(j.Nom) + 1;

                    int a = Integer.parseInt(j.numeroDeJugador);


                    if (tauler.map_jugadors.get(j.Nom) == tauler.turno) {

                        for (int i = 5; i > -1; i--) {
                            if (tauler.matrix[i][j.num-1] == 0){
                                tauler.matrix[i][j.num-1] = Integer.parseInt(j.numeroDeJugador);
                                break;
                            }
                        }

                        int puntos = 0;

                        for (int i = 0; i < tauler.matrix.length - 1; i++) {
                            if (tauler.matrix[i][j.num-1] == Integer.parseInt(j.numeroDeJugador) && tauler.matrix[i+1][j.num-1] == tauler.matrix[i][j.num-1]){
                                puntos++;
                            }else{
                                puntos = 0;}

                        }
                        if (puntos == 3){
                            ganar = true;
                            System.out.println("ganar");
                        }

                        tauler.cambioTurno();
                    }
                    Thread.sleep(5000);
                    tauler.map_jugadors.put(j.Nom, a);
                    //System.out.println(j.OtroInt + "XD");


                    /*for (int i = 0; i < tauler.matrix.length; i++) {
                        for (int k = 0; k < tauler.matrix[i].length; k++) {
                            if(j.num == k && tauler.matrix[i][k] == 0){
                                tauler.matrix[i][k] = Integer.parseInt(j.numeroDeJugador);
                                break;
                            }else{
                                if(j.num == k && i <5){
                                    tauler.matrix[i+1][k] = Integer.parseInt(j.numeroDeJugador);
                                    break;
                                }

                            }

                        }
                    }*/




                }

                //comprobar la jugada i actualitzar tauler amb el resultat de la jugada
                //tauler.resultat = ns.comprova(j.num);
                tauler.resultat = 4;
                if(ganar) {
                    acabat = true;
                    System.out.println(j.Nom + " A Guanyat!");
                    tauler.acabats++;
                    tauler.resultat = 0;
                    tauler.cambioTurno();
                }
            }
        }catch(IOException | InterruptedException e){
            System.out.println(e.getLocalizedMessage());
        }
        //Enviem últim estat del tauler abans de acabar amb la comunicació i acabem
        try {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(tauler);
            oos.flush();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
