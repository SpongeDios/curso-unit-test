package org.hector.test.springboot.app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Banco {
    private Long id;
    private String nombre;
    private Integer totalTransferencias;
}
