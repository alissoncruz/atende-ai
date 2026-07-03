package com.atendeai.modules.customer.service;

import com.atendeai.modules.customer.dto.CustomerRequest;
import com.atendeai.modules.customer.dto.CustomerResponse;
import com.atendeai.modules.customer.model.Customer;
import com.atendeai.modules.customer.repository.CustomerRepository;
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
public class CustomerService {

    private final CustomerRepository repository;

    public Page<CustomerResponse> list(String q, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Customer> result = StringUtils.hasText(q)
                ? repository.search(q, pageable)
                : repository.findByActiveTrue(pageable);
        return result.map(CustomerResponse::from);
    }

    public CustomerResponse get(UUID id) {
        return CustomerResponse.from(findById(id));
    }

    @Transactional
    public CustomerResponse create(CustomerRequest req) {
        Customer customer = Customer.builder()
                .name(req.name())
                .email(req.email())
                .phone(req.phone())
                .document(req.document())
                .address(req.address())
                .notes(req.notes())
                .build();
        return CustomerResponse.from(repository.save(customer));
    }

    @Transactional
    public CustomerResponse update(UUID id, CustomerRequest req) {
        Customer customer = findById(id);
        customer.setName(req.name());
        customer.setEmail(req.email());
        customer.setPhone(req.phone());
        customer.setDocument(req.document());
        customer.setAddress(req.address());
        customer.setNotes(req.notes());
        return CustomerResponse.from(repository.save(customer));
    }

    @Transactional
    public void delete(UUID id) {
        Customer customer = findById(id);
        customer.setActive(false);
        repository.save(customer);
    }

    private Customer findById(UUID id) {
        return repository.findById(id)
                .filter(Customer::isActive)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado", HttpStatus.NOT_FOUND));
    }
}
