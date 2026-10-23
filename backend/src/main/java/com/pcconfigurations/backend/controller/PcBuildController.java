package com.pcconfigurations.backend.controller;

import com.pcconfigurations.backend.dto.PcBuildDTO;
import com.pcconfigurations.backend.dto.PcBuildRequest;
import com.pcconfigurations.backend.entity.User;
import com.pcconfigurations.backend.service.PcBuildService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/builds")
public class PcBuildController {

    @Autowired
    private PcBuildService pcBuildService;

    @PostMapping
    public ResponseEntity<?> createBuild(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PcBuildRequest request) {
        try {
            return ResponseEntity.ok(pcBuildService.createBuild(user, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<PcBuildDTO>> getUserBuilds(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(pcBuildService.getUserBuilds(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBuild(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(pcBuildService.getBuildById(user, id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBuild(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody PcBuildRequest request) {
        try {
            return ResponseEntity.ok(pcBuildService.updateBuild(user, id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBuild(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        try {
            pcBuildService.deleteBuild(user, id);
            return ResponseEntity.ok(Map.of("message", "Build deleted"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
    }
}
