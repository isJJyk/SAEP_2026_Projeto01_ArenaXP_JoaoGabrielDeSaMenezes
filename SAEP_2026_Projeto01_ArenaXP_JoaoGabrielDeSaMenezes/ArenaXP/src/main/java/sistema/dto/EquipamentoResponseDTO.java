package sistema.dto;

import sistema.entities.Equipamentos;

public record EquipamentoResponseDTO(Long id,
                                     String codigoPatrimonio,
                                     String nome,
                                     String categoria,
                                     String especificacaoTecnica,
                                     int quantidadeAtual,
                                     int estoqueMinimo) {
    public static EquipamentoResponseDTO fromEntity(Equipamentos equipamento) {
        return new EquipamentoResponseDTO(
                equipamento.getId(),
                equipamento.getCodigoPatrimonio(),
                equipamento.getNome(),
                equipamento.getCategoria(),
                equipamento.getEspecificacaoTecnica(),
                equipamento.getQuantidadeAtual(),
                equipamento.getEstoqueMinimo()
        );
    }


    public boolean estoqueCritico() {
        return quantidadeAtual <= estoqueMinimo;
    }
}
