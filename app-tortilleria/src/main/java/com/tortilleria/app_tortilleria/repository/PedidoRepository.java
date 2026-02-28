package com.tortilleria.app_tortilleria.repository;

import com.tortilleria.app_tortilleria.model.EstadoPedido;
import com.tortilleria.app_tortilleria.model.Pedido;
import com.tortilleria.app_tortilleria.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Page<Pedido> findAllByUsuario(Usuario usuario, Pageable pageable);

    Page<Pedido> findAllByEstado(EstadoPedido estado, Pageable pageable);
}
