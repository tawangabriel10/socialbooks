package br.com.rest.socialbooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rest.socialbooks.domain.Livro;

public interface LivrosRepository extends JpaRepository<Livro, Long> {
	
	public List<Livro> findByNomeContaining(String nome); 

}
