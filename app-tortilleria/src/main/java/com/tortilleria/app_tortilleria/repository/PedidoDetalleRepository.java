package com.tortilleria.app_tortilleria.repository;

import com.tortilleria.app_tortilleria.model.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
}
