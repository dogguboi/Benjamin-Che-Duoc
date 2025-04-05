/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.e2_s4_benjamin_chec;

import java.util.Scanner;

/**
 *
 * @author doggu
 */
public class E2_S4_Benjamin_Chec {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        System.out.println("Bienvenidos al Teatro Moro!");
        
        boolean continuar = true;
        while (continuar) {
            System.out.println("Menu Teatro Moro");
            System.out.println("1. Comprar entrada");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opcion: ");
            int opcion = input.nextInt();
            
            if (opcion == 2) {
                continuar = false;
                continue;
            } else if (opcion != 1) {
                System.out.println("Opcion no valida");
                continue;
            }
            
            int tipoEntrada = 0;
            double valorPagar = 0;
            
            System.out.println("---------------------");
            System.out.println("                     ");
            System.out.println("      ESCENARIO      ");
            System.out.println("                     ");
            System.out.println("---------------------");
            System.out.println("       Zona A        ");
            System.out.println("         VIP         ");
            System.out.println("---------------------");
            System.out.println("       Zona B        ");
            System.out.println("     Platea baja     ");
            System.out.println("---------------------");
            System.out.println("       Zona C        ");
            System.out.println("     Platea alta     ");
            System.out.println("---------------------");
            System.out.println("       Zona D        ");
            System.out.println("       Palcos        ");
            System.out.println("---------------------");
            
            // Tipos de entrada
            while (tipoEntrada < 1 || tipoEntrada > 4) {
                System.out.println("Tipos de entrada disponibles:");
                System.out.println("1. VIP (Zona 1) - $30,000");
                System.out.println("2. Platea baja (Zona 2) - $15,000");
                System.out.println("3. Platea alta (Zona 3) - $18,000");
                System.out.println("4. Palcos (Zona 4) - $13,000");
                System.out.print("Seleccione el tipo de entrada, del 1 al 4): ");
                tipoEntrada = input.nextInt();
                if (tipoEntrada < 1 || tipoEntrada > 4) {
                    System.out.println("Opcion no valida. Intentelo de nuevo.");
                }
            }
            
            // Edad
            System.out.print("Ingrese su edad: ");
            int edad = input.nextInt();
            
            // Proceso de calculo
            switch (tipoEntrada) {
                case 1: valorPagar = 30000; break;
                case 2: valorPagar = 15000; break;
                case 3: valorPagar = 18000; break;
                case 4: valorPagar = 13000; break;
            }
            
            String tipoTarifa;
            if (edad < 20) {
                valorPagar *= 0.90;
                tipoTarifa = "Estudiante (10% descuento)";
            } else if (edad > 60) {
                valorPagar *= 0.85;
                tipoTarifa = "Tercera edad (15% descuento)";
            } else {
                tipoTarifa = "General";
            }
            
            // Compra final
            System.out.println("DETALLES DE COMPRA");
            System.out.println("Tipo de entrada: " + 
                (tipoEntrada == 1 ? "VIP" :
                 tipoEntrada == 2 ? "Platea baja" :
                 tipoEntrada == 3 ? "Platea alta" :
                 "Palcos"));
            System.out.println("Tipo de tarifa: " + tipoTarifa);
            System.out.println("Valor a pagar: $" + valorPagar);
            
            // Salida 
            System.out.println("Gracias por su compra, disfrute la funcion.");
        }
        input.close();
    }
}
