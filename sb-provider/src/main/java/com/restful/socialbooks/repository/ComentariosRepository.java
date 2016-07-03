package com.restful.socialbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restful.socialbooks.domain.Comentario;

public interface ComentariosRepository extends JpaRepository<Comentario, Long>{

}
