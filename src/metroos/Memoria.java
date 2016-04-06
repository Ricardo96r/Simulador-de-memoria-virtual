/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metroos;

import static java.lang.Math.round;
import java.util.Arrays;
import javax.swing.JTextArea;

/**
 *
 * @author ricar
 */
public class Memoria {
    private int memoriaPrincipalTotal;
    private int memoriaSecundariaTotal;
    private int memoriaPrincipal;
    private int memoriaSecundaria;
    private int tamañoPagina;
    private int cantidadMarcos;
    private int marcosDisponibles[];
    private int contadorDisponibles;
    private int cantidadMarcosAlmacenamiento;
    private int maximasPaginas;
    private Pagina memoria[];
    private Pagina almacenamiento[];
    private int marcosDisponiblesAlmacenamiento[];
    private int contadorDisponiblesAlmacenamiento;
    private JTextArea textArea;
    
    public Memoria(int memoriaPrincipalTotal, int memoriaSecundariaTotal, int tamañoPagina, JTextArea textArea) {
        this.textArea = textArea;
        this.memoriaPrincipalTotal = memoriaPrincipalTotal;
        this.memoriaSecundariaTotal = memoriaSecundariaTotal;
        this.tamañoPagina = tamañoPagina;
        this.cantidadMarcos = cantidadMarcos(memoriaPrincipalTotal, tamañoPagina);
        this.cantidadMarcosAlmacenamiento = cantidadMarcosAlmacenamiento(memoriaSecundariaTotal, tamañoPagina);
        this.almacenamiento = new Pagina[cantidadMarcosAlmacenamiento];
        this.memoria = new Pagina[cantidadMarcos];
        this.marcosDisponibles = new int[cantidadMarcos];
        this.marcosDisponiblesAlmacenamiento = new int[cantidadMarcosAlmacenamiento];
        this.memoriaPrincipal = memoriaPrincipalTotal;
        this.memoriaSecundaria = memoriaSecundariaTotal;
        this.maximasPaginas = maximaCapacidadPaginas();
        llenarMarcosDisponibles(cantidadMarcos);
        llenarMarcosDisponiblesAlmacenamiento(cantidadMarcosAlmacenamiento);
        llenarMemoriaVacia();
        llenarAlmacenamientoVacio();
    }
    
    private void llenarMarcosDisponibles(int cantidadMarcos) {
        for(int i = 0; i < cantidadMarcos;i++) {
            this.marcosDisponibles[i] = i;
        }
        contadorDisponibles = cantidadMarcos;
    }
    
    private void llenarMarcosDisponiblesAlmacenamiento(int cantidadMarcosAlmacenamiento) {
        for(int i = 0; i < cantidadMarcosAlmacenamiento;i++) {
            this.marcosDisponiblesAlmacenamiento[i] = i;
        }
        contadorDisponiblesAlmacenamiento = cantidadMarcosAlmacenamiento;
    }
    
    private void llenarMemoriaVacia() {
        for(int i = 0; i < cantidadMarcos;i++) {
            this.memoria[i] = new Pagina(i);
        }
    }
    
    private void llenarAlmacenamientoVacio() {
        for(int i = 0; i < cantidadMarcosAlmacenamiento;i++) {
            this.almacenamiento[i] = new Pagina(i);
        }
    }
    
    private void agregarProcesoMemoria(Proceso proceso, int i) {
        int espacio = obtenerEspacioDisponible();
        contadorDisponibles--;
        proceso.getTablaPagina()[i].setIdMarco(espacio);
        proceso.getTablaPagina()[i].setPrincipal(true);
        this.memoria[espacio] = proceso.getTablaPagina()[i];
    }
    
    private void eliminarProcesoMemoria(int posicion) {
        memoria[posicion] = new Pagina(posicion);
        contadorDisponibles++;
        marcosDisponibles[contadorDisponibles-1] = posicion;
        memoriaPrincipal += tamañoPagina;
        Arrays.sort(marcosDisponibles);
    }
    
    private void agregarProcesoAlmacenamiento(Proceso proceso, int i) {
        int espacio = obtenerEspacioDisponibleAlmacenamiento();
        proceso.getTablaPagina()[i].setIdMarco(espacio);
        proceso.getTablaPagina()[i].setPrincipal(false);
        memoriaSecundaria -= tamañoPagina;
        this.almacenamiento[espacio] = proceso.getTablaPagina()[i];
    }
    
