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
class ExamenServiceImplTest {

    @Mock
    PreguntasRepositoryImpl preguntaRepository;

    @Mock
    ExamenRepositoryImpl repository;

//    @InjectMocks <-- ayuda a inyectar los @Mock dentro del objeto, pero como es una interfaz
//    hay que inicializarla o cambiarla a la implementacion como hice yo
    @InjectMocks
    ExamenServiceImpl examenService;

    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this); //<- habilita la creacion de ExamenService con el
        //constructor por defecto. REEMPLAZA EL @ExtendWith(MockitoExtension.class)
//        Los comento porque el @Mock reemplaza esto--------------------------
//        preguntaRepository = mock(PreguntaRepository.class);
//        repository = mock(ExamenRepositoryImpl.class);
//        examenService = new ExamenServiceImpl(repository, preguntaRepository);
    }

    @Test
    void findExamenPorNombre() {
        //arrange
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        when(repository.findAll()).thenReturn(lista);

        //act
        Optional<Examen> examen = examenService.findExamenPorNombre("Matematicas");

        //La api de optionals recomieda usar orElseThrow antes que un .get()

        //asserts
        assertNotNull(examen);
        assertNotNull(examen.orElseThrow().getNombre());
        assertNotNull(examen.orElseThrow().getId());
        assertNotNull(examen.orElseThrow().getPreguntas());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matematicas", examen.orElseThrow().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        //arrange
        List<Examen> lista = Collections.emptyList();
        when(repository.findAll()).thenReturn(lista);

        //act
        Optional<Examen> examen = examenService.findExamenPorNombre("Matematicas");

        //La api de optionals recomieda usar orElseThrow antes que un .get()

        //asserts
        assertNotNull(examen);
        assertTrue(examen.isEmpty());
    }

    @Test
    void testPreguntasExamen() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        when(repository.findAll()).thenReturn(lista);
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        Examen response = examenService.findExamenPornombreConPreguntas("Matematicas");

        assertNotNull(response);
        assertNotNull(response.getPreguntas());
        assertTrue(response.getPreguntas().contains("aritemtica"));
        assertEquals(5, response.getPreguntas().size());
    }

    @Test
    void testPreguntasExamenVerify() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        when(repository.findAll()).thenReturn(lista);
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        Examen response = examenService.findExamenPornombreConPreguntas("Matematicas");

        assertNotNull(response);
        assertNotNull(response.getPreguntas());
        assertTrue(response.getPreguntas().contains("aritemtica"));
        assertEquals(5, response.getPreguntas().size());

        //con verify se verifica si se invoca el findAll, si no se invoca falla el test

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testGUardarExamen() {
        Examen examenMock = new Examen(8L, "Fisica");
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");
        examenMock.setPreguntas(preguntas);
        when(repository.guardar(any(Examen.class))).thenReturn(examenMock);

        Examen examen = examenService.guardar(examenMock);

        assertNotNull(examen);
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testGUardarExamen2() {
        //LEER SOBRE BDD

        //GIVEN
        Examen examenMock = new Examen(8L, "Fisica");
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");
        examenMock.setPreguntas(preguntas);

        long secuencia = 8L;
        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>() {
            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia+1);
                return examen;
            }
        });

        //WHEN
        Examen examen = examenService.guardar(examenMock);

        //THEN
        assertNotNull(examen);
        assertNotNull(examen.getId());
        assertEquals(9L, examen.getId());
        assertEquals("Fisica", examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testGUardarExamen3() {
        Examen examenMock = new Examen(8L, "Fisica");
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");
        examenMock.setPreguntas(preguntas);

        long secuencia = 8L;
        when(repository.guardar(any(Examen.class))).then((Answer<Examen>) invocationOnMock -> {
            Examen examen = invocationOnMock.getArgument(0);
            examen.setId(secuencia+1);
            return examen;
        });

        Examen examen = examenService.guardar(examenMock);

        assertNotNull(examen);
        assertNotNull(examen.getId());
        assertEquals(9L, examen.getId());
        assertEquals("Fisica", examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testManejoExcepcion() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        when(repository.findAll()).thenReturn(lista);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenThrow(new IllegalArgumentException());


        assertThrows(IllegalArgumentException.class, () -> examenService.findExamenPornombreConPreguntas("Matematicas"));
    }

    @Test
    void testManejoExcepcion2() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        when(repository.findAll()).thenReturn(lista);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenThrow(new IllegalArgumentException());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> examenService.findExamenPornombreConPreguntas("Matematicas"));
        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    //////////////ARGUMENTS MATCHERS/////////////////////////////////////////

    //ARGTHAT -> VERIFICA QUE LOS ARGUMENTOS QUE SE PASAN SEAN LOS CORRECTOS
    @Test
    void testArgumentMatchers() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");

        when(repository.findAll()).thenReturn(lista);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        examenService.findExamenPornombreConPreguntas("Matematicas");

        verify(repository).findAll();
        //ARGTHAT -> VERIFICA QUE LOS ARGUMENTOS QUE SE PASAN SEAN LOS CORRECTOS
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(5L)));
        verify(preguntaRepository).findPreguntasPorExamenId(eq(5L));
    }

    @Test
    void testArgumentMatchers2() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");

        when(repository.findAll()).thenReturn(lista);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        examenService.findExamenPornombreConPreguntas("Matematicas");

        verify(repository).findAll();
        //ARGTHAT -> VERIFICA QUE LOS ARGUMENTOS QUE SE PASAN SEAN LOS CORRECTOS
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));
        verify(preguntaRepository).findPreguntasPorExamenId(eq(5L));
    }

    @Test
    void testArgumentMatchers3() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");

        when(repository.findAll()).thenReturn(lista);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        examenService.findExamenPornombreConPreguntas("Matematicas");

        verify(repository).findAll();
        //ARGTHAT -> VERIFICA QUE LOS ARGUMENTOS QUE SE PASAN SEAN LOS CORRECTOS
        verify(preguntaRepository).findPreguntasPorExamenId(argThat( (argument) ->  argument != null && argument > 0));
        verify(preguntaRepository).findPreguntasPorExamenId(eq(5L));
    }

    //Esta es una inner class, tambien puede ser una clase fuera de esta clase.
    public static class MiArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "mensaje personalizado por si falla";
        }
    }

    @Test
    void testArgumentCaptor() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");
        when(repository.findAll()).thenReturn(lista);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        examenService.findExamenPornombreConPreguntas("Matematicas");
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());

        assertEquals(5L, captor.getValue());
    }

    @Test
    void testArgumentCaptorConAnotaciones() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");
        when(repository.findAll()).thenReturn(lista);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        examenService.findExamenPornombreConPreguntas("Matematicas");
