package sistema.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import sistema.dto.UsuarioResponseDTO;


@ControllerAdvice
public class UsuarioLogadoAdvice {

    @ModelAttribute("usuarioLogado")
    public UsuarioResponseDTO usuarioLogado(HttpServletRequest request) {
        return LoginUsuario.obter(request.getSession(false));
    }
}
