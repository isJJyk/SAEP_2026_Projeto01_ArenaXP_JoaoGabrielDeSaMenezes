package sistema.exceptions;

import java.util.List;


public class SistemaException extends RuntimeException {

    private final List<String> mensagens;

    public SistemaException(String mensagem) {
        super(mensagem);
        this.mensagens = List.of(mensagem);
    }

    public SistemaException(List<String> mensagens) {
        super(String.join("; ", mensagens));
        this.mensagens = List.copyOf(mensagens);
    }

    public List<String> getMensagens() {
        return mensagens;
    }
}
