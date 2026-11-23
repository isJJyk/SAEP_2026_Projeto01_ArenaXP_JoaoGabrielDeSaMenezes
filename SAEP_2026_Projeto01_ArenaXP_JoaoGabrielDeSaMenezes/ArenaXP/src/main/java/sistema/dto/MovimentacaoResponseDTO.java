package sistema.dto;

import sistema.entities.Movimentacao;

import java.time.LocalDateTime;

public record MovimentacaoResponseDTO(Long id,
                                      String tipoMovimentacao,
                                      int quantidade,
                                      LocalDateTime dataHora,
                                      String observacaoEvento,
                                      Long equipamentoId,
                                      String equipamentoNome,
                                      String codigoPatrimonio,
                                      String usuarioNome) {
    public static MovimentacaoResponseDTO fromEntity(Movimentacao movimentacao) {
        return new MovimentacaoResponseDTO(
                movimentacao.getId(),
                movimentacao.getTipoMovimentacao(),
                movimentacao.getQuantidade(),
                movimentacao.getDataHora(),
                movimentacao.getObservacaoEvento(),
                movimentacao.getEquipamentosId().getId(),
                movimentacao.getEquipamentosId().getNome(),
                movimentacao.getEquipamentosId().getCodigoPatrimonio(),
                movimentacao.getUsuarioId().getNome()
        );
    }
}
