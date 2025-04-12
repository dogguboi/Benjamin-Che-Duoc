package com.mycompany.e2_s5_benjamin_che_p;

import java.util.ArrayList;
import java.util.Scanner;

public class E2_S5_Benjamin_Che_P {
    // Variables estaticas para estadísticas
    private static int totalEntradasVendidas = 0;
    private static double ingresosTotales = 0;
    private static int entradasCanceladas = 0;
    private static int proximoNumeroCompra = 1;
    
    // Variables de instancia
    private ArrayList<Entrada> entradasVendidas;
    private boolean[][] asientosDisponibles;
    
    static class Entrada {
        int numeroCompra;
        int tipoEntrada;
        int edad;
        double precio;
        int asiento;
        boolean activa;
        
        public Entrada(int numeroCompra, int tipoEntrada, int edad, double precio, int asiento) {
            this.numeroCompra = numeroCompra;
            this.tipoEntrada = tipoEntrada;
            this.edad = edad;
            this.precio = precio;
            this.asiento = asiento;
            this.activa = true;
        }
    }

    public E2_S5_Benjamin_Che_P() {
        entradasVendidas = new ArrayList<>();
        asientosDisponibles = new boolean[4][40];
        // Aca todos los asientos estaran disponibles
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 40; j++) {
                asientosDisponibles[i][j] = true;
            }
        }
    }

    public static void main(String[] args) {
        E2_S5_Benjamin_Che_P teatro = new E2_S5_Benjamin_Che_P();
        Scanner input = new Scanner(System.in);
        
        System.out.println("Bienvenidos al Teatro Moro!");
        
        boolean continuar = true;
        while (continuar) {
            System.out.println("Menu Teatro Moro");
            System.out.println("1. Comprar entrada");
            System.out.println("2. Ver entradas vendidas");
            System.out.println("3. Buscar entrada por numero de compra");
            System.out.println("4. Cancelar entrada");
            System.out.println("5. Mostrar estadisticas");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opcion: ");
            
            int opcion;
            try {
                opcion = input.nextInt();
            } catch (Exception e) {
                System.out.println("ERROR: Ingrese un numero valido.");
                input.nextLine(); 
                continue;
            }
            
            switch (opcion) {
                case 1:
                    teatro.comprarEntrada(input);
                    break;
                    
                case 2:
                    teatro.mostrarEntradasVendidas();
                    break;
                    
                case 3:
                    teatro.buscarEntrada(input);
                    break;
                    
                case 4:
                    teatro.cancelarEntrada(input);
                    break;
                    
                case 5:
                    teatro.mostrarEstadisticas();
                    break;
                    
                case 6:
                    continuar = false;
                    System.out.println("Gracias por usar el sistema de ventas del Teatro Moro.");
                    break;
                    
                default:
                    System.out.println("Opcion no valida. Intente de nuevo.");
            }
        }
        
        input.close();
    }

    private void comprarEntrada(Scanner input) {
        System.out.println("------------------------------");
        System.out.println("                              ");
        System.out.println("           ESCENARIO          ");
        System.out.println("                              ");
        System.out.println("------------------------------");
        System.out.println("                              ");
        System.out.println("   1. Zona A - VIP ($30,000)  ");
        System.out.println("                              ");
        System.out.println("------------------------------");
        System.out.println("                              ");
        System.out.println("2. Zona B - Platea baja ($15,000)");
        System.out.println("                              ");
        System.out.println("------------------------------");
        System.out.println("                              ");
        System.out.println("3. Zona C - Platea alta ($18,000)");
        System.out.println("                              ");
        System.out.println("------------------------------");
        System.out.println("                              ");
        System.out.println(" 4. Zona D - Palcos ($13,000) ");
        System.out.println("                              ");
        System.out.println("------------------------------");
        
        int tipoEntrada = 0;
        while (tipoEntrada < 1 || tipoEntrada > 4) {
            System.out.print("Seleccione el tipo de entrada (1-4): ");
            try {
                tipoEntrada = input.nextInt();
                if (tipoEntrada < 1 || tipoEntrada > 4) {
                    System.out.println("Opcion no valida. Intente de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: Ingrese un numero valido.");
                input.nextLine();
            }
        }
        
        System.out.print("Ingrese su edad: ");
        int edad = input.nextInt();
        
        System.out.print("Cuantas entradas desea comprar? (Minimo 1): ");
        int cantidad = input.nextInt();
        
        double precioBase = 0;
        switch (tipoEntrada) {
            case 1: precioBase = 30000; break;
            case 2: precioBase = 15000; break;
            case 3: precioBase = 18000; break;
            case 4: precioBase = 13000; break;
        }
        
        double descuento = 0;
        String tipoDescuento = "Ninguno";
        
        if (edad < 20) {
            descuento += 0.10;
            tipoDescuento = "Estudiante (10%)";
        } else if (edad > 60) {
            descuento += 0.15;
            tipoDescuento = "Tercera edad (15%)";
        }
        
        if (cantidad >= 3) {
            descuento += 0.05;
            tipoDescuento += (tipoDescuento.isEmpty() ? "" : " + ") + "Promocion (5%)";
        }
        
        double precioFinal = precioBase * cantidad * (1 - descuento);
        
        ArrayList<Integer> asientosSeleccionados = new ArrayList<>();
        
        for (int i = 1; i <= cantidad; i++) {
            System.out.println("Asientos disponibles para Zona " + (char)('A' + tipoEntrada - 1) + ":");
            for (int j = 0; j < 40; j++) {
                if (asientosDisponibles[tipoEntrada-1][j]) {
                    System.out.print((j+1) + " ");
                } else {
                    System.out.print("X ");
                }
                if ((j+1) % 10 == 0) System.out.println();
            }
            
            int asiento = 0;
            while (asiento < 1 || asiento > 40) {
                System.out.print("Seleccione el asiento: " + i + " (1-40): ");
                try {
                    asiento = input.nextInt();
                    if (asiento < 1 || asiento > 40) {
                        System.out.println("Asiento no valido. Intente de nuevo.");
                    } else if (!asientosDisponibles[tipoEntrada-1][asiento-1]) {
                        System.out.println("Asiento ocupado. Seleccione otro.");
                        asiento = 0;
                    } else if (asientosSeleccionados.contains(asiento)) {
                        System.out.println("Asiento ya seleccionado. Elija otro.");
                        asiento = 0;
                    }
                } catch (Exception e) {
                    System.out.println("ERROR: Ingrese un numero valido.");
                    input.nextLine();
                }
            }
            
            asientosSeleccionados.add(asiento);
            asientosDisponibles[tipoEntrada-1][asiento-1] = false;
        }
        
        for (int asiento : asientosSeleccionados) {
            entradasVendidas.add(new Entrada(proximoNumeroCompra, tipoEntrada, edad, precioFinal/cantidad, asiento));
        }
        
        // Estadísticas
        totalEntradasVendidas += cantidad;
        ingresosTotales += precioFinal;
        proximoNumeroCompra++;
        
        System.out.println("DETALLES DE COMPRA: " + (proximoNumeroCompra-1));
        System.out.println("Tipo de entrada: " + 
            (tipoEntrada == 1 ? "VIP" :
             tipoEntrada == 2 ? "Platea baja" :
             tipoEntrada == 3 ? "Platea alta" : "Palcos"));
        System.out.println("Asientos: " + asientosSeleccionados);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Descuentos aplicados: " + (tipoDescuento.isEmpty() ? "Ninguno" : tipoDescuento));
        System.out.println("Precio total: $" + precioFinal);
    }

    private void mostrarEntradasVendidas() {
        System.out.println("ENTRADAS VENDIDAS:");
        for (Entrada e : entradasVendidas) {
            if (e.activa) {
                System.out.println("Compra #" + e.numeroCompra + 
                    " - Tipo: " + (e.tipoEntrada == 1 ? "VIP" :
                                   e.tipoEntrada == 2 ? "Platea baja" :
                                   e.tipoEntrada == 3 ? "Platea alta" : "Palcos") +
                    " - Asiento: " + e.asiento +
                    " - Precio: $" + e.precio);
            }
        }
    }

    private void buscarEntrada(Scanner input) {
        System.out.print("Ingrese numero de compra: ");
        int numBusqueda = input.nextInt();
        boolean encontrado = false;
        
        for (Entrada e : entradasVendidas) {
            if (e.numeroCompra == numBusqueda && e.activa) {
                System.out.println("ENTRADA ENCONTRADA:");
                System.out.println("Compra #" + e.numeroCompra);
                System.out.println("Tipo: " + (e.tipoEntrada == 1 ? "VIP" :
                                              e.tipoEntrada == 2 ? "Platea baja" :
                                              e.tipoEntrada == 3 ? "Platea alta" : "Palcos"));
                System.out.println("Asiento: " + e.asiento);
                System.out.println("Precio: $" + e.precio);
                encontrado = true;
            }
        }
        
        if (!encontrado) {
            System.out.println("No se encontro la entrada con ese numero de compra.");
        }
    }

    private void cancelarEntrada(Scanner input) {
        System.out.print("Ingrese numero de compra a cancelar: ");
        int numCancelar = input.nextInt();
        boolean encontrado = false;
        
        for (Entrada e : entradasVendidas) {
            if (e.numeroCompra == numCancelar && e.activa) {
                e.activa = false;
                asientosDisponibles[e.tipoEntrada-1][e.asiento-1] = true;
                System.out.println("Entrada #" + numCancelar + " (asiento " + e.asiento + ") cancelada.");
                encontrado = true;
                entradasCanceladas++;
                ingresosTotales -= e.precio;
            }
        }
        
        if (!encontrado) {
            System.out.println("No se encontro una entrada con ese numero de compra.");
        }
    }

    private void mostrarEstadisticas() {
        System.out.println("ESTADISTICAS DEL TEATRO:");
        System.out.println("Total de entradas vendidas: " + totalEntradasVendidas);
        System.out.println("Total de entradas canceladas: " + entradasCanceladas);
        System.out.println("Ingresos totales: $" + ingresosTotales);
        System.out.println("Proximo numero de compra: " + proximoNumeroCompra);
    }
}
