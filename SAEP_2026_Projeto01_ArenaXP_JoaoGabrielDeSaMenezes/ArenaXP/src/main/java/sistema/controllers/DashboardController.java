package sistema.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sistema.services.EquipamentosService;

@Controller
public class DashboardController {

    @Autowired
    private EquipamentosService equipamentosService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalEquipamentos", equipamentosService.contarTotal());
        model.addAttribute("totalEmAlerta", equipamentosService.contarEmAlerta());
        model.addAttribute("equipamentosCriticos", equipamentosService.listarEmAlerta());
        return "dashboard";
    }
}
