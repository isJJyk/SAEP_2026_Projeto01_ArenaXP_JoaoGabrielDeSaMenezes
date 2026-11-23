package sistema.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sistema.dto.UsuarioRequestDTO;
import sistema.dto.UsuarioResponseDTO;
import sistema.entities.Usuario;
import sistema.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO usuarioCadastro(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setPerfil(dto.perfil());

        Usuario cadastrado = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(cadastrado);
    }


    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> autenticar(String email, String senha) {
        if (email == null || senha == null || email.isBlank() || senha.isBlank()) {
            return Optional.empty();
        }
        return usuarioRepository.findByEmail(email.trim()).stream()
                .filter(usuario -> passwordEncoder.matches(senha, usuario.getSenha()))
                .findFirst()
                .map(UsuarioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(UsuarioResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Não foi possível encontrar usuários"));

        return UsuarioResponseDTO.fromEntity(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> lerUsuarioPorEmail(String email) {
        List<Usuario> usuarios = usuarioRepository.findByEmail(email);
        if (usuarios.isEmpty()) {
            throw new RuntimeException("Não foi possível encontrar usuários");
        }
        return usuarios.stream().map(UsuarioResponseDTO::fromEntity).toList();
    }

}
