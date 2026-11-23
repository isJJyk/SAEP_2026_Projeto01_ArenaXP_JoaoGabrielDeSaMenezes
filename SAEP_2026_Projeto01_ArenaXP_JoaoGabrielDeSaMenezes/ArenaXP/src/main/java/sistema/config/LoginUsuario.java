package sistema.config;

import jakarta.servlet.http.HttpSession;
import sistema.dto.UsuarioResponseDTO;


public final class LoginUsuario {

    public static final String SESSAO_USUARIO = "usuarioLogado";

    private LoginUsuario() {
    }

    public static UsuarioResponseDTO obter(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object usuario = session.getAttribute(SESSAO_USUARIO);
        return usuario instanceof UsuarioResponseDTO dto ? dto : null;
    }
}
