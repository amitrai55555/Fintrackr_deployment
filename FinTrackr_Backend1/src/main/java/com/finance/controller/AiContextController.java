package com.finance.controller;

import com.finance.dto.ai.AiContextDTO;
import com.finance.security.UserPrincipal;
import com.finance.service.AiContextService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiContextController {

    private final AiContextService aiContextService;

    public AiContextController(AiContextService aiContextService) {
        this.aiContextService = aiContextService;
    }

    @GetMapping("/context")
    public ResponseEntity<AiContextDTO> getContext(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        LocalDate resolvedTo = to != null ? to : LocalDate.now();
        LocalDate resolvedFrom = from != null ? from : resolvedTo.withDayOfMonth(1);
        return ResponseEntity.ok(aiContextService.getContext(principal.getId(), resolvedFrom, resolvedTo));
    }
}
