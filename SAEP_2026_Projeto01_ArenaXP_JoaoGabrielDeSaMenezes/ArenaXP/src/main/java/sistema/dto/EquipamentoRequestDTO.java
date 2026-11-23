package sistema.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipamentoRequestDTO(@NotBlank(message = "Informe o código de patrimônio.")
                                    @Size(max = 30, message = "O código de patrimônio aceita no máximo 30 caracteres.")
                                    String codigoPatrimonio,
                                    @NotBlank(message = "Informe o nome do equipamento.")
                                    @Size(max = 120, message = "O nome aceita no máximo 120 caracteres.")
                                    String nome,
                                    @NotBlank(message = "Informe a categoria.")
                                    @Size(max = 50, message = "A categoria aceita no máximo 50 caracteres.")
                                    String categoria,
                                    @NotBlank(message = "Informe a especificação técnica.")
                                    @Size(max = 255, message = "A especificação técnica aceita no máximo 255 caracteres.")
                                    String especificacaoTecnica,
                                    @Min(value = 0, message = "A quantidade atual deve ser maior ou igual a zero.")
                                    int quantidadeAtual,
                                    @Min(value = 0, message = "O estoque mínimo deve ser maior ou igual a zero.")
                                    int estoqueMinimo) {


    public EquipamentoRequestDTO {
        codigoPatrimonio = codigoPatrimonio == null ? null : codigoPatrimonio.trim();
        nome = nome == null ? null : nome.trim();
        categoria = categoria == null ? null : categoria.trim();
        especificacaoTecnica = especificacaoTecnica == null ? null : especificacaoTecnica.trim();
    }
}