//        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class); -> el captor esta con anotaciones
        //arriba

        verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());

        assertEquals(5L, captor.getValue());
    }


    ////////////// MUY UTIL PARA TESTEAR METODOS VOID -> DO THROW /////////////////////
    @Test
    void testDoThrow() {
        Examen examen = new Examen(8L, "Física");
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");
        examen.setPreguntas(preguntas);
        
        doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());
        assertThrows(IllegalArgumentException.class, () -> examenService.guardar(examen));
    }

    //ES PARA CAPTURAR LOS ARGUMENTOS QUE SE LE PASAN AL METODO TESTEADO
    //FUNCIONA MUY PARECIDO AL ANSWER ANTERIOR
    @Test
    void testDoAnswer() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");

        when(repository.findAll()).thenReturn(lista);
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? preguntas: null;
        }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = examenService.findExamenPornombreConPreguntas("Matematicas");

        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    //test para invocar al metodo real y no al when. Hay mejores formas de hacerlo
    @Test
    void testDoCallRealMethod() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");

        when(repository.findAll()).thenReturn(lista);
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas); -> Reemplazado por el do call

        //COn esto hacemos que se invoque el metodo real.
        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen = examenService.findExamenPornombreConPreguntas("Matematicas");
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
    }

    //ES UN HIBRIDO ENTRE UN METODO REAL Y UN MOCK
    //ES UN CLON DEL OBJETO REAL CON CARACTERISTICAS DE UN MOCK
    //SOLO ES RECOMENDABLE UTILIZAR CUANDO NECESITAMOS LA RESPUESTA REAL, NO DEBERIAMOS UTILIZARLOS MUCHO
    @Test
    void testSpy() {
        //TIENE QUE SER LA CLASE O LA IMPLEMENTACION PUESTO QUE SE LLAMA A METODOS REALES
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository = spy(PreguntasRepositoryImpl.class);
        //LOS SPY TAMBIEN SE PUEDEN INSTANCIAR CON LA ANOTACION @SPY EN VEZ DE MOCK
        List<String> preguntas = Arrays.asList("aritemtica", "integrales", "derivadas", "trigonometria", "geometria");

//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
//        tambien se puede usar un when en los spy pero se mockearian, no seria el metodo real

        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);
        Examen examen = examenService.findExamenPornombreConPreguntas("Matematicas");
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
    }

    //Los DO RETURN SIRVE PARA METODOS VOID


    @Test
    //VERIFICANDO EL ORDEN DE EJECUCION DE LOS METODOS
    void testOrdenDeInvocaciones() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        when(repository.findAll()).thenReturn(lista);

        examenService.findExamenPornombreConPreguntas("Matematicas");
        examenService.findExamenPornombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(preguntaRepository);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);

    }

    @Test
        //VERIFICANDO EL ORDEN DE EJECUCION DE LOS METODOS
    void testOrdenDeInvocaciones2() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        when(repository.findAll()).thenReturn(lista);

        examenService.findExamenPornombreConPreguntas("Matematicas");
        examenService.findExamenPornombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(repository, preguntaRepository);
        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);

        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);

    }

    @Test
    void testNumeroDeInvocaciones() {
        List<Examen> lista = Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historias")
        );
        when(repository.findAll()).thenReturn(lista);
        examenService.findExamenPornombreConPreguntas("Matematicas");

        verify(preguntaRepository).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, times(1)).findPreguntasPorExamenId(5L);

        //Los dos metodos de arriba son equivalentes

        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMost(10)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    void testNumeroInvocaciones2() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        examenService.findExamenPornombreConPreguntas("Matematicas");

        verify(preguntaRepository, never()).findPreguntasPorExamenId(5L);
        verifyNoInteractions(preguntaRepository);

        verify(repository).findAll();
        verify(repository, times(1)).findAll();
        verify(repository, atLeast(1)).findAll();
        verify(repository,atLeastOnce()).findAll();
        verify(repository, atMost(10)).findAll();
        verify(repository, atMostOnce()).findAll();



    }
}









































































































