package br.com.rest.socialbooks.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.rest.socialbooks.domain.DetalhesErro;
import br.com.rest.socialbooks.services.exceptions.LivroNaoEncontradoException;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(LivroNaoEncontradoException.class)
	public ResponseEntity<DetalhesErro> handlerLivroNaoEncontradoException(
			LivroNaoEncontradoException e, HttpServletRequest request) {
		
		DetalhesErro erro = new DetalhesErro();
		erro.setStatus(404L);
		erro.setTitulo("O livro não pode ser encontrado");
		erro.setMensagemDesenvolvedor("http://erros.socialbooks.com/404");
		erro.setTimestamp(System.currentTimeMillis());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}
}
