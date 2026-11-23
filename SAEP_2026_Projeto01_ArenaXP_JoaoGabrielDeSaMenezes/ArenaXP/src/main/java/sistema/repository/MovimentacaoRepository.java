package sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sistema.entities.Movimentacao;

import java.util.List;
import java.util.Optional;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao,Long> {
    @Override
    Optional<Movimentacao> findById(Long id);


    @Query("SELECT m FROM Movimentacao m " +
            "JOIN FETCH m.equipamentosId " +
            "JOIN FETCH m.usuarioId " +
            "ORDER BY m.dataHora DESC, m.id DESC")
    List<Movimentacao> listarHistorico();


    @Query("SELECT COUNT(m) FROM Movimentacao m WHERE m.equipamentosId.id = :equipamentoId")
    long contarPorEquipamento(@Param("equipamentoId") Long equipamentoId);

}
