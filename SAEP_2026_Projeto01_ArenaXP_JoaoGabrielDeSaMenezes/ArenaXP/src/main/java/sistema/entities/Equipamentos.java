package sistema.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "equipamentos")
public class Equipamentos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30, name = "codigo_patrimonio")
    private String codigoPatrimonio;

    @Column(nullable = false, length = 120, name = "nome")
    private String nome;

    @Column(nullable = false,length = 50,name = "categoria")
    private String categoria;

    @Column(nullable = false,name = "especificacao_tecnica")
    private String especificacaoTecnica;

    @Column(nullable = false,check = @CheckConstraint(constraint = "quantidade_atual >= 0"),name = "quantidade_atual")
    private int quantidadeAtual; //int invés de Integer, justamente para evitar o "null"

    @Column(nullable = false,check = @CheckConstraint(constraint = "estoque_minimo >= 0"), name = "estoque_minimo")
    private  int estoqueMinimo;

    public Equipamentos(){}

    public Equipamentos(String codigoPatrimonio, String nome, String categoria, String especificacaoTecnica, int quantidadeAtual, int estoqueMinimo) {
        this.codigoPatrimonio = codigoPatrimonio;
        this.nome = nome;
        this.categoria = categoria;
        this.especificacaoTecnica = especificacaoTecnica;
        this.quantidadeAtual = quantidadeAtual;
        this.estoqueMinimo = estoqueMinimo;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoPatrimonio() {
        return codigoPatrimonio;
    }

    public void setCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = codigoPatrimonio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEspecificacaoTecnica() {
        return especificacaoTecnica;
    }

    public void setEspecificacaoTecnica(String especificacaoTecnica) {
        this.especificacaoTecnica = especificacaoTecnica;
    }

    public int getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(int quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }
}
