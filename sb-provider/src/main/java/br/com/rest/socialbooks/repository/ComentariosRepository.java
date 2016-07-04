package br.com.rest.socialbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rest.socialbooks.domain.Comentario;

public interface ComentariosRepository extends JpaRepository<Comentario, Long>{

}
