package sistema.services;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sistema.dto.EquipamentoRequestDTO;
import sistema.dto.EquipamentoResponseDTO;

import sistema.entities.Equipamentos;
import sistema.exceptions.SistemaException;
import sistema.repository.EquipamentosRepository;
import sistema.repository.MovimentacaoRepository;


import java.util.List;
import java.util.TreeSet;

@Service
public class EquipamentosService {

    private static final List<String> CATEGORIAS_PADRAO = List.of("Placa de Captura", "Headset", "Monitor", "Mouse", "Teclado");

    private final EquipamentosRepository equipamentosRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final Validator validator;

    public EquipamentosService(EquipamentosRepository equipamentosRepository,
                               MovimentacaoRepository movimentacaoRepository,
                               Validator validator) {
        this.equipamentosRepository = equipamentosRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.validator = validator;
    }




    //Devolve as mensagens de validação do DTO (lista vazia = dados válidos).
    public List<String> validarDados(EquipamentoRequestDTO dto) {
        return validator.validate(dto).stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .toList();
    }

    private void validar(EquipamentoRequestDTO dto) {
        List<String> erros = validarDados(dto);
        if (!erros.isEmpty()) {
            throw new SistemaException(erros);
        }
    }

    @Transactional
    public EquipamentoResponseDTO adicionarEquipamento(EquipamentoRequestDTO dto) {
        validar(dto);
        if (equipamentosRepository.findByCodigoPatrimonio(dto.codigoPatrimonio()).isPresent()) {
            throw new SistemaException("Já existe um equipamento com o código de patrimônio " + dto.codigoPatrimonio() + ".");
        }

        Equipamentos equipamento = new Equipamentos();
        equipamento.setCodigoPatrimonio(dto.codigoPatrimonio());
        equipamento.setNome(dto.nome());
        equipamento.setCategoria(dto.categoria());
        equipamento.setEspecificacaoTecnica(dto.especificacaoTecnica());
        equipamento.setQuantidadeAtual(dto.quantidadeAtual());
        equipamento.setEstoqueMinimo(dto.estoqueMinimo());

        Equipamentos cadastrados = equipamentosRepository.save(equipamento);
        return EquipamentoResponseDTO.fromEntity(cadastrados);
    }

    @Transactional(readOnly = true)
    public EquipamentoResponseDTO procurarPorId(Long equipamentoId) {
        Equipamentos equipamentos = equipamentosRepository.findById(equipamentoId).orElseThrow(() -> new SistemaException("Não foi possível encontrar o equipamento."));
        return EquipamentoResponseDTO.fromEntity(equipamentos);
    }

    @Transactional(readOnly = true)
    public EquipamentoResponseDTO procurarPorCodigoPatrimonial(String codigoPatrimonial) {
        Equipamentos equipamentos = equipamentosRepository.findByCodigoPatrimonio(codigoPatrimonial).orElseThrow(() -> new SistemaException("Não foi possível encontrar o equipamento."));
        return EquipamentoResponseDTO.fromEntity(equipamentos);
    }


    @Transactional(readOnly = true)
    public List<EquipamentoResponseDTO> procurarPorNome(String nome) {
        List<Equipamentos> equipamentos = equipamentosRepository.findByNomeContaining(nome);
        if (equipamentos.isEmpty()) {
            throw new SistemaException("Não foi possível encontrar algum equipamento.");
        }
        return equipamentos.stream().map(EquipamentoResponseDTO::fromEntity).toList();
    }


    @Transactional(readOnly = true)
    public List<EquipamentoResponseDTO> buscar(String busca, String categoria) {
        String termo = busca == null ? "" : busca.trim();
        String filtro = categoria == null ? "" : categoria.trim();
        return equipamentosRepository.buscar(termo, filtro).stream().map(EquipamentoResponseDTO::fromEntity).toList();
    }

    // listagem (A-Z)
    @Transactional(readOnly = true)
    public List<EquipamentoResponseDTO> listarOrdenadoPorNome() {
        return equipamentosRepository.findAllByOrderByNomeAsc().stream().map(EquipamentoResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<String> listarCategorias() {
        TreeSet<String> categorias = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        categorias.addAll(CATEGORIAS_PADRAO);
        categorias.addAll(equipamentosRepository.listarCategorias());
        return List.copyOf(categorias);
    }

    @Transactional(readOnly = true)
    public long contarTotal() {
        return equipamentosRepository.count();
    }

    @Transactional(readOnly = true)
    public long contarEmAlerta() {
        return equipamentosRepository.contarEmAlerta();
    }

    @Transactional(readOnly = true)
    public List<EquipamentoResponseDTO> listarEmAlerta() {
        return equipamentosRepository.listarEmAlerta().stream().map(EquipamentoResponseDTO::fromEntity).toList();
    }

    @Transactional
    public EquipamentoResponseDTO atualizarEquipamentos(Long equipamentoId, EquipamentoRequestDTO dto) {
        Equipamentos equipamentos = equipamentosRepository.findById(equipamentoId).orElseThrow(() -> new SistemaException("Não foi possível encontrar este equipamento."));

        validar(dto);
        equipamentosRepository.findByCodigoPatrimonio(dto.codigoPatrimonio()).ifPresent(outro -> {
            if (!outro.getId().equals(equipamentoId)) {
                throw new SistemaException("Já existe um equipamento com o código de patrimônio " + dto.codigoPatrimonio() + ".");
            }
        });

        equipamentos.setCodigoPatrimonio(dto.codigoPatrimonio());
        equipamentos.setNome(dto.nome());
        equipamentos.setCategoria(dto.categoria());
        equipamentos.setEspecificacaoTecnica(dto.especificacaoTecnica());
        equipamentos.setQuantidadeAtual(dto.quantidadeAtual());
        equipamentos.setEstoqueMinimo(dto.estoqueMinimo());

        Equipamentos atualizados = equipamentosRepository.save( equipamentos);
        return EquipamentoResponseDTO.fromEntity(atualizados);
    }



    @Transactional
    public void deletarEquipamento(Long equipamentoId) {
        Equipamentos equipamentosExistente = equipamentosRepository.findById(equipamentoId).orElseThrow(() -> new SistemaException("Equipamento não encontrado."));

        long movimentacoes = movimentacaoRepository.contarPorEquipamento(equipamentoId);
        if (movimentacoes > 0) {
            throw new SistemaException("Não é possível excluir o equipamento \"" + equipamentosExistente.getNome()
                    + "\": ele possui " + movimentacoes + " movimentações registradas no histórico.");
        }

        equipamentosRepository.delete(equipamentosExistente);
    }

}
