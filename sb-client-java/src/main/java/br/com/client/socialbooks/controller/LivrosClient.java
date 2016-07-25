package br.com.client.socialbooks.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.client.socialbooks.domain.Livro;

public class LivrosClient {
	
	private static String URI_BASE;
	
	private static final String URN_BASE = "/livros";
	
	private static String USER_AUTENTICATION;

	private RestTemplate restTemplate;
	
	public LivrosClient() {}
	
	public LivrosClient(String url, String usuario, String senha) {
		restTemplate = new RestTemplate();
		
		URI_BASE = url.concat(URN_BASE);
		
		String credencial = usuario + ":" + senha;
		USER_AUTENTICATION = "Basic " + Base64.getEncoder()
				.encodeToString(credencial.getBytes());
	}

	public List<Livro> listar() {
		RequestEntity<Void> request = RequestEntity
				.get(URI.create(URI_BASE))
				.header("Authorization", USER_AUTENTICATION).build();
		
		ResponseEntity<Livro[]> response = restTemplate.exchange(request, Livro[].class);
		return Arrays.asList(response.getBody());
	}
	
	public String salvar(Livro livro) {
		RequestEntity<Livro> request = RequestEntity
				.post(URI.create(URI_BASE))
				.header("Authorization", USER_AUTENTICATION)
				.body(livro);
		
		ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
		
		return response.getHeaders().getLocation().toString();
	}
	
	public Livro buscar(String uri) {
		RequestEntity<Void> request = RequestEntity
				.get(URI.create(uri))
				.header("Authorization", USER_AUTENTICATION)
				.build();
		
		ResponseEntity<Livro> response = restTemplate.exchange(request, Livro.class);
		return response.getBody();
	}
}
