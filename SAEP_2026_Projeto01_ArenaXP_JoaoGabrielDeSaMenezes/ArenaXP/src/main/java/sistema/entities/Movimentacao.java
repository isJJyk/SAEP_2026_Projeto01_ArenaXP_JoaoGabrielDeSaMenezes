package sistema.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "movimentacoes")
public class Movimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15, name = "tipo_movimentacao")
    private String tipoMovimentacao;

    @Column(nullable = false,check = @CheckConstraint(constraint = "quantidade > 0"),name = "quantidade")
    private int quantidade;

    @Column(nullable = false,name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "observacao_evento")
    private String observacaoEvento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false,name = "equipamento_id")
    private Equipamentos equipamentosId;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(nullable = false,name = "usuario_id")
    private Usuario usuarioId;

    public Movimentacao() {

    }

    public Movimentacao(String tipoMovimentacao, int quantidade, LocalDateTime dataHora, String observacaoEvento, Equipamentos equipamentosId, Usuario usuarioId) {
        this.tipoMovimentacao = tipoMovimentacao;
        this.quantidade = quantidade;
        this.dataHora = dataHora;
        this.observacaoEvento = observacaoEvento;
        this.equipamentosId = equipamentosId;
        this.usuarioId = usuarioId;
    }

    public Long getId() {
        return id;
    }

    public String getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(String tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getObservacaoEvento() {
        return observacaoEvento;
    }

    public void setObservacaoEvento(String observacaoEvento) {
        this.observacaoEvento = observacaoEvento;
    }

    public Equipamentos getEquipamentosId() {
        return equipamentosId;
    }

    public void setEquipamentosId(Equipamentos equipamentosId) {
        this.equipamentosId = equipamentosId;
    }

    public Usuario getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Usuario usuarioId) {
        this.usuarioId = usuarioId;
    }
}
