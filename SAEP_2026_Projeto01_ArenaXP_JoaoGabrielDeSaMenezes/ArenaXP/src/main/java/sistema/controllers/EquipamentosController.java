package sistema.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sistema.dto.EquipamentoRequestDTO;
import sistema.dto.EquipamentoResponseDTO;
import sistema.exceptions.SistemaException;
import sistema.services.EquipamentosService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/equipamentos")
public class EquipamentosController {

    @Autowired
    private EquipamentosService equipamentosService;


    @GetMapping
    public String listar(@RequestParam(required = false) String busca,
                         @RequestParam(required = false) String categoria,
                         Model model) {
        model.addAttribute("equipamentos", equipamentosService.buscar(busca, categoria));
        model.addAttribute("categorias", equipamentosService.listarCategorias());
        model.addAttribute("busca", busca == null ? "" : busca);
        model.addAttribute("categoriaSelecionada", categoria == null ? "" : categoria);
        return "equipamentos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(model, null, new EquipamentoRequestDTO("", "", "", "", 0, 0));
    }

    @PostMapping
    public String adicionarEquipamento(@RequestParam(defaultValue = "") String codigoPatrimonio,
                                       @RequestParam(defaultValue = "") String nome,
                                       @RequestParam(defaultValue = "") String categoria,
                                       @RequestParam(defaultValue = "") String especificacaoTecnica,
                                       @RequestParam(required = false) Integer quantidadeAtual,
                                       @RequestParam(required = false) Integer estoqueMinimo,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        EquipamentoRequestDTO dto = montarDto(codigoPatrimonio, nome, categoria, especificacaoTecnica, quantidadeAtual, estoqueMinimo);
        List<String> erros = coletarErros(dto, quantidadeAtual, estoqueMinimo);
        if (erros.isEmpty()) {
            try {
                EquipamentoResponseDTO criado = equipamentosService.adicionarEquipamento(dto);
                redirectAttributes.addFlashAttribute("sucesso", "Equipamento \"" + criado.nome() + "\" cadastrado com sucesso.");
                return "redirect:/equipamentos";
            } catch (SistemaException e) {
                erros.addAll(e.getMensagens());
            } catch (DataIntegrityViolationException e) {
                erros.add("Não foi possível salvar: código de patrimônio já cadastrado ou dados inválidos.");
            }
        }
        model.addAttribute("erros", erros);
        return formulario(model, null, dto);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            EquipamentoResponseDTO e = equipamentosService.procurarPorId(id);
            EquipamentoRequestDTO dto = new EquipamentoRequestDTO(e.codigoPatrimonio(), e.nome(), e.categoria(),
                    e.especificacaoTecnica(), e.quantidadeAtual(), e.estoqueMinimo());
            return formulario(model, id, dto);
        } catch (SistemaException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/equipamentos";
        }
    }

    @PostMapping("/{id}")
    public String atualizarEquipamento(@PathVariable Long id,
                                       @RequestParam(defaultValue = "") String codigoPatrimonio,
                                       @RequestParam(defaultValue = "") String nome,
                                       @RequestParam(defaultValue = "") String categoria,
                                       @RequestParam(defaultValue = "") String especificacaoTecnica,
                                       @RequestParam(required = false) Integer quantidadeAtual,
                                       @RequestParam(required = false) Integer estoqueMinimo,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        EquipamentoRequestDTO dto = montarDto(codigoPatrimonio, nome, categoria, especificacaoTecnica, quantidadeAtual, estoqueMinimo);
        List<String> erros = coletarErros(dto, quantidadeAtual, estoqueMinimo);
        if (erros.isEmpty()) {
            try {
                EquipamentoResponseDTO atualizado = equipamentosService.atualizarEquipamentos(id, dto);
                redirectAttributes.addFlashAttribute("sucesso", "Equipamento \"" + atualizado.nome() + "\" atualizado com sucesso.");
                return "redirect:/equipamentos";
            } catch (SistemaException e) {
                erros.addAll(e.getMensagens());
            } catch (DataIntegrityViolationException e) {
                erros.add("Não foi possível salvar: código de patrimônio já cadastrado ou dados inválidos.");
            }
        }
        model.addAttribute("erros", erros);
        return formulario(model, id, dto);
    }

    // RNF05: exclusão tratada sem erro 500 (equipamento com movimentações não é excluído)
    @PostMapping("/{id}/excluir")
    public String deletarEquipamento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            equipamentosService.deletarEquipamento(id);
            redirectAttributes.addFlashAttribute("sucesso", "Equipamento excluído com sucesso.");
        } catch (SistemaException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Não é possível excluir este equipamento porque ele possui movimentações vinculadas.");
        }
        return "redirect:/equipamentos";
    }

    // ---------- auxiliares ----------

    private String formulario(Model model, Long id, EquipamentoRequestDTO dto) {
        model.addAttribute("equipamento", dto);
        model.addAttribute("equipamentoId", id);
        model.addAttribute("modoEdicao", id != null);
        model.addAttribute("categorias", equipamentosService.listarCategorias());
        return "equipamento-form";
    }

    private EquipamentoRequestDTO montarDto(String codigoPatrimonio, String nome, String categoria,
                                            String especificacaoTecnica, Integer quantidadeAtual, Integer estoqueMinimo) {
        return new EquipamentoRequestDTO(codigoPatrimonio, nome, categoria, especificacaoTecnica,
                quantidadeAtual == null ? 0 : quantidadeAtual,
                estoqueMinimo == null ? 0 : estoqueMinimo);
    }

    private List<String> coletarErros(EquipamentoRequestDTO dto, Integer quantidadeAtual, Integer estoqueMinimo) {
        List<String> erros = new ArrayList<>();
        if (quantidadeAtual == null) {
            erros.add("Informe a quantidade atual.");
        }
        if (estoqueMinimo == null) {
            erros.add("Informe o estoque mínimo.");
        }
        erros.addAll(equipamentosService.validarDados(dto));
        return erros;
    }
}
