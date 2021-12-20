package org.hector.test.springboot.app.repositories;

import org.hector.test.springboot.app.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BancoRepository extends JpaRepository<Banco, Long> {
}
