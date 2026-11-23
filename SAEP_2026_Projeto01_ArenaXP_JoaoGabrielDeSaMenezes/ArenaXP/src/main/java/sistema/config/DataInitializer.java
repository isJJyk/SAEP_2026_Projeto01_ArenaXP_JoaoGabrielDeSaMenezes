package sistema.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sistema.dto.UsuarioRequestDTO;
import sistema.repository.UsuarioRepository;
import sistema.services.UsuarioService;


@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public DataInitializer(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }
    //Usuários do sistema
    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        usuarioService.usuarioCadastro(new UsuarioRequestDTO("Administrador ArenaXP", "adm@arenaxp.com", "adm123", "ADMIN"));
        usuarioService.usuarioCadastro(new UsuarioRequestDTO("João Técnico", "tecnicoum@arenaxp.com", "tecnico123", "TECNICO"));
        usuarioService.usuarioCadastro(new UsuarioRequestDTO("Maria Técnica", "tecnicodois@arenaxp.com", "tecnico123", "TECNICO"));
    }
}
