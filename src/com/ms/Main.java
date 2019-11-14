package com.ms;


import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        double lambda = 60;
        double ts = 0.015;
        double tc = 0;
        double length = 100;
        int tcOption = 0;
        Scanner s = new Scanner(System.in);

        System.out.println("Digite a taxa de chegada: ");
        lambda = s.nextDouble();

        int opcaoTc = 0;
        do {
            System.out
                    .println("\n\n### Tempo de chegada ###");
            System.out.println("\n=========================");
            System.out.println("|     1 - Deterministico ");
            System.out.println("|     2 - Aleatorio      ");
            System.out.println("=========================\n");

            opcaoTc = s.nextInt();
            System.out.print("\n");
            switch (opcaoTc) {
                case 1:
                    System.out.println("Digite o valor: ");
                    tc = s.nextDouble();
                    break;
                case 2:
                    System.out.println("\n=========================");
                    System.out.println("|1 - Distibuicao normal      ");
                    System.out.println("|2 - Distribuicao poisson");
                    System.out.println("|3 - Distribuicao exponencial");
                    System.out.println("=========================\n");
                    tcOption = s.nextInt();
                    if (tcOption == 1) {
                        tc = MM1Queue.normal();
                    } else {
                        if (tcOption == 2) {
                            tc = MM1Queue.poisson(lambda);
                        } else {
                            tc = MM1Queue.exponencial(lambda);
                        }
                    }
                    break;
                default:
                    System.out.println("Opção Inválida!");
                    break;
            }
        } while (opcaoTc == 0);

        int opcaoTs = 0;
        do {
            System.out
                    .println("\n\n### Tempo de servico ###");
            System.out.println("\n=========================");
            System.out.println("|     1 - Deterministico ");
            System.out.println("|     2 - Aleatorio      ");
            System.out.println("=========================\n");

            opcaoTs = s.nextInt();
            System.out.print("\n");
            switch (opcaoTs) {
                case 1:
                    System.out.println("Digite o valor: ");
                    ts = s.nextDouble();
                    break;
                case 2:
                    System.out.println("\n=========================");
                    System.out.println("|1 - Distibuicao normal      ");
                    System.out.println("|2 - Distribuicao poisson");
                    System.out.println("|3 - Distribuicao exponencial");
                    System.out.println("=========================\n");
                    int opcaoDist = s.nextInt();
                    if (opcaoDist == 1) {
                        ts = MM1Queue.normal();
                    } else {
                        if (opcaoDist == 2) {
                            ts = MM1Queue.poisson(lambda);
                        } else {
                            ts = MM1Queue.exponencial(lambda);
                        }
                    }
                    break;
                default:
                    System.out.println("Opção Inválida!");
                    break;
            }
        } while (opcaoTs == 0);

        MM1Queue c1 = new MM1Queue(lambda, ts, length, tc, tcOption);

        c1.simular();

    }
}