    private void eliminarProcesoAlmacenamiento(int posicion) {
        almacenamiento[posicion] = new Pagina(posicion);
        contadorDisponiblesAlmacenamiento++;
        marcosDisponiblesAlmacenamiento[contadorDisponiblesAlmacenamiento-1] = posicion;
        memoriaSecundaria += tamañoPagina;
        Arrays.sort(marcosDisponiblesAlmacenamiento);
    }
    
    public void agregarPaginaMemoria(Proceso proceso, int pagina) {
        eliminarProcesoAlmacenamiento(proceso.getTablaPagina()[pagina].getIdMarco());
        agregarProcesoMemoria(proceso, pagina);
        memoriaPrincipal -= tamañoPagina;
        proceso.setPaginasMemoriaPrincipal(proceso.getPaginasMemoriaPrincipal()+1);
        proceso.setPaginasMemoriaSecundaria(proceso.getPaginasMemoriaSecundaria()-1);
        procesoActivoListo(proceso);
        textArea.append("- Se ha puesto la pagina del proceso de id "+proceso.getIdProceso()+" pasando "+proceso.getPaginasMemoriaSecundaria()+" paginas de memoria secundaria a memoria principal\n");
        mostrarEspaciosDisponibles();
        mostrarEspaciosDisponiblesAlmacenamiento();
    }
    
    public void suspenderProceso(Proceso proceso) {
        for (int i = 0; i < proceso.getCantidadPaginas(); i++) {
            if(proceso.getTablaPagina()[i].getPrincipal()) {
                eliminarProcesoMemoria(proceso.getTablaPagina()[i].getIdMarco());
                agregarProcesoAlmacenamiento(proceso, i);
            }
        }
        textArea.append("- Se ha suspendido el proceso de id "+proceso.getIdProceso()+" pasando "+proceso.getPaginasMemoriaPrincipal()+" paginas de memoria principal a memoria secundaria\n");
        proceso.setPaginasMemoriaSecundaria(proceso.getPaginasMemoriaSecundaria()+proceso.getPaginasMemoriaPrincipal());
        proceso.setPaginasMemoriaPrincipal(0);
        mostrarEspaciosDisponibles();
        mostrarEspaciosDisponiblesAlmacenamiento();
    }
    
    public void listoProceso(Proceso proceso) {
        for (int i = 0; i < proceso.getCantidadPaginas(); i++) {
            if(contadorDisponibles == 0)
                break;
            if(!proceso.getTablaPagina()[i].getPrincipal()) {
                eliminarProcesoAlmacenamiento(proceso.getTablaPagina()[i].getIdMarco());
                agregarProcesoMemoria(proceso, i);
                memoriaPrincipal -= tamañoPagina;
                proceso.setPaginasMemoriaPrincipal(proceso.getPaginasMemoriaPrincipal()+1);
                proceso.setPaginasMemoriaSecundaria(proceso.getPaginasMemoriaSecundaria()-1);
            }
        }
        procesoActivoListo(proceso);
        textArea.append("- Se ha puesto en listo el proceso de id "+proceso.getIdProceso()+" pasando "+proceso.getPaginasMemoriaSecundaria()+" paginas de memoria secundaria a memoria principal\n");
        mostrarEspaciosDisponibles();
        mostrarEspaciosDisponiblesAlmacenamiento();
    }
        
    public void eliminarProceso(Proceso proceso) {
        for (int i = 0; i < proceso.getCantidadPaginas(); i++) {
            if(proceso.getTablaPagina()[i].getPrincipal()) {
                eliminarProcesoMemoria(proceso.getTablaPagina()[i].getIdMarco());
            } else {
                eliminarProcesoAlmacenamiento(proceso.getTablaPagina()[i].getIdMarco());
            }
        }
        proceso.setEstado("Eliminado");
        textArea.append("- Se ha eliminado el proceso de id "+proceso.getIdProceso()+" borrando "+proceso.getPaginasMemoriaPrincipal()+" paginas de memoria principal y "+proceso.getPaginasMemoriaSecundaria()+" paginas de memoria secundaria\n");
        proceso.setPaginasMemoriaPrincipal(0);
        proceso.setPaginasMemoriaSecundaria(0);
    }
         
