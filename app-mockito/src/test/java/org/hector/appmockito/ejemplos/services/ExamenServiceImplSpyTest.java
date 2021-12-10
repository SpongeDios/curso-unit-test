package org.hector.appmockito.ejemplos.services;

import org.hector.appmockito.ejemplos.models.Examen;
import org.hector.appmockito.ejemplos.repositories.ExamenRepository;
import org.hector.appmockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.hector.appmockito.ejemplos.repositories.PreguntaRepository;
import org.hector.appmockito.ejemplos.repositories.PreguntasRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {

    @Spy
    PreguntasRepositoryImpl preguntaRepository;

    @Spy
    ExamenRepositoryImpl repository;

//    @InjectMocks <-- ayuda a inyectar los @Mock dentro del objeto, pero como es una interfaz
//    hay que inicializarla o cambiarla a la implementacion como hice yo
    @InjectMocks
    ExamenServiceImpl examenService;




    //ES UN HIBRIDO ENTRE UN METODO REAL Y UN MOCK
    //ES UN CLON DEL OBJETO REAL CON CARACTERISTICAS DE UN MOCK
    //SOLO ES RECOMENDABLE UTILIZAR CUANDO NECESITAMOS LA RESPUESTA REAL, NO DEBERIAMOS UTILIZARLOS MUCHO
    @Test
    void testSpy() {
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");

//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
//        tambien se puede usar un when en los spy pero se mockearian, no seria el metodo real

        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen = examenService.findExamenPornombreConPreguntas("Matematicas");
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
    }

    //Los DO RETURN SIRVE PARA METODOS VOID
}









































































































