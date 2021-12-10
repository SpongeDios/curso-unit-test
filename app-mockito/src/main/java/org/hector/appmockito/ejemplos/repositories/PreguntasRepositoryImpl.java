package org.hector.appmockito.ejemplos.repositories;

import java.util.Arrays;
import java.util.List;

public class PreguntasRepositoryImpl implements PreguntaRepository{

    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");
        System.out.println("PreguntasRepositoryImpl.findPreguntasPorExamenId");
        return preguntas;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntasRepositoryImpl.guardarVarias");
    }
}