    public void agregarProceso(Proceso proceso) {
        int espacio;
        if (contadorDisponibles >= proceso.getCantidadPaginas()) {
            if (proceso.getCantidadPaginas() <= cantidadMarcos) {
                for(int i = 0; i < proceso.getCantidadPaginas(); i++) {
                    agregarProcesoMemoria(proceso, i);
                }
                proceso.setEstado("Activo");
                proceso.setPaginasMemoriaPrincipal(proceso.getCantidadPaginas());
                this.memoriaPrincipal -= proceso.getTamañoTotal(); 
            }
        } else if (contadorDisponibles == 0){
            textArea.append("* ALERTA: Ha ocurrido un error!");
        } else {
            textArea.append("> No hay suficiente espacio en memoria principal para agregar todas las paginas. Se ha agregado a la memoria secundaria algunas paginas pero no todas.\n");
            proceso.setPaginasMemoriaPrincipal(contadorDisponibles);
            for(int i = 0; i < contadorDisponibles; i++) {
                espacio = obtenerEspacioDisponible();
                proceso.getTablaPagina()[i].setIdMarco(espacio);
                proceso.getTablaPagina()[i].setPrincipal(true);
                memoriaPrincipal -= tamañoPagina;
                this.memoria[espacio] = proceso.getTablaPagina()[i];
            }          
            for(int i = contadorDisponibles;i < proceso.getCantidadPaginas();i++) {
                agregarProcesoAlmacenamiento(proceso, i);
            }
            proceso.setPaginasMemoriaSecundaria(proceso.getCantidadPaginas()-contadorDisponibles);
            contadorDisponibles = 0;
            procesoActivoListo(proceso);
            mostrarEspaciosDisponibles();
            mostrarEspaciosDisponiblesAlmacenamiento();
        }
    }
    
    public void quitarUnaPaginaMemoria(Proceso proceso) {
        for (int i = 0; i < proceso.getCantidadPaginas(); i++) {
            if(proceso.getTablaPagina()[i].getPrincipal()) {
                eliminarProcesoMemoria(proceso.getTablaPagina()[i].getIdMarco());
                agregarProcesoAlmacenamiento(proceso, i);
                break;
            }
        }
        textArea.append("> No hay espacio en memoria principal. Se ha movido una(s) pagina(s) del proceso de id "+proceso.getIdProceso()+" a memoria secundaria\n");
        proceso.setPaginasMemoriaSecundaria(proceso.getPaginasMemoriaSecundaria()+1);
        proceso.setPaginasMemoriaPrincipal(proceso.getPaginasMemoriaPrincipal()-1);
        procesoActivoListo(proceso);
        mostrarEspaciosDisponibles();
        mostrarEspaciosDisponiblesAlmacenamiento();        
    }
    
    private int obtenerEspacioDisponible() {
        int disponible = marcosDisponibles[0];
        for (int i = 0; i < contadorDisponibles-1; i++) {
            marcosDisponibles[i] = marcosDisponibles[i+1];
        }
        return disponible;
    }
    
    private void procesoActivoListo(Proceso proceso) {
        if((proceso.getCantidadPaginas() / 2) <= proceso.getPaginasMemoriaPrincipal()) {
            proceso.setEstado("Activo");
        } else {
            proceso.setEstado("Listo");
        }
    }
    
    private int obtenerEspacioDisponibleAlmacenamiento() {
        int disponible = marcosDisponiblesAlmacenamiento[0];
        for (int i = 0; i < contadorDisponiblesAlmacenamiento-1; i++) {
            marcosDisponiblesAlmacenamiento[i] = marcosDisponiblesAlmacenamiento[i+1];
        }
        contadorDisponiblesAlmacenamiento--;
        return disponible;
    }
    
    
    private void mostrarEspaciosDisponibles() {
        System.out.println("______________________/n");
        System.out.println("Memoria:/n");
        System.out.println("Disponibles: "+contadorDisponibles+"/n");
        for (int i = 0; i < marcosDisponibles.length; i++) {
            System.out.println(i+" -> "+marcosDisponibles[i]);
        }
    }
    
