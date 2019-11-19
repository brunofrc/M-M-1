package com.ms;

import java.util.*;

public class MM2Queue {


    Map<Integer, LinkedList<Event>> mapServerEvents;
    int tcOption;
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


    public MM2Queue(double lambda, double ts, double TR, double tc, int tcOption) {
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
        mapServerEvents = new HashMap<>();
        mapServerEvents.put(1, new LinkedList<>());
        mapServerEvents.put(2, new LinkedList<>());
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
            if(mapServerEvents.get(1).isEmpty()){
                scheduleDeparture(time);
                mapServerEvents.get(1).addFirst(new Event(HS, "departure"));
            }else if(mapServerEvents.get(2).isEmpty()) {
                scheduleDeparture(time);
                mapServerEvents.get(2).addFirst(new Event(HS, "departure"));
            }else if(!mapServerEvents.get(1).isEmpty()){
                mapServerEvents.get(1).add(new Event(time,"arrival"));
            }else if(!mapServerEvents.get(2).isEmpty()){
                mapServerEvents.get(2).add(new Event(time,"arrival"));
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


    public void departure(int server) {

        if(server == 1) {
            mapServerEvents.get(1).remove();
            if(!mapServerEvents.get(1).isEmpty()) {
                Event next1 = mapServerEvents.get(1).remove();
                scheduleDeparture(next1.getTime());
            }
        }else {
            mapServerEvents.get(2).remove();
            if(!mapServerEvents.get(2).isEmpty()) {
                Event next2 = mapServerEvents.get(2).remove();
                scheduleDeparture(next2.getTime());
            }
        }
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

    public void scheduleDeparture(double arrivalTime) {
        HS = relogioDeSimulacao + exponencial(1/ ts);

        served++;
        Wq += (HS - arrivalTime);
        Ws += (HS - relogioDeSimulacao);
        W += (relogioDeSimulacao - arrivalTime);
    }

    public void monitor() {
        int currentQ = mapServerEvents.get(1).size() +  mapServerEvents.get(2).size();
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
                if (!mapServerEvents.get(1).isEmpty() && mapServerEvents.get(1).getFirst().getTime() <= relogioDeSimulacao){
                    departure(1);
                }
                if(!mapServerEvents.get(2).isEmpty() && mapServerEvents.get(2).getFirst().getTime() <= relogioDeSimulacao){
                    departure(2);
                }
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
