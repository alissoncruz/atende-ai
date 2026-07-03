package com.atendeai.modules.church.service;

import com.atendeai.modules.church.dto.ChurchRequest;
import com.atendeai.modules.church.dto.ChurchResponse;
import com.atendeai.modules.church.model.Church;
import com.atendeai.modules.church.repository.ChurchRepository;
import com.atendeai.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChurchService {

    private final ChurchRepository repository;

    public List<ChurchResponse> list() {
        return repository.findByActiveTrueOrderByNameAsc().stream()
                .map(ChurchResponse::from)
                .toList();
    }

    @Transactional
    public ChurchResponse create(ChurchRequest req) {
        Church church = Church.builder()
                .name(req.name())
                .address(req.address())
                .build();
        return ChurchResponse.from(repository.save(church));
    }

    @Transactional
    public ChurchResponse update(UUID id, ChurchRequest req) {
        Church church = findById(id);
        church.setName(req.name());
        church.setAddress(req.address());
        return ChurchResponse.from(repository.save(church));
    }

    @Transactional
    public void delete(UUID id) {
        Church church = findById(id);
        church.setActive(false);
        repository.save(church);
    }

    private Church findById(UUID id) {
        return repository.findById(id)
                .filter(Church::isActive)
                .orElseThrow(() -> new BusinessException("Igreja não encontrada", HttpStatus.NOT_FOUND));
    }
}
