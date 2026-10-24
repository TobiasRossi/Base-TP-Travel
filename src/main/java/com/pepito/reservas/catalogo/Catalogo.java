package com.pepito.reservas.catalogo;

import com.pepito.reservas.catalogo.busqueda.CriterioBusqueda;
import com.pepito.reservas.catalogo.servicio.GestorServicios;
import com.pepito.reservas.catalogo.servicio.Periodo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Catalogo implements GestorServicios {

    List<ItemCatalogo> items;


    public Catalogo(){
        this.items = new ArrayList<>();
    }

    public List<ItemCatalogo> getCatalogo() {
        return items;
    }

    @Override
    public void agregarItem(ItemCatalogo item){
        if(contieneItem(item)){
            throw new CatalogoException.ItemExistente(item);
        }

        if(!item.validate()){
            throw new CatalogoException.ErrorItemInvalido(item);
        }

        this.items.add(item);
    }



    public Boolean contieneItem(ItemCatalogo itemBuscado){
        return this.items.stream().anyMatch(itemCatalogo -> itemCatalogo.equals(itemBuscado));
    }

    @Override
    public void removerItem(ItemCatalogo item){
        if(!contieneItem(item)){
            throw new CatalogoException.ItemNoEncontrado(item);
        }

        this.items.remove(item);
    }

    @Override
    public Integer cantidadItems() {
        return this.items.size();
    }

    public List<ItemCatalogo> buscar(CriterioBusqueda criterio) {
        // Obtenes una lista de ItemsCatalogo que cada uno cumple con el criterio impuesto
        return items.stream()
                .filter(criterio::cumple)
                .toList();
    }

    public List<ItemCatalogo> buscarDisponibles(CriterioBusqueda criterio, LocalDate fecha, Integer cantidad) {
        // Metodo polimórfico de búsqueda en donde verificas disponibilidad para una sola fecha para cualquier objeto ItemCatalogo
        return buscar(criterio).stream()
                .filter(item -> item.disponible(fecha, cantidad))
                .toList();
    }

    public List<ItemCatalogo> buscarDisponibles(CriterioBusqueda criterio, Periodo fechas, Integer cantidad) {
        // Metodo polimórfico de búsqueda en donde verificas disponibilidad para un rango de fechas para cualquier objeto ItemCatalogo
        return buscar(criterio).stream()
                .filter(item -> item.disponible(fechas, cantidad))
                .toList();
    }


}
