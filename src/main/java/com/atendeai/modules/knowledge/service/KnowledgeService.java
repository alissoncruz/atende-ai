package com.atendeai.modules.knowledge.service;

import com.atendeai.modules.customer.model.Customer;
import com.atendeai.modules.customer.repository.CustomerRepository;
import com.atendeai.modules.knowledge.dto.KnowledgeRequest;
import com.atendeai.modules.knowledge.dto.KnowledgeResponse;
import com.atendeai.modules.knowledge.model.KnowledgeDocument;
import com.atendeai.modules.knowledge.repository.KnowledgeRepository;
import com.atendeai.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;
    private final CustomerRepository customerRepository;

    public List<KnowledgeResponse> list(UUID customerId) {
        if (customerId != null) {
            return knowledgeRepository
                    .findByCustomer_IdAndActiveTrueOrderByCreatedAtDesc(customerId)
                    .stream().map(KnowledgeResponse::from).toList();
        }
        return Stream.concat(
                knowledgeRepository.findByCustomerIsNullAndActiveTrue().stream(),
                knowledgeRepository.findAll().stream().filter(d -> d.getCustomer() != null && d.isActive())
        ).map(KnowledgeResponse::from).toList();
    }

    @Transactional
    public KnowledgeResponse create(KnowledgeRequest req) {
        Customer customer = null;
        if (req.customerId() != null) {
            customer = customerRepository.findById(req.customerId())
                    .orElseThrow(() -> new BusinessException("Cliente não encontrado", HttpStatus.NOT_FOUND));
        }

        KnowledgeDocument doc = KnowledgeDocument.builder()
                .customer(customer)
                .title(req.title())
                .content(req.content())
                .category(req.category())
                .build();

        return KnowledgeResponse.from(knowledgeRepository.save(doc));
    }

    @Transactional
    public void delete(UUID id) {
        KnowledgeDocument doc = knowledgeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Documento não encontrado", HttpStatus.NOT_FOUND));
        doc.setActive(false);
        knowledgeRepository.save(doc);
    }
}
