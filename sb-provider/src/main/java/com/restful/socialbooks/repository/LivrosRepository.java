package com.restful.socialbooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restful.socialbooks.domain.Livro;

public interface LivrosRepository extends JpaRepository<Livro, Long> {
	
	public List<Livro> findByNomeContaining(String nome); 

}
