package com.atendeai.modules.scheduling.service;

import com.atendeai.modules.auth.repository.UserRepository;
import com.atendeai.modules.customer.model.Customer;
import com.atendeai.modules.customer.repository.CustomerRepository;
import com.atendeai.modules.scheduling.dto.AppointmentRequest;
import com.atendeai.modules.scheduling.dto.AppointmentResponse;
import com.atendeai.modules.scheduling.dto.StatusUpdateRequest;
import com.atendeai.modules.scheduling.model.Appointment;
import com.atendeai.modules.scheduling.repository.AppointmentRepository;
import com.atendeai.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public Page<AppointmentResponse> list(UUID customerId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("scheduledAt").descending());
        if (customerId != null) {
            return appointmentRepository.findByCustomer_Id(customerId, pageable)
                    .map(AppointmentResponse::from);
        }
        return appointmentRepository.findAll(pageable).map(AppointmentResponse::from);
    }

    public AppointmentResponse get(UUID id) {
        return AppointmentResponse.from(findById(id));
    }

    public List<AppointmentResponse> getSchedule(Instant from, Instant to) {
        return appointmentRepository.findSchedule(from, to)
                .stream().map(AppointmentResponse::from).toList();
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest req) {
        Customer customer = customerRepository.findById(req.customerId())
                .orElseThrow(() -> new BusinessException("Cliente não encontrado", HttpStatus.NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .createdBy(creator)
                .serviceType(req.serviceType())
                .title(req.title())
                .description(req.description())
                .scheduledAt(req.scheduledAt())
                .durationMinutes(req.durationMinutes() != null ? req.durationMinutes() : 60)
                .notes(req.notes())
                .build();

        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse updateStatus(UUID id, StatusUpdateRequest req) {
        Appointment appointment = findById(id);
        appointment.setStatus(req.status());
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    public List<String> getAvailableSlots(Instant date, String service) {
        // Slots padrão: a cada hora das 8h às 18h
        List<Appointment> booked = appointmentRepository.findByDate(date);
        Instant dayStart = date.truncatedTo(ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS);

        return java.util.stream.IntStream.range(0, 10)
                .mapToObj(i -> dayStart.plus(i, ChronoUnit.HOURS))
                .filter(slot -> booked.stream().noneMatch(a ->
                        Math.abs(a.getScheduledAt().toEpochMilli() - slot.toEpochMilli()) < 3_600_000))
                .map(Instant::toString)
                .toList();
    }

    private Appointment findById(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Agendamento não encontrado", HttpStatus.NOT_FOUND));
    }
}
