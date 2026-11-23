package sistema.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sistema.enums.TipoMovimentacao;

import java.time.LocalDateTime;

public record MovimentacaoRequestDTO(@NotNull(message = "Selecione um equipamento.")
                                     Long equipamentoId,
                                     @NotNull(message = "Selecione o tipo de movimentação (Entrada ou Saída).")
                                     TipoMovimentacao tipoMovimentacao,
                                     @NotNull(message = "Informe a quantidade.")
                                     @Min(value = 1, message = "A quantidade deve ser maior que zero.")
                                     Integer quantidade,
                                     LocalDateTime dataHora,
                                     @Size(max = 255, message = "A observação aceita no máximo 255 caracteres.")
                                     String observacaoEvento) {
}
