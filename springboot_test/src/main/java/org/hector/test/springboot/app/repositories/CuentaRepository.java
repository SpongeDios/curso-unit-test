package org.hector.test.springboot.app.repositories;

import org.hector.test.springboot.app.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
//    List<Cuenta> findAll();
//    Cuenta findById(Long id);
//    void update(Cuenta cuenta);
    Optional<Cuenta> findByNombre(String nombre);

    @Query("select c from Cuenta c where c.nombre=?1")
    Optional<Cuenta> buscarPersona(String nombre);

}
