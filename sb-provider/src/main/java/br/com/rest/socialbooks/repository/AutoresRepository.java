package br.com.rest.socialbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rest.socialbooks.domain.Autor;

@Repository
public interface AutoresRepository extends JpaRepository<Autor, Long> {

}
