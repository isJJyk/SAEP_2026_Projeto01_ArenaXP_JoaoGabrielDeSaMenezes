package sistema.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sistema.config.LoginUsuario;
import sistema.dto.EquipamentoResponseDTO;
import sistema.dto.MovimentacaoRequestDTO;
import sistema.dto.MovimentacaoResponseDTO;
import sistema.dto.UsuarioResponseDTO;
import sistema.enums.TipoMovimentacao;
import sistema.exceptions.SistemaException;
import sistema.services.EquipamentosService;
import sistema.services.MovimentacaoService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MovimentacaoController {

    private static final DateTimeFormatter FORMATO_INPUT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Autowired
    public MovimentacaoService movimentacaoService;

    @Autowired
    private EquipamentosService equipamentosService;

    // (A-Z) e formulário de Entrada/Saída
    @GetMapping("/estoque")
    public String estoque(@RequestParam(required = false) Long equipamentoId, Model model) {
        MovimentacaoRequestDTO form = new MovimentacaoRequestDTO(equipamentoId, TipoMovimentacao.SAIDA, null,
                LocalDateTime.now(), "");
        return carregarEstoque(model, form);
    }


    @PostMapping("/estoque/movimentar")
    public String movimentar(@RequestParam(required = false) Long equipamentoId,
                             @RequestParam(required = false) String tipoMovimentacao,
                             @RequestParam(required = false) Integer quantidade,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataHora,
                             @RequestParam(required = false) String observacaoEvento,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        UsuarioResponseDTO usuario = LoginUsuario.obter(session);
        if (usuario == null) {
            return "redirect:/login";
        }

        TipoMovimentacao tipo = TipoMovimentacao.de(tipoMovimentacao);
        MovimentacaoRequestDTO dto = new MovimentacaoRequestDTO(equipamentoId, tipo, quantidade,
                dataHora != null ? dataHora : LocalDateTime.now(), observacaoEvento);

        List<String> erros = new ArrayList<>();
        try {
            MovimentacaoResponseDTO registrada = movimentacaoService.registrarMovimentacao(dto, usuario.id());

            String rotulo = tipo == TipoMovimentacao.SAIDA ? "Saída" : "Entrada";
            redirectAttributes.addFlashAttribute("sucesso", rotulo + " de " + registrada.quantidade()
                    + " unidade(s) de \"" + registrada.equipamentoNome() + "\" registrada com sucesso.");


            if (tipo == TipoMovimentacao.SAIDA) {
                EquipamentoResponseDTO equipamento = equipamentosService.procurarPorId(registrada.equipamentoId());
                if (equipamento.estoqueCritico()) {
                    redirectAttributes.addFlashAttribute("alertaCritico", "\"" + equipamento.nome()
                            + "\" está com " + equipamento.quantidadeAtual() + " unidade(s) em estoque (mínimo: "
                            + equipamento.estoqueMinimo() + ").");
                }
            }
            return "redirect:/estoque";
        } catch (SistemaException e) {
            erros.addAll(e.getMensagens());
        }

        model.addAttribute("erros", erros);
        return carregarEstoque(model, dto);
    }


    @GetMapping("/movimentacoes")
    public String historico(Model model) {
        model.addAttribute("movimentacoes", movimentacaoService.listarHistorico());
        return "movimentacoes";
    }



    private String carregarEstoque(Model model, MovimentacaoRequestDTO form) {
        model.addAttribute("equipamentos", equipamentosService.listarOrdenadoPorNome());
        model.addAttribute("equipamentosCriticos", equipamentosService.listarEmAlerta());
        model.addAttribute("form", form);
        model.addAttribute("dataHoraForm", (form.dataHora() != null ? form.dataHora() : LocalDateTime.now()).format(FORMATO_INPUT));
        return "estoque";
    }
}
