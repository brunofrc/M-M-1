package com.ms;

import java.util.*;

public class MM1Queue {


    LinkedList<Event> events;
    int tcOption;
    int tsOption;
    double relogioDeSimulacao;
    double TR; //Tempo da simulação
    int TF;    //Tamanho da fila
    double HC; //Tempo agendado para a próxima chegada
    double HS; //Tempo agendado para a próxima saída
    double lambda;
    double ts; //Tempo de serviço

    ArrayList<Double> imagemDoSistema;
    int numImagens;

    //variaveis para estatisticas
    double Wq, W, Ws;
    int requests, served;


    public MM1Queue(double lambda, double ts, double TR, double tc, int tcOption) {
        this.lambda = lambda;
        this.ts = ts;
        this.TR = TR;
        this.tcOption = tcOption;

        imagemDoSistema = new ArrayList<Double>();
        double n = 0;
        while (n < 2* TR) {
            numImagens++;
            n += exponencial(lambda);
            imagemDoSistema.add(n);
        }


        relogioDeSimulacao = 0;
        events = new LinkedList<Event>();
        //TEC
        if(tcOption != 0) {
            if (tcOption == 1) {
                HC = normal();
            } else if (tcOption == 2) {
                HC = poisson(lambda);
            } else {
                HC = exponencial(lambda);
            }
        }else{
            HC = tc;
        }
        HS = Double.POSITIVE_INFINITY;

        Wq = 0; W = 0; Ws = 0;
        TF = 0;
        requests = 0; served = 0;
    }


    public void arrive(double time) {
        if (events.isEmpty()) {
            scheduleDeparture(time);
        }
        else {
            events.add(new Event(time,"arrival"));
        }
        if(tcOption != 0) {
            if (tcOption == 1) {
                HC += normal();
            } else if (tcOption == 2) {
                HC += poisson(lambda);
            } else {
                HC += exponencial(lambda);
            }
        }else{
            HC += HC;
        }
    }


    public void departure() {
        events.remove();
        if (!events.isEmpty()) {
            Event next = events.remove();
            scheduleDeparture(next.getTime());
        } else{
            HS = Double.POSITIVE_INFINITY;
            //TEC
            if(tcOption != 0) {
                if (tcOption == 1) {
                    HC += normal();
                } else if (tcOption == 2) {
                    HC += poisson(lambda);
                } else {
                    HC += exponencial(lambda);
                }
            }else{
                HC += HC;
            }
        }
    }

    public void scheduleDeparture(double arrivalTime) {
        HS = relogioDeSimulacao + exponencial(1/ ts);
        events.addFirst(new Event(HS, "departure"));
        served++;
        Wq += (HS - arrivalTime);
        Ws += (HS - relogioDeSimulacao);
        W += (relogioDeSimulacao - arrivalTime);
    }

    public void monitor() {
        int currentQ = events.size();
        TF += currentQ;
        if(imagemDoSistema.size() > 0) {
            imagemDoSistema.remove(0);
        }
        System.out.println("-Imagem do sistema no relogio: " + relogioDeSimulacao);
        System.out.println("\tclientes na fila (TF): " + currentQ);


    }

    public static double exponencial(double lambda) {
        Random r = new Random();
        double x = Math.log(1-r.nextDouble())/(-lambda);
        return x;
    }

    public static double poisson(double lambda) {
        Random random = new Random();
        double r = 0;
        double a = random.nextDouble();
        double p = Math.exp(-lambda);

        while (a > p) {
            r++;
            a = a - p;
            p = p * lambda / r;
        }
        return r;
    }
    public static double normal() {
        Random random = new Random();
        return random.nextGaussian();
    }


    public void simular() {
        while (relogioDeSimulacao < 2* TR) {
            if (imagemDoSistema.get(0) < HC && imagemDoSistema.get(0) < HS) {
                relogioDeSimulacao = imagemDoSistema.get(0);
                monitor();
            }
            else if (HC <= HS) {
                relogioDeSimulacao = HC;
                System.out.println("-Evento chegada em: " + relogioDeSimulacao);
                arrive(HC);
                requests++;
            }
            else {
                relogioDeSimulacao = HS;
                System.out.println("-Evento saida em: " + relogioDeSimulacao);
                departure();
            }
        }
        System.out.println("-Evento fim da simulacao");
        System.out.println("-Estatisticas: ");
        System.out.println("\tNumero de entidades servidas: " + served);
        System.out.println("\tQuantidade media de entidades na fila = " + TF /numImagens);
        System.out.println("\tTempo medio de espera = " + W / served);
        System.out.println("\tTempo medio na fila = " + Wq/ served);
        System.out.println("\tTempo medio de servico = " + Ws / served);
    }
}
