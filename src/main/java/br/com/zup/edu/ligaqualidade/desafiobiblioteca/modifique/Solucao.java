package br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import br.com.zup.edu.ligaqualidade.desafiobiblioteca.DadosDevolucao;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.DadosEmprestimo;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.EmprestimoConcedido;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.*;

public class Solucao {

	/**
	 * Você precisa implementar o código para executar o fluxo
	 * o completo de empréstimo e devoluções a partir dos dados
	 * que chegam como argumento. 
	 * 
	 * Caso você queira pode adicionar coisas nas classes que já existem,
	 * mas não pode alterar nada.
	 */
	
	/**
	 * 
	 * @param livros dados necessários dos livros
	 * @param exemplares tipos de exemplares para cada livro
	 * @param usuarios tipos de usuarios
	 * @param emprestimos informações de pedidos de empréstimos
	 * @param devolucoes informações de devoluções, caso exista. 
	 * @param dataExpiracao aqui é a data que deve ser utilizada para verificar expiração
	 * @return
	 */
	public static Set<EmprestimoConcedido> executa(Set<DadosLivro> livros,
			Set<DadosExemplar> exemplares,
			Set<DadosUsuario> usuarios, Set<DadosEmprestimo> emprestimos,
			Set<DadosDevolucao> devolucoes, LocalDate dataExpiracao) {

		Set<EmprestimoConcedido> concedidos = new HashSet<>();

		// Regra - Não pode solicitar livro para +60 dias
		emprestimos.stream().filter(it -> it.tempo <= 60).forEach(emprestimo ->{
			Optional<DadosExemplar> exemplar;

			// Regra - Tem que existir um exemplar para o livro solicitado
			// Regra - Somente PESQUISADOR pode pegar livro RESTRITO
			// Regra - Tipo do Exemplar precisar ser o mesmo do Tipo do Emprestimo
			exemplar = exemplares.stream().filter(it ->
					it.idLivro == emprestimo.idLivro && //emprestimo.tipoExemplar == it.tipo &&
							liberado(it, usuarios, emprestimo)).findFirst();

			LocalDate date = LocalDate.now().plusDays(emprestimo.tempo);

			exemplar.ifPresent(it ->
					concedidos.add(new EmprestimoConcedido(emprestimo.idUsuario, it.idExemplar, date)));
		});

		devolucoes.forEach(it -> {
			concedidos.iterator().next().registraDevolucao();
		});

		return concedidos;
	}

	public static boolean liberado(DadosExemplar exemplar, Set<DadosUsuario> usuarios, DadosEmprestimo emprestimo) {
		boolean estaLiberado = false;
		if (exemplar.tipo == TipoExemplar.LIVRE) {
			estaLiberado = true;
		} else {
			Optional<DadosUsuario> usuario;

			// Regra - Somente PESQUISADOR pode pegar livro RESTRITO
			usuario = usuarios.stream().filter(it -> it.idUsuario == emprestimo.idUsuario).findFirst();
			estaLiberado = usuario.isPresent() && usuario.get().padrao == TipoUsuario.PESQUISADOR;
		}

		return estaLiberado;
	}

}
