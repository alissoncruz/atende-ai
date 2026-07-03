package com.atendeai.modules.tithepayment.controller;

import com.atendeai.modules.tithepayment.dto.MarkPaidRequest;
import com.atendeai.modules.tithepayment.dto.MonthlyHistoryItem;
import com.atendeai.modules.tithepayment.dto.MonthlySummaryResponse;
import com.atendeai.modules.tithepayment.dto.MonthlyTitherRow;
import com.atendeai.modules.tithepayment.service.TithePaymentService;
import com.atendeai.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tithe-payments")
@RequiredArgsConstructor
public class TithePaymentController {

    private final TithePaymentService service;

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<MonthlyTitherRow>>> monthly(
            @RequestParam String month,
            @RequestParam(required = false) UUID churchId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(service.listMonthly(month, churchId, q, status)));
    }

    @GetMapping("/monthly/summary")
    public ResponseEntity<ApiResponse<MonthlySummaryResponse>> summary(
            @RequestParam String month,
            @RequestParam(required = false) UUID churchId) {
        return ResponseEntity.ok(ApiResponse.ok(service.summary(month, churchId)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<MonthlyHistoryItem>>> history(
            @RequestParam(defaultValue = "6") int months,
            @RequestParam(required = false) UUID churchId) {
        return ResponseEntity.ok(ApiResponse.ok(service.history(months, churchId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MonthlyTitherRow>> markPaid(@Valid @RequestBody MarkPaidRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.markPaid(req)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> unmark(
            @RequestParam UUID titherId,
            @RequestParam String month) {
        service.unmark(titherId, month);
        return ResponseEntity.ok(ApiResponse.ok(null, "Pagamento removido"));
    }
}
