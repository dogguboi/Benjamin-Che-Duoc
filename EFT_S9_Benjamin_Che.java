package com.mycompany.eft_s9_benjamin_che;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class EFT_S9_Benjamin_Che {
    // Estadísticas globales
    private static int totalEntradas = 0;
    private static double ingresosTotales = 0;
    private static int asientosOcupados = 0;
    private static int nextOrderId = 1;
    
    // Estructuras de datos principales
    private final Map<Integer, Orden> ordenes = new HashMap<>();
    private final List<List<Boolean>> asientos = new ArrayList<>(5);
    private final List<String> boletas = new ArrayList<>();
    private final Timer timer = new Timer();

    static class Orden {
        final int id;
        final String cliente;
        final List<Entrada> entradas = new ArrayList<>();
        boolean activa = true;

        Orden(int id, String cliente) {
            this.id = id;
            this.cliente = cliente;
        }
    }

    // Class para cada entrada
    static class Entrada {
        final int tipo;
        final int edad;
        final double precio;
        final int asiento;

        Entrada(int tipo, int edad, double precio, int asiento) {
            this.tipo = tipo;
            this.edad = edad;
            this.precio = precio;
            this.asiento = asiento;
        }
    }

    public EFT_S9_Benjamin_Che() {
        inicializarAsientos();
        timer.schedule(new BloqueadorAsientos(), 30000, 30000);
    }

    private void inicializarAsientos() {
        for (int i = 0; i < 5; i++) {
            List<Boolean> zona = new ArrayList<>(40);
            for (int j = 0; j < 40; j++) {
                zona.add(true);
            }
            asientos.add(zona);
        }
    }

    class BloqueadorAsientos extends TimerTask {
        private final Random rand = new Random();

        public void run() {
            int bloqueados = 0;
            while (bloqueados < 2) {
                int zona = rand.nextInt(5);
                int asiento = rand.nextInt(40);
                
                if (asientos.get(zona).get(asiento)) {
                    asientos.get(zona).set(asiento, false);
                    
                    // Registrar las ventas de 30 segundos como ventas normales
                    double precio = getPrecio(zona);
                    int orderId = nextOrderId++;
                    Orden nuevaOrden = new Orden(orderId, "Venta anonima");
                    nuevaOrden.entradas.add(new Entrada(zona, 0, precio, asiento+1));
                    ordenes.put(orderId, nuevaOrden);
                    
                    totalEntradas++;
                    ingresosTotales += precio;
                    asientosOcupados++;
                    
                    String boleta = String.format(
                        "\n=== BOLETA ORDEN #%d ===\nCliente: %s\nZona: %s\nAsiento: %d\nTotal: $%,.2f\n",
                        orderId, "Anonimo", getNombreZona(zona), asiento+1, precio
                    );
                    boletas.add(boleta);
                    
                    bloqueados++;
                    System.out.printf("\nAsiento %d de la zona %c vendido\n", 
                                    asiento+1, (char)('A'+zona));
                }
            }
        }
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            EFT_S9_Benjamin_Che teatro = new EFT_S9_Benjamin_Che();
            System.out.println("Bienvenidos al Teatro Moro!");

            while (true) {
                mostrarMenu();
                int opcion = leerEntero(sc);
                
                switch (opcion) {
                    case 1 -> teatro.comprarEntrada(sc);
                    case 2 -> teatro.mostrarOrdenes();
                    case 3 -> teatro.buscarOrden(sc);
                    case 4 -> teatro.cancelarOrden(sc);
                    case 5 -> teatro.mostrarEstadisticas();
                    case 6 -> { 
                        teatro.generarBoletaFinal();
                        System.out.println("Gracias por usar nuestro sistema!");
                        teatro.detenerTimer();
                        return;
                    }
                    default -> System.out.println("Opcion invalida");
                }
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n=== MENU TEATRO MORO ===");
        System.out.println("1. Comprar entradas");
        System.out.println("2. Ver todas las ordenes");
        System.out.println("3. Buscar orden por numero");
        System.out.println("4. Cancelar orden");
        System.out.println("5. Ver estadisticas");
        System.out.println("6. Salir");
        System.out.print("Seleccione opcion: ");
    }

    private static int leerEntero(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.println("Error: Ingrese un numero valido");
            sc.next();
        }
        return sc.nextInt();
    }

    private void comprarEntrada(Scanner sc) {
        System.out.println("\n=== NUEVA COMPRA ===");
        mostrarZonas();
        int tipo = leerTipo(sc);
        
        // Preguntar por sexo (solo Hombre/Mujer)
        System.out.print("Sexo (H/M): ");
        String sexo = sc.next().toUpperCase();
        while (!sexo.equals("H") && !sexo.equals("M")) {
            System.out.println("Ingrese H (Hombre) o M (Mujer)");
            sexo = sc.next().toUpperCase();
        }
        
        int edad = leerEdad(sc);
        int cantidad = leerCantidad(sc);
        sc.nextLine(); // Limpiar buffer
        System.out.print("Nombre del cliente: ");
        String nombre = sc.nextLine().trim();
        
        double precioBase = getPrecio(tipo);
        double descuento = calcularDescuento(edad, cantidad, sexo);
        double total = precioBase * cantidad * (1 - descuento);
        
        List<Integer> asientosSel = seleccionarAsientos(sc, tipo, cantidad);
        
        // Crear y registrar la nueva orden
        int orderId = nextOrderId++;
        Orden nuevaOrden = new Orden(orderId, nombre);
        
        // Agregar entradas a la orden
        double precioUnitario = total / cantidad;
        for (int asiento : asientosSel) {
            nuevaOrden.entradas.add(new Entrada(tipo, edad, precioUnitario, asiento));
        }
        
        ordenes.put(orderId, nuevaOrden);
        totalEntradas += cantidad;
        ingresosTotales += total;
        asientosOcupados += cantidad;
        
        generarBoleta(nuevaOrden, tipo, asientosSel, total);
    }

    private void mostrarZonas() {
        System.out.println("\nZonas disponibles:");
        System.out.println("-----------------------------");
        System.out.println("       1. VIP ($30,000)      ");
        System.out.println("             Zona A          ");
        System.out.println("-----------------------------");
        System.out.println("  2. Platea Baja ($15,000)   ");
        System.out.println("             Zona B          ");
        System.out.println("-----------------------------");
        System.out.println("   3. Platea Alta ($18,000)  ");
        System.out.println("             Zona C          ");
        System.out.println("-----------------------------");
        System.out.println("     4. Palcos ($13,000)     ");
        System.out.println("             Zona D          ");
        System.out.println("-----------------------------");
        System.out.println("    5. Galeria ($10,000)     ");
        System.out.println("             Zona E          ");
        System.out.println("-----------------------------");
    }

    private int leerTipo(Scanner sc) {
        int tipo;
        do {
            System.out.print("Seleccione zona (1-5): ");
            tipo = leerEntero(sc) - 1;
        } while (tipo < 0 || tipo > 4);
        return tipo;
    }

    private int leerEdad(Scanner sc) {
        System.out.print("Edad del cliente: ");
        return leerEntero(sc);
    }

    private int leerCantidad(Scanner sc) {
        System.out.println("\nPromocion! Por 3+ entradas obtienes 5% de descuento adicional");
        int cant;
        do {
            System.out.print("Cantidad de entradas (minimo 1): ");
            cant = leerEntero(sc);
            if (cant < 1) {
                System.out.println("Debe comprar al menos 1 entrada");
            } else if (cant >= 3) {
                System.out.println("Descuento por cantidad aplicado!");
            }
        } while (cant < 1);
        return cant;
    }

    private double getPrecio(int tipo) {
        return switch (tipo) {
            case 0 -> 30000;
            case 1 -> 15000;
            case 2 -> 18000;
            case 3 -> 13000;
            case 4 -> 10000;
            default -> 0;
        };
    }

    private double calcularDescuento(int edad, int cantidad, String sexo) {
        double descuento = 0;
        
        // Descuento por edad
        if (edad <= 12) {
            descuento += 0.10; // 10% para niños
            System.out.println("Descuento niño (10%) aplicado");
        } else if (edad < 20) {
            descuento += 0.15; // 15% para estudiantes
            System.out.println("Descuento estudiante (15%) aplicado");
        } else if (edad > 60) {
            descuento += 0.25; // 25% para adultos mayores
            System.out.println("Descuento adulto mayor (25%) aplicado");
        } else {
            // Solo aplicar descuento mujer si es que no es adulto mayor
            if (sexo.equals("M")) {
                descuento += 0.20; // 20% para mujeres
                System.out.println("Descuento mujer (20%) aplicado");
            }
        }
        
        // Descuento por cantidad
        if (cantidad >= 3) {
            descuento += 0.05; // 5% adicional
            System.out.println("Descuento por cantidad (5%) aplicado");
        }
        
        // Asegurar que el descuento no aguante mas de lo que deberia
        descuento = Math.min(descuento, 0.50);
        
        if (descuento > 0) {
            System.out.printf("Descuentos aplicados: %.0f%%\n", descuento * 100);
        }
        return descuento;
    }

    private List<Integer> seleccionarAsientos(Scanner sc, int tipo, int cantidad) {
        List<Integer> seleccionados = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            mostrarAsientosDisponibles(tipo);
            int asiento;
            do {
                System.out.printf("Seleccione asiento #%d (1-40): ", i+1);
                asiento = leerEntero(sc);
            } while (asiento < 1 || asiento > 40 || 
                    !asientos.get(tipo).get(asiento-1) || 
                    seleccionados.contains(asiento));
            
            seleccionados.add(asiento);
            asientos.get(tipo).set(asiento-1, false);
        }
        return seleccionados;
    }

    private void mostrarAsientosDisponibles(int tipo) {
        System.out.printf("\nAsientos disponibles Zona %c:\n", (char)('A' + tipo));
        for (int i = 0; i < 40; i++) {
            System.out.printf(asientos.get(tipo).get(i) ? "%2d " : " X ", i+1);
            if ((i+1) % 10 == 0) System.out.println();
        }
    }

    private void generarBoleta(Orden orden, int tipo, List<Integer> asientosSel, double total) {
        String zona = getNombreZona(tipo);
        
        StringBuilder detalles = new StringBuilder();
        for (Entrada e : orden.entradas) {
            String desc = "";
            if (e.edad <= 12) {
                desc = "[Niño 10%]";
            } else if (e.edad < 20) {
                desc = "[Estudiante 15%]";
            } else if (e.edad > 60) {
                desc = "[Adulto Mayor 25%]";
            } else if (orden.cliente.toLowerCase().startsWith("mujer") || 
                      orden.cliente.toLowerCase().contains(" mujer")) {
                desc = "[Mujer 20%]";
            }
            
            detalles.append(String.format("- %s (Asiento %d) - $%,.2f %s%n",
                            getNombreZona(e.tipo), e.asiento, e.precio, desc));
        }
        
        String boleta = String.format(
            "\n=== BOLETA ORDEN #%d ===\nCliente: %s\nZona: %s\nAsientos: %s\n%sTotal: $%,.2f\n",
            orden.id, orden.cliente, zona, asientosSel, detalles.toString(), total
        );
        
        System.out.println(boleta);
        boletas.add(boleta);
    }

    private String getNombreZona(int tipo) {
        return switch (tipo) {
            case 0 -> "VIP";
            case 1 -> "Platea Baja";
            case 2 -> "Platea Alta";
            case 3 -> "Palcos";
            case 4 -> "Galeria";
            default -> "";
        };
    }

    private void mostrarOrdenes() {
        if (ordenes.isEmpty()) {
            System.out.println("\nNo hay ordenes registradas");
            return;
        }
        
        System.out.println("\n=== TODAS LAS ORDENES ===");
        for (Orden orden : ordenes.values()) {
            String estado = orden.activa ? "Activa" : "Cancelada";
            System.out.printf("\nOrden #%d - Cliente: %s - %d entradas - Estado: %s",
                            orden.id, orden.cliente, orden.entradas.size(), estado);
        }
    }

    private void buscarOrden(Scanner sc) {
        System.out.print("\nIngrese numero de orden: ");
        int orderId = leerEntero(sc);
        
        Orden orden = ordenes.get(orderId);
        if (orden == null) {
            System.out.println("Orden no encontrada");
            return;
        }
        
        System.out.printf("\n=== ORDEN #%d ===\n", orderId);
        System.out.println("Cliente: " + orden.cliente);
        System.out.println("Estado: " + (orden.activa ? "ACTIVA" : "CANCELADA"));
        System.out.printf("Total entradas: %d\n", orden.entradas.size());
        
        double totalOrden = 0;
        System.out.println("\nDetalle de entradas:");
        for (Entrada e : orden.entradas) {
            String desc = e.edad <= 12 ? "[Niño 10%]" : 
                         e.edad < 20 ? "[Estudiante 15%]" : 
                         e.edad > 60 ? "[Adulto Mayor 25%]" : 
                         orden.cliente.toLowerCase().contains("mujer") ? "[Mujer 20%]" : "";
            System.out.printf("- %s (Asiento %d) - $%,.2f %s\n",
                            getNombreZona(e.tipo), e.asiento, e.precio, desc);
            totalOrden += e.precio;
        }
        
        System.out.printf("\nTotal de la orden: $%,.2f\n", totalOrden);
        if (!orden.activa) {
            System.out.println("\nESTA ORDEN HA SIDO CANCELADA!");
        }
    }

    private void cancelarOrden(Scanner sc) {
        System.out.print("\nIngrese numero de orden a cancelar: ");
        int orderId = leerEntero(sc);
        
        Orden orden = ordenes.get(orderId);
        if (orden == null) {
            System.out.println("Orden no encontrada");
            return;
        }
        
        if (!orden.activa) {
            System.out.println("Esta orden ya esta cancelada");
            return;
        }
        
        if (orden.cliente.equals("Venta anonima")) {
            System.out.println("Las ventas anonimas no pueden cancelarse");
            return;
        }
        
        orden.activa = false;
        int entradasCanceladas = orden.entradas.size();
        double totalDevuelto = 0;
        
        for (Entrada e : orden.entradas) {
            asientos.get(e.tipo).set(e.asiento-1, true);
            totalDevuelto += e.precio;
        }
        
        asientosOcupados -= entradasCanceladas;
        ingresosTotales -= totalDevuelto;
        
        System.out.printf(
            "\nOrden #%d cancelada exitosamente\nEntradas liberadas: %d\nTotal devuelto: $%,.2f\n",
            orderId, entradasCanceladas, totalDevuelto
        );
    }

    private void mostrarEstadisticas() {
        System.out.println("\n=== ESTADISTICAS DEL TEATRO ===");
        
        int ordenesNormales = 0;
        int ordenesCanceladas = 0;
        for (Orden orden : ordenes.values()) {
            if (orden.activa) {
                ordenesNormales++;
            } else {
                ordenesCanceladas++;
            }
        }
        
        System.out.printf("Total ordenes: %d\n", ordenes.size());
        System.out.printf(" - Ordenes normales: %d\n", ordenesNormales);
        System.out.printf(" - Ordenes canceladas: %d\n", ordenesCanceladas);
        System.out.printf("Entradas vendidas: %d\n", totalEntradas);
        System.out.printf("Asientos ocupados: %d/200\n", asientosOcupados); // Cambiado de 160 a 200
        System.out.printf("Ingresos totales: $%,.2f\n", ingresosTotales);
        System.out.printf("Porcentaje ocupacion: %.1f%%\n", (asientosOcupados / 2.0)); // Cambiado de 1.6 a 2.0
        
        if (ordenesCanceladas > 0) {
            System.out.println("\n=== RESUMEN CANCELACIONES ===");
            System.out.printf("Total entradas canceladas: %d\n", totalEntradas - asientosOcupados);
            System.out.printf("Total devuelto por cancelaciones: $%,.2f\n", 
                            calcularTotalDevuelto());
        }
    }

    private double calcularTotalDevuelto() {
        double totalDevuelto = 0;
        for (Orden orden : ordenes.values()) {
            if (!orden.activa) {
                for (Entrada e : orden.entradas) {
                    totalDevuelto += e.precio;
                }
            }
        }
        return totalDevuelto;
    }

    private void generarBoletaFinal() {
        System.out.println("\n=== RESUMEN FINAL DE VENTAS ===");
        System.out.printf("Total ordenes procesadas: %d\n", ordenes.size());
        
        int ordenesActivas = 0;
        double totalVentas = 0;
        
        for (Orden orden : ordenes.values()) {
            if (orden.activa) {
                ordenesActivas++;
                totalVentas += orden.entradas.stream().mapToDouble(e -> e.precio).sum();
            }
        }
        
        System.out.printf("Ordenes activas: %d\n", ordenesActivas);
        System.out.printf("Ventas totales: $%,.2f\n", totalVentas);
        System.out.println("\n=== DETALLE DE ORDENES ===");
        
        for (String boleta : boletas) {
            System.out.println(boleta);
        }
    }

    private void detenerTimer() {
        timer.cancel();
    }
}
