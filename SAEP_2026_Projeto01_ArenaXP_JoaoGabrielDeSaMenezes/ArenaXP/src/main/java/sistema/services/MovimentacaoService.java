package sistema.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sistema.dto.MovimentacaoRequestDTO;
import sistema.dto.MovimentacaoResponseDTO;
import sistema.entities.Equipamentos;
import sistema.entities.Movimentacao;
import sistema.entities.Usuario;
import sistema.enums.TipoMovimentacao;
import sistema.exceptions.SistemaException;
import sistema.repository.EquipamentosRepository;
import sistema.repository.MovimentacaoRepository;
import sistema.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private EquipamentosRepository equipamentosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private Validator validator;


    public List<String> validarDados(MovimentacaoRequestDTO dto) {
        return validator.validate(dto).stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .toList();
    }


    @Transactional
    public MovimentacaoResponseDTO registrarMovimentacao(MovimentacaoRequestDTO dto, Long usuarioId) {
        List<String> erros = validarDados(dto);
        if (!erros.isEmpty()) {
            throw new SistemaException(erros);
        }

        Equipamentos equipamento = equipamentosRepository.buscarParaAtualizar(dto.equipamentoId())
                .orElseThrow(() -> new SistemaException("Equipamento não encontrado."));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new SistemaException("Usuário logado não encontrado. Faça login novamente."));

        int quantidade = dto.quantidade();
        int saldoAtual = equipamento.getQuantidadeAtual();

        if (dto.tipoMovimentacao() == TipoMovimentacao.SAIDA) {
            if (quantidade > saldoAtual) {
                throw new SistemaException("Saldo insuficiente para a saída de \"" + equipamento.getNome()
                        + "\": solicitado " + quantidade + " unidade(s), disponível apenas " + saldoAtual + ".");
            }
            equipamento.setQuantidadeAtual(saldoAtual - quantidade);
        } else {
            equipamento.setQuantidadeAtual(saldoAtual + quantidade);
        }
        equipamentosRepository.save(equipamento);

        LocalDateTime dataHora = dto.dataHora() != null ? dto.dataHora() : LocalDateTime.now();
        String observacao = (dto.observacaoEvento() == null || dto.observacaoEvento().isBlank())
                ? null : dto.observacaoEvento().trim();

        Movimentacao movimentacao = new Movimentacao(dto.tipoMovimentacao().name(), quantidade, dataHora,
                observacao, equipamento, usuario);
        return MovimentacaoResponseDTO.fromEntity(movimentacaoRepository.save(movimentacao));
    }

    @Transactional(readOnly = true)
    public MovimentacaoResponseDTO procurarPorId(Long movimentacaoId){
        Movimentacao movimentacao = movimentacaoRepository.findById(movimentacaoId)
                .orElseThrow(() -> new SistemaException("Não foi possível encontrar a movimentação."));
        return MovimentacaoResponseDTO.fromEntity(movimentacao);
    }


    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarHistorico() {
        return movimentacaoRepository.listarHistorico().stream().map(MovimentacaoResponseDTO::fromEntity).toList();
    }
}
