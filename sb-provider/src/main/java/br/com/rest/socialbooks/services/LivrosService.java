package br.com.rest.socialbooks.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.com.rest.socialbooks.domain.Comentario;
import br.com.rest.socialbooks.domain.Livro;
import br.com.rest.socialbooks.repository.ComentariosRepository;
import br.com.rest.socialbooks.repository.LivrosRepository;
import br.com.rest.socialbooks.services.exceptions.LivroNaoEncontradoException;

@Service
public class LivrosService {

	@Autowired
	private LivrosRepository livrosRepository;
	
	@Autowired
	private ComentariosRepository comentariosRepository;
	
	public List<Livro> listar(String nome) {
		String strnome = nome == null ? "%" : nome;
		return livrosRepository.findByNomeContaining(strnome);
	}
	
	public Livro buscar(Long id) {
		Livro livro = livrosRepository.findOne(id);
		
		if (livro == null) {
			throw new LivroNaoEncontradoException("O livro não pôde ser encontrado!");
		}
		
		return livro;
	}
	
	public Livro salvar(Livro livro) {
		livro.setId(null);
		return livrosRepository.save(livro);
	}
	
	public void deletar(Long id) {
		try {
			livrosRepository.delete(id);
		} catch (EmptyResultDataAccessException e) { 
			throw new LivroNaoEncontradoException("O livro não pôde ser eencontrado.");
		}
	}
	
	public void atualizar(Livro livro) {
		verificarExistencia(livro);
		livrosRepository.save(livro);
	}
	
	private void verificarExistencia(Livro livro) {
		buscar(livro.getId());
	}
	
	public Comentario salvarComentario(Long livroId, Comentario comentario) {
		Livro livro = buscar(livroId);
		
		comentario.setLivro(livro);
		comentario.setData(new Date());
		
		return comentariosRepository.save(comentario);
	}
	
	public List<Comentario> listarComentarios(Long livroId) {
		Livro livro = buscar(livroId);
		
		return livro.getComentarios();
	}
}
