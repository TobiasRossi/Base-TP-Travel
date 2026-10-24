package com.pepito.reservas.catalogo;

public abstract class CatalogoException extends RuntimeException {
    public CatalogoException(String message) {
        super(message);
    }

    public static class ItemNoEncontrado extends CatalogoException{
        public ItemNoEncontrado (ItemCatalogo item){
            super(item.nombre() + " no se encuentra en el catálogo");

        }
    }
    public static class ItemExistente extends CatalogoException{
        public ItemExistente (ItemCatalogo item){
            super(item.nombre() + " ya existe en el catálogo");

        }
    }

    public static class ErrorItemInvalido extends CatalogoException{
        public ErrorItemInvalido (ItemCatalogo item){
            super(item.nombre() + " no puede agregarse al catálogo debido a que su configuración no es la correcta");

        }
    }
}
