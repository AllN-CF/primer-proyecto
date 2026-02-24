package com.tortilleria.app_tortilleria.repository;

import com.tortilleria.app_tortilleria.model.Pedido;
import com.tortilleria.app_tortilleria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByUsuario(Usuario usuario);
}
