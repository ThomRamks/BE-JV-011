package br.com.ada.testeautomatizado.service;

import br.com.ada.testeautomatizado.dto.VeiculoDTO;
import br.com.ada.testeautomatizado.exception.PlacaInvalidaException;
import br.com.ada.testeautomatizado.exception.VeiculoNaoEncontradoException;
import br.com.ada.testeautomatizado.model.Veiculo;
import br.com.ada.testeautomatizado.repository.VeiculoRepository;
import br.com.ada.testeautomatizado.util.Response;
import br.com.ada.testeautomatizado.util.ValidacaoPlaca;
import br.com.ada.testeautomatizado.util.VeiculoConverter;
import br.com.ada.testeautomatizado.util.VeiculoDTOConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ValidacaoPlaca validacaoPlaca;

    @Autowired
    private VeiculoDTOConverter veiculoDTOConverter;

    @Autowired
    private VeiculoConverter veiculoConverter;

    public ResponseEntity<Response<VeiculoDTO>> cadastrar(VeiculoDTO veiculoDTO) {
        try {
            this.validacaoPlaca.isPlacaValida(veiculoDTO.getPlaca());
            Veiculo veiculo = new Veiculo();
            veiculo = this.veiculoConverter.convertFrom(veiculoDTO);
            this.veiculoRepository.save(veiculo);
            return ResponseEntity.ok(new Response<VeiculoDTO>("Sucesso", veiculoDTO));
        } catch (PlacaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new Response<VeiculoDTO>(e.getMessage(), null));
        }
    }

    public ResponseEntity<Response<Boolean>> deletarVeiculoPelaPlaca(String placa) {
        try {
            this.validacaoPlaca.isPlacaValida(placa);
            Optional<Veiculo> optionalVeiculo = buscarVeiculoPelaPlaca(placa);
            if (optionalVeiculo.isPresent()) {
                this.veiculoRepository.delete(optionalVeiculo.get());
                Response<Boolean> response = new Response<>("Sucesso", Boolean.TRUE);
                return ResponseEntity.ok(response);
            } else {
                throw new VeiculoNaoEncontradoException();
            }
        } catch (VeiculoNaoEncontradoException | PlacaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new Response<Boolean>(e.getMessage(), Boolean.FALSE));
        }
    }

    public ResponseEntity<Response<VeiculoDTO>> atualizar(VeiculoDTO veiculoDTO) {
        try {
            this.validacaoPlaca.isPlacaValida(veiculoDTO.getPlaca());
            Optional<Veiculo> optionalVeiculo = buscarVeiculoPelaPlaca(veiculoDTO.getPlaca());
            if (optionalVeiculo.isPresent()) {
                Veiculo veiculo = new Veiculo();
                veiculo.setId(optionalVeiculo.get().getId());
                veiculo.setPlaca(veiculoDTO.getPlaca());
                veiculo.setModelo(veiculoDTO.getModelo());
                veiculo.setMarca(veiculoDTO.getMarca());
                veiculo.setDisponivel(veiculoDTO.getDisponivel());
                veiculo.setDataFabricacao(veiculoDTO.getDataFabricacao());
                Veiculo veiculoAtualizado = this.veiculoRepository.save(veiculo);
                VeiculoDTO veiculoDTOAtualizado = this.veiculoDTOConverter.convertFrom(veiculoAtualizado);
                Response<VeiculoDTO> response = new Response<>("Sucesso", veiculoDTOAtualizado);
                return ResponseEntity.ok(response);
            } else {
                throw new VeiculoNaoEncontradoException();
            }
        } catch (VeiculoNaoEncontradoException | PlacaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    public ResponseEntity<Response<List<VeiculoDTO>>> listarTodos() {
        List<VeiculoDTO> lista = veiculoRepository.findAll().stream().map(veiculo -> {
            VeiculoDTO veiculoDTO = new VeiculoDTO();
            veiculoDTO = this.veiculoDTOConverter.convertFrom(veiculo);
            return veiculoDTO;
        }).collect(Collectors.toList());
        if (lista.size() == 0) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new Response<List<VeiculoDTO>>("Nao ha veiculos a serem listados", new ArrayList<>()));
        } else {
            return ResponseEntity.ok(new Response<List<VeiculoDTO>>("Sucesso", lista));
        }

    }

    private Optional<Veiculo> buscarVeiculoPelaPlaca(String placa) {
        return this.veiculoRepository.findByPlaca(placa);
    }
}

