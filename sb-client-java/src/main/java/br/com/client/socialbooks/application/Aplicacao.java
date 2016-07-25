package br.com.client.socialbooks.application;

import java.text.SimpleDateFormat;
import java.util.List;

import java.text.ParseException;

import br.com.client.socialbooks.controller.LivrosClient;
import br.com.client.socialbooks.domain.Livro;

public class Aplicacao {

	public static void main(String[] args) throws ParseException {
		String localizacao = salvarLivro();
		
		System.out.println(localizacao);
		
		LivrosClient livrosClient = new LivrosClient();
		
		List<Livro> listaLivros = livrosClient.listar();
		
		for (Livro livro : listaLivros) {
			System.out.println("Livro: " + livro.getNome());
		}
		
	}
	
	private static String salvarLivro() throws ParseException {
		Livro livro1 = new Livro();
		livro1.setNome("Rest Aplicado");
		livro1.setEditora("Algaworks");
		
		SimpleDateFormat publicacao = new SimpleDateFormat("dd/MM/yyyy");
		livro1.setPublicacao(publicacao.parse("23/07/2016"));
		livro1.setResumo("Este livro aborda conceitos do modelo arquitetural Rest");
		
		return "";
	}
}
