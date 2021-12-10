package org.hector.appmockito.ejemplos.services;

import org.hector.appmockito.ejemplos.models.Examen;

import java.util.Optional;

public interface ExamenService {
    Optional<Examen> findExamenPorNombre(String nombre);
    Examen findExamenPornombreConPreguntas(String nombre);
    Examen guardar(Examen examen);
}
