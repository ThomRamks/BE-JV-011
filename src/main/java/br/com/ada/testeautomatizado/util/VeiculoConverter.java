package br.com.ada.testeautomatizado.util;

import br.com.ada.testeautomatizado.dto.VeiculoDTO;
import br.com.ada.testeautomatizado.model.Veiculo;
import org.springframework.stereotype.Component;

@Component
public class VeiculoConverter {
    public Veiculo convertFrom(VeiculoDTO veiculoDTO) {
        return Veiculo.builder()
                .placa(veiculoDTO.getPlaca())
                .modelo(veiculoDTO.getModelo())
                .marca(veiculoDTO.getMarca())
                .disponivel(veiculoDTO.getDisponivel())
                .dataFabricacao(veiculoDTO.getDataFabricacao())
                .build();
    }
}
