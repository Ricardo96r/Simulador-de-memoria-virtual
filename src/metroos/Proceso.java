/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metroos;

import static java.lang.Math.ceil;
import static java.lang.Math.round;

/**
 *
 * @author ricar
 */
public class Proceso {
    private String nombre;
    private int tamaño;
    private int cantidadPaginas;
    private String estado;
    private int paginasMemoriaPrincipal;
    private int paginasMemoriaSecundaria;
    private Pagina tablaPagina[];
    private int idProceso;
    private int tamañoTotal;
    
    public Proceso(int idProceso, String nombre, int tamaño, int tamañoPagina) {
        this.idProceso = idProceso;
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.cantidadPaginas = cantidadPaginas(tamaño, tamañoPagina);
        this.tamañoTotal = cantidadPaginas*tamañoPagina;
        this.tablaPagina = new Pagina[this.cantidadPaginas];
        crearPaginas();
    }
    
    private void crearPaginas() {
        for(int i = 0; i < cantidadPaginas; i++) {
            this.tablaPagina[i] = new Pagina(idProceso, nombre, i, null);
        }
    }
    
    private int cantidadPaginas(int tamaño, int tamañoPagina) {
        if (tamaño % tamañoPagina != 0)
            return (int) (ceil(tamaño / tamañoPagina) + 1);
        return tamaño / tamañoPagina;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTamaño() {
        return tamaño;
    }

    public void setTamaño(int tamaño) {
        this.tamaño = tamaño;
    }

    public int getCantidadPaginas() {
        return cantidadPaginas;
    }

    public void setCantidadPaginas(int cantidadPaginas) {
        this.cantidadPaginas = cantidadPaginas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getPaginasMemoriaPrincipal() {
        return paginasMemoriaPrincipal;
    }

    public void setPaginasMemoriaPrincipal(int paginasMemoriaPrincipal) {
        this.paginasMemoriaPrincipal = paginasMemoriaPrincipal;
    }

    public int getPaginasMemoriaSecundaria() {
        return paginasMemoriaSecundaria;
    }

    public void setPaginasMemoriaSecundaria(int paginasMemoriaSecundaria) {
        this.paginasMemoriaSecundaria = paginasMemoriaSecundaria;
    }

    public Pagina[] getTablaPagina() {
        return tablaPagina;
    }

    public void setTablaPagina(Pagina[] tablaPagina) {
        this.tablaPagina = tablaPagina;
    }

    public int getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(int idProceso) {
        this.idProceso = idProceso;
    }

    public int getTamañoTotal() {
        return tamañoTotal;
    }

    public void setTamañoTotal(int tamañoTotal) {
        this.tamañoTotal = tamañoTotal;
    }
    
}
