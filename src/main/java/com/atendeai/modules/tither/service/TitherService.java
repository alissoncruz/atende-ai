package com.atendeai.modules.tither.service;

import com.atendeai.modules.church.model.Church;
import com.atendeai.modules.church.repository.ChurchRepository;
import com.atendeai.modules.tither.dto.TitherRequest;
import com.atendeai.modules.tither.dto.TitherResponse;
import com.atendeai.modules.tither.model.Tither;
import com.atendeai.modules.tither.repository.TitherRepository;
import com.atendeai.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TitherService {

    private final TitherRepository repository;
    private final ChurchRepository churchRepository;

    public Page<TitherResponse> list(String q, UUID churchId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Tither> result = StringUtils.hasText(q)
                ? repository.search(q, churchId, pageable)
                : (churchId != null
                        ? repository.findByActiveTrueAndChurch_Id(churchId, pageable)
                        : repository.findByActiveTrue(pageable));
        return result.map(TitherResponse::from);
    }

    public TitherResponse get(UUID id) {
        return TitherResponse.from(findById(id));
    }

    @Transactional
    public TitherResponse create(TitherRequest req) {
        if (repository.existsByCpf(req.cpf())) {
            throw new BusinessException("Já existe um dizimista cadastrado com este CPF");
        }
        Church church = findChurch(req.churchId());

        Tither tither = Tither.builder()
                .name(req.name())
                .cpf(req.cpf())
                .phone(req.phone())
                .email(req.email())
                .birthDate(req.birthDate())
                .zipCode(req.zipCode())
                .street(req.street())
                .number(req.number())
                .neighborhood(req.neighborhood())
                .city(req.city())
                .state(req.state())
                .church(church)
                .startDate(req.startDate())
                .referenceAmount(req.referenceAmount())
                .build();
        return TitherResponse.from(repository.save(tither));
    }

    @Transactional
    public TitherResponse update(UUID id, TitherRequest req) {
        Tither tither = findById(id);
        if (!tither.getCpf().equals(req.cpf()) && repository.existsByCpf(req.cpf())) {
            throw new BusinessException("Já existe um dizimista cadastrado com este CPF");
        }
        Church church = findChurch(req.churchId());

        tither.setName(req.name());
        tither.setCpf(req.cpf());
        tither.setPhone(req.phone());
        tither.setEmail(req.email());
        tither.setBirthDate(req.birthDate());
        tither.setZipCode(req.zipCode());
        tither.setStreet(req.street());
        tither.setNumber(req.number());
        tither.setNeighborhood(req.neighborhood());
        tither.setCity(req.city());
        tither.setState(req.state());
        tither.setChurch(church);
        tither.setStartDate(req.startDate());
        tither.setReferenceAmount(req.referenceAmount());
        return TitherResponse.from(repository.save(tither));
    }

    @Transactional
    public void delete(UUID id) {
        Tither tither = findById(id);
        tither.setActive(false);
        repository.save(tither);
    }

    private Tither findById(UUID id) {
        return repository.findById(id)
                .filter(Tither::isActive)
                .orElseThrow(() -> new BusinessException("Dizimista não encontrado", HttpStatus.NOT_FOUND));
    }

    private Church findChurch(UUID churchId) {
        return churchRepository.findById(churchId)
                .orElseThrow(() -> new BusinessException("Igreja não encontrada", HttpStatus.NOT_FOUND));
    }
}
