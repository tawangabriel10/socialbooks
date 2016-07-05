package br.com.rest.socialbooks.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rest.socialbooks.domain.Autor;
import br.com.rest.socialbooks.repository.AutoresRepository;
import br.com.rest.socialbooks.services.exceptions.AutorExistenteException;
import br.com.rest.socialbooks.services.exceptions.AutorNaoEncontradoException;

@Service
public class AutoresService {
	
	@Autowired
	private AutoresRepository autoresRepository;

	public List<Autor> listar() {
		return autoresRepository.findAll();
	}
	
	public Autor salvar(Autor autor) {
		if (autor.getId() != null) {
			Autor a = autoresRepository.findOne(autor.getId());
			
			if(a != null) {
				throw new AutorExistenteException("O autor já existe.");
			}
		}
		return autoresRepository.save(autor);
	}
	
	public Autor buscar(Long id) {
		Autor autor = autoresRepository.findOne(id);
		
		if (autor == null) {
			throw new AutorNaoEncontradoException("O autor não pode ser encontrado.");
		}
		return autor;
	}
}
