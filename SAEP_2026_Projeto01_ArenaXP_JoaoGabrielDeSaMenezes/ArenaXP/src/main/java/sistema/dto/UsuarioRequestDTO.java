package sistema.dto;

public record UsuarioRequestDTO(String nome,
                                String email,
                                String senha,
                                String perfil) {
}
