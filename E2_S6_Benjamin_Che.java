package com.mycompany.e2_s6_benjamin_che;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class E2_S6_Benjamin_Che {
    // Variables estáticas para estadísticas globales
    private static int totalEntradasVendidas = 0;
    private static double ingresosTotales = 0;
    private static int entradasCanceladas = 0;
    private static int asientosUtilizados = 0;
    
    // Variables de instancia
    private ArrayList<Entrada> entradasVendidas;
    private boolean[][] asientosDisponibles;
    private ArrayList<String> boletasGeneradas;
    private Timer timer;
    
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

    // Constructor
    public E2_S6_Benjamin_Che() {
        entradasVendidas = new ArrayList<>();
        asientosDisponibles = new boolean[4][40];
        boletasGeneradas = new ArrayList<>();
        inicializarAsientos();
        iniciarTimer();
    }

    private void inicializarAsientos() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 40; j++) {
                asientosDisponibles[i][j] = true;
            }
        }
    }

    private void iniciarTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                bloquearAsientosAleatorios();
            }
        }, 30000, 30000); // Cada 30 segundos
    }

    private void bloquearAsientosAleatorios() {
        Random rand = new Random();
        int asientosBloqueados = 0;
        
        while (asientosBloqueados < 2) {
            int zona = rand.nextInt(4);
            int asiento = rand.nextInt(40);
            
            if (asientosDisponibles[zona][asiento]) {
                asientosDisponibles[zona][asiento] = false;
                asientosBloqueados++;
                System.out.println("ALERTA: El asiento " + (asiento+1) + 
                                 " de la zona " + (char)('A'+zona) + 
                                 " ya no esta disponible.");
            }
        }
    }

    public static void main(String[] args) {
        E2_S6_Benjamin_Che E2_S6_Benjamin_Che;
        E2_S6_Benjamin_Che = new E2_S6_Benjamin_Che();
        Scanner input = new Scanner(System.in);
        
        System.out.println("Bienvenidos al Teatro Moro!");
        
        boolean continuar = true;
        while (continuar) {
            System.out.println("\nMenu Teatro Moro");
            System.out.println("1. Comprar entrada");
            System.out.println("2. Ver entradas vendidas");
            System.out.println("3. Buscar entrada por numero de compra");
            System.out.println("4. Cancelar entrada");
            System.out.println("5. Mostrar estadisticas");
            System.out.println("6. Salir y generar boleta final");
            System.out.print("Seleccione una opcion: ");
            
            int opcion = leerEntero(input);
            
            switch (opcion) {
                case 1:
                    E2_S6_Benjamin_Che.comprarEntrada(input);
                    break;
                case 2:
                    E2_S6_Benjamin_Che.mostrarEntradasVendidas();
                    break;
                case 3:
                    E2_S6_Benjamin_Che.buscarEntrada(input);
                    break;
                case 4:
                    E2_S6_Benjamin_Che.cancelarEntrada(input);
                    break;
                case 5:
                    E2_S6_Benjamin_Che.mostrarEstadisticas();
                    break;
                case 6:
                    continuar = false;
                    E2_S6_Benjamin_Che.generarBoletaFinal();
                    System.out.println("Gracias por usar el sistema de ventas del Teatro Moro.");
                    break;
                default:
                    System.out.println("Opcion no valida. Intente de nuevo.");
            }
        }
        
        E2_S6_Benjamin_Che.detenerTimer();
        input.close();
    }

    private static int leerEntero(Scanner input) {
        while (true) {
            try {
                return input.nextInt();
            } catch (Exception e) {
                System.out.println("ERROR: Ingrese un número válido.");
                input.nextLine();
            }
        }
    }

    private void comprarEntrada(Scanner input) {
        mostrarZonasDisponibles();
        
        int tipoEntrada = leerTipoEntrada(input);
        int edad = leerEdad(input);
        int cantidad = leerCantidad(input);
        
        double precioBase = obtenerPrecioBase(tipoEntrada);
        double[] descuentos = calcularDescuentos(edad, cantidad);
        double precioFinal = precioBase * cantidad * (1 - descuentos[0]);
        
        ArrayList<Integer> asientosSeleccionados = seleccionarAsientos(input, tipoEntrada, cantidad);
        
        registrarCompra(tipoEntrada, edad, precioFinal, cantidad, asientosSeleccionados);
        
        generarBoletaCompra(tipoEntrada, cantidad, asientosSeleccionados, (int) descuentos[1], precioFinal);
    }

    private void mostrarZonasDisponibles() {
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
    }

    private int leerTipoEntrada(Scanner input) {
        int tipoEntrada = 0;
        while (tipoEntrada < 1 || tipoEntrada > 4) {
            System.out.print("Seleccione el tipo de entrada (1-4): ");
            tipoEntrada = leerEntero(input);
            if (tipoEntrada < 1 || tipoEntrada > 4) {
                System.out.println("Opcion no valida. Intente de nuevo.");
            }
        }
        return tipoEntrada;
    }

    private int leerEdad(Scanner input) {
        System.out.print("Ingrese su edad: ");
        return leerEntero(input);
    }

    private int leerCantidad(Scanner input) {
        int cantidad = 0;
        while (cantidad < 1) {
            System.out.print("Cuantas entradas desea comprar? (Minimo 1): ");
            cantidad = leerEntero(input);
            if (cantidad < 1) {
                System.out.println("Debe comprar al menos 1 entrada.");
            }
        }
        return cantidad;
    }

    private double obtenerPrecioBase(int tipoEntrada) {
        switch (tipoEntrada) {
            case 1: return 30000;
            case 2: return 15000;
            case 3: return 18000;
            case 4: return 13000;
            default: return 0;
        }
    }

    private double[] calcularDescuentos(int edad, int cantidad) {
        double descuento = 0;
        int tipoDescuento = 0; // 0=none, 1=estudiante, 2=adulto mayor, 3=promo
        
        // Punto de debug
        System.out.println("Calculando descuentos para edad: " + edad + ", cantidad: " + cantidad);
        
        if (edad < 20) {
            descuento += 0.10;
            tipoDescuento = 1;
        } else if (edad > 60) {
            descuento += 0.15;
            tipoDescuento = 2;
        }
        
        if (cantidad >= 3) {
            descuento += 0.05;
            tipoDescuento = 3;
        }
        
        // Punto de debug
        System.out.println("Descuento total aplicado: " + (descuento*100) + "%");
        
        return new double[]{descuento, tipoDescuento};
    }

    private ArrayList<Integer> seleccionarAsientos(Scanner input, int tipoEntrada, int cantidad) {
        ArrayList<Integer> asientosSeleccionados = new ArrayList<>();
        
        for (int i = 1; i <= cantidad; i++) {
            mostrarAsientosDisponibles(tipoEntrada);
            
            int asiento = 0;
            while (asiento < 1 || asiento > 40) {
                System.out.print("Seleccione el asiento #" + i + " (1-40): ");
                asiento = leerEntero(input);
                
                // Punto de debug
                System.out.println("alidando asiento " + asiento + " en zona " + tipoEntrada);
                
                if (asiento < 1 || asiento > 40) {
                    System.out.println("Asiento no valido. Intente de nuevo.");
                } else if (!asientosDisponibles[tipoEntrada-1][asiento-1]) {
                    System.out.println("Asiento ocupado. Seleccione otro.");
                    asiento = 0;
                } else if (asientosSeleccionados.contains(asiento)) {
                    System.out.println("Asiento ya seleccionado. Elija otro.");
                    asiento = 0;
                }
            }
            
            asientosSeleccionados.add(asiento);
            asientosDisponibles[tipoEntrada-1][asiento-1] = false;
            
            // Punto de debug 
            System.out.println("Asiento " + asiento + " confirmado para zona " + 
                              (char)('A'+tipoEntrada-1));
        }
        
        return asientosSeleccionados;
    }

    private void mostrarAsientosDisponibles(int tipoEntrada) {
        System.out.println("Asientos disponibles para Zona " + (char)('A' + tipoEntrada - 1) + ":");
        for (int j = 0; j < 40; j++) {
            if (asientosDisponibles[tipoEntrada-1][j]) {
                System.out.print((j+1) + " ");
            } else {
                System.out.print("X ");
            }
            if ((j+1) % 10 == 0) System.out.println();
        }
    }

    private void registrarCompra(int tipoEntrada, int edad, double precioFinal, 
                               int cantidad, ArrayList<Integer> asientosSeleccionados) {
        for (int asiento : asientosSeleccionados) {
            entradasVendidas.add(new Entrada(
                entradasVendidas.size() + 1,
                tipoEntrada, 
                edad, 
                precioFinal/cantidad, 
                asiento
            ));
        }
        
        totalEntradasVendidas += cantidad;
        ingresosTotales += precioFinal;
        asientosUtilizados += cantidad;
    }

    private void generarBoletaCompra(int tipoEntrada, int cantidad, 
                                   ArrayList<Integer> asientosSeleccionados, 
                                   int tipoDescuento, double precioFinal) {
        String boleta = "COMPRA EXITOSA #" + entradasVendidas.size() + "\n" +
            "Tipo: " + (tipoEntrada == 1 ? "VIP" : tipoEntrada == 2 ? "Platea baja" : 
                      tipoEntrada == 3 ? "Platea alta" : "Palcos") + "\n" +
            "Asientos: " + asientosSeleccionados + "\n" +
            "Descuento: " + (tipoDescuento == 1 ? "Estudiante (10%)" : 
                            tipoDescuento == 2 ? "Adulto mayor (15%)" : 
                            tipoDescuento == 3 ? "Promocion (5%)" : "Ninguno") + "\n" +
            "Total: $" + precioFinal + "\n";
        
        // Punto de debug
        System.out.println("Boleta generada: " + boleta);
        
        System.out.println(boleta);
        boletasGeneradas.add(boleta);
    }

    private void mostrarEntradasVendidas() {
        if (entradasVendidas.isEmpty()) {
            System.out.println("No hay entradas vendidas aún.");
            return;
        }
        
        // Estructura de control eficiente 1: for-each con filtrado
        System.out.println("ENTRADAS VENDIDAS:");
        entradasVendidas.stream()
            .filter(e -> e.activa)
            .forEach(e -> System.out.printf(
                "Compra #%d - %s (Asiento %d) - $%.2f %s\n",
                e.numeroCompra,
                (e.tipoEntrada == 1 ? "VIP" : e.tipoEntrada == 2 ? "Platea baja" : 
                 e.tipoEntrada == 3 ? "Platea alta" : "Palcos"),
                e.asiento,
                e.precio,
                (e.edad < 20 ? "[Estudiante]" : e.edad > 60 ? "[Adulto mayor]" : "")
            ));
    }

    private void buscarEntrada(Scanner input) {
        System.out.print("Ingrese numero de compra: ");
        int numBusqueda = leerEntero(input);
        
        // Estructura de control eficiente 2: Stream con búsqueda
        Entrada encontrada = entradasVendidas.stream()
            .filter(e -> e.numeroCompra == numBusqueda && e.activa)
            .findFirst()
            .orElse(null);
        
        if (encontrada != null) {
            System.out.println("ENTRADA ENCONTRADA:");
            System.out.println("Numero: #" + encontrada.numeroCompra);
            System.out.println("Zona: " + (encontrada.tipoEntrada == 1 ? "VIP" : 
                              encontrada.tipoEntrada == 2 ? "Platea baja" : 
                              encontrada.tipoEntrada == 3 ? "Platea alta" : "Palcos"));
            System.out.println("Asiento: " + encontrada.asiento);
            System.out.println("Precio: $" + encontrada.precio);
            System.out.println("Edad: " + encontrada.edad + " ");
        } else {
            System.out.println("No se encontro la entrada.");
        }
    }

    private void cancelarEntrada(Scanner input) {
        System.out.print("Ingrese numero de compra a cancelar: ");
        int numCancelar = leerEntero(input);
        
        for (Entrada e : entradasVendidas) {
            if (e.numeroCompra == numCancelar && e.activa) {
                e.activa = false;
                asientosDisponibles[e.tipoEntrada-1][e.asiento-1] = true;
                System.out.println("Entrada #" + numCancelar + " (asiento " + e.asiento + ") cancelada.");
                entradasCanceladas++;
                ingresosTotales -= e.precio;
                asientosUtilizados--;
                
                // Punto de depuración 6: Confirmación de cancelación
                System.out.println(" Asiento " + e.asiento + " liberado en zona " + 
                                 (char)('A'+e.tipoEntrada-1));
                return;
            }
        }
        
        System.out.println("No se encontro una entrada activa con ese numero.");
    }

    private void mostrarEstadisticas() {
        System.out.println("ESTADISTICAS DEL TEATRO");
        System.out.println("Entradas vendidas: " + totalEntradasVendidas);
        System.out.println("Entradas canceladas: " + entradasCanceladas);
        System.out.println("Asientos ocupados: " + asientosUtilizados + "/160");
        System.out.printf("Ingresos totales: $%.2f\n", ingresosTotales);
        System.out.println("Capacidad disponible: " + (160 - asientosUtilizados) + " asientos");
    }

    private void generarBoletaFinal() {
        System.out.println("BOLETA FINAL DEL TEATRO MORO");
        System.out.println("----------------------------------");
        boletasGeneradas.forEach(System.out::println);
        System.out.println("----------------------------------");
        System.out.printf("TOTAL GENERAL: $%.2f\n", ingresosTotales);
        System.out.println("----------------------------------");
    }

    private void detenerTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
