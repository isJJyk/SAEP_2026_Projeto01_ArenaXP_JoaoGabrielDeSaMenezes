package sistema.enums;

public enum TipoMovimentacao {
    ENTRADA,
    SAIDA;


    public static TipoMovimentacao de(String valor) {
        if (valor == null) {
            return null;
        }
        try {
            return valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
