package sistema.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sistema.config.LoginUsuario;
import sistema.dto.UsuarioResponseDTO;
import sistema.services.UsuarioService;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String formulario(HttpServletRequest request) {
        if (LoginUsuario.obter(request.getSession(false)) != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam(defaultValue = "") String email,
                             @RequestParam(defaultValue = "") String senha,
                             HttpServletRequest request,
                             Model model) {
        Optional<UsuarioResponseDTO> usuario = usuarioService.autenticar(email, senha);
        if (usuario.isEmpty()) {
            model.addAttribute("erro", "E-mail ou senha inválidos.");
            model.addAttribute("email", email);
            return "login";
        }


        HttpSession antiga = request.getSession(false);
        if (antiga != null) {
            antiga.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(LoginUsuario.SESSAO_USUARIO, usuario.get());
        session.setMaxInactiveInterval(30 * 60);
        return "redirect:/";
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String sair(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("sucesso", "Sessão encerrada com sucesso.");
        return "redirect:/login";
    }
}
