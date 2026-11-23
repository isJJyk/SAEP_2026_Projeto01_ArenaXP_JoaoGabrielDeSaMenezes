package sistema.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sistema.entities.Equipamentos;

import java.util.List;
import java.util.Optional;

public interface EquipamentosRepository extends JpaRepository<Equipamentos,Long> {
    @Override
    Optional<Equipamentos> findById(Long id);

    List<Equipamentos> findByNomeContaining(String nome);

    List<Equipamentos> findByCategoria(String categoria);

    Optional<Equipamentos> findByCodigoPatrimonio(String codigoPatrimonio);


    List<Equipamentos> findAllByOrderByNomeAsc(); // "asc" de ascending, no caso ordem alfabética


    @Query("SELECT e FROM Equipamentos e " +
            "WHERE (:busca = '' OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :busca, '%')) " +
            "      OR LOWER(e.codigoPatrimonio) LIKE LOWER(CONCAT('%', :busca, '%'))) " +
            "AND (:categoria = '' OR LOWER(e.categoria) = LOWER(:categoria)) " +
            "ORDER BY e.nome ASC")
    List<Equipamentos> buscar(@Param("busca") String busca, @Param("categoria") String categoria);

    @Query("SELECT DISTINCT e.categoria FROM Equipamentos e ORDER BY e.categoria")
    List<String> listarCategorias();

    @Query("SELECT COUNT(e) FROM Equipamentos e WHERE e.quantidadeAtual <= e.estoqueMinimo")
    long contarEmAlerta();

    @Query("SELECT e FROM Equipamentos e WHERE e.quantidadeAtual <= e.estoqueMinimo ORDER BY e.nome ASC")
    List<Equipamentos> listarEmAlerta();


    @Query("SELECT e FROM Equipamentos e WHERE e.id = :id")
    Optional<Equipamentos> buscarParaAtualizar(@Param("id") Long id);




}