    private void mostrarEspaciosDisponiblesAlmacenamiento() {
        System.out.println("Almacenamientos:/n");
        System.out.println("Disponibles: "+contadorDisponiblesAlmacenamiento+"/n");
        for (int i = 0; i < marcosDisponiblesAlmacenamiento.length; i++) {
            System.out.println(i+" -> "+marcosDisponiblesAlmacenamiento[i]);
        }
    }
    
    private int maximaCapacidadPaginas() {
        return cantidadMarcos + (memoriaSecundariaTotal/tamañoPagina);
    }
        
    private int cantidadMarcos(int memoriaPrincipalTotal, int tamañoPagina) {
        return round(memoriaPrincipalTotal / tamañoPagina);
    }
    
    private int cantidadMarcosAlmacenamiento(int memoriaSecundariaTotal, int tamañoPagina) {
        return round(memoriaSecundariaTotal/tamañoPagina);
    }

    public int getMemoriaSecundaria() {
        return memoriaSecundaria;
    }

    public void setMemoriaSecundaria(int memoriaSecundaria) {
        this.memoriaSecundaria = memoriaSecundaria;
    }

    public int getTamañoPagina() {
        return tamañoPagina;
    }

    public void setTamañoPagina(int tamañoPagina) {
        this.tamañoPagina = tamañoPagina;
    }

    public int getCantidadMarcos() {
        return cantidadMarcos;
    }

    public void setCantidadMarcos(int cantidadMarcos) {
        this.cantidadMarcos = cantidadMarcos;
    }

    public int[] getMarcosDisponibles() {
        return marcosDisponibles;
    }

    public void setMarcosDisponibles(int[] marcosDisponibles) {
        this.marcosDisponibles = marcosDisponibles;
    }

    public Pagina[] getMemoria() {
        return memoria;
    }

    public void setMemoria(Pagina[] memoria) {
        this.memoria = memoria;
    }

    public int getContadorDisponibles() {
        return contadorDisponibles;
    }

    public void setContadorDisponibles(int contadorDisponibles) {
        this.contadorDisponibles = contadorDisponibles;
    }

    public int getMaximasPaginas() {
        return maximasPaginas;
    }

    public void setMaximasPaginas(int maximasPaginas) {
        this.maximasPaginas = maximasPaginas;
    }

    public int getMemoriaPrincipalTotal() {
        return memoriaPrincipalTotal;
    }

    public void setMemoriaPrincipalTotal(int memoriaPrincipalTotal) {
        this.memoriaPrincipalTotal = memoriaPrincipalTotal;
    }

    public int getMemoriaSecundariaTotal() {
        return memoriaSecundariaTotal;
    }

    public void setMemoriaSecundariaTotal(int memoriaSecundariaTotal) {
        this.memoriaSecundariaTotal = memoriaSecundariaTotal;
    }

    public int getMemoriaPrincipal() {
        return memoriaPrincipal;
    }

    public void setMemoriaPrincipal(int memoriaPrincipal) {
        this.memoriaPrincipal = memoriaPrincipal;
    }

    public int getCantidadMarcosAlmacenamiento() {
        return cantidadMarcosAlmacenamiento;
    }

    public void setCantidadMarcosAlmacenamiento(int cantidadMarcosAlmacenamiento) {
        this.cantidadMarcosAlmacenamiento = cantidadMarcosAlmacenamiento;
    }

    public Pagina[] getAlmacenamiento() {
        return almacenamiento;
    }

    public void setAlmacenamiento(Pagina[] almacenamiento) {
        this.almacenamiento = almacenamiento;
    }

    public int[] getMarcosDisponiblesAlmacenamiento() {
        return marcosDisponiblesAlmacenamiento;
    }

    public void setMarcosDisponiblesAlmacenamiento(int[] marcosDisponiblesAlmacenamiento) {
        this.marcosDisponiblesAlmacenamiento = marcosDisponiblesAlmacenamiento;
    }

    public int getContadorDisponiblesAlmacenamiento() {
        return contadorDisponiblesAlmacenamiento;
    }

    public void setContadorDisponiblesAlmacenamiento(int contadorDisponiblesAlmacenamiento) {
        this.contadorDisponiblesAlmacenamiento = contadorDisponiblesAlmacenamiento;
    }
    
}
