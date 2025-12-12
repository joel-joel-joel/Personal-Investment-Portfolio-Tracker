package com.joelcode.personalinvestmentportfoliotracker.controllers.entitycontrollers;

import com.joelcode.personalinvestmentportfoliotracker.dto.portfoliosnapshot.PortfolioSnapshotDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.portfoliosnapshot.PortfolioSnapshotCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.services.portfoliosnapshot.PortfolioSnapshotService;
import com.joelcode.personalinvestmentportfoliotracker.services.scheduler.PortfolioSnapshotScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/snapshots")
@Profile("!test")
public class PortfolioSnapshotController {

    @Autowired
    public PortfolioSnapshotService snapshotService;

    @Autowired
    public PortfolioSnapshotScheduler snapshotScheduler;

    // Get all snapshots
    @GetMapping
    public ResponseEntity<List<PortfolioSnapshotDTO>> getAllSnapshots() {
        return ResponseEntity.ok(snapshotService.getAllSnapshots());
    }

    // Get snapshot by ID
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioSnapshotDTO> getSnapshotById(@PathVariable UUID id) {
        PortfolioSnapshotDTO snapshot = snapshotService.getSnapshotById(id);
        if (snapshot != null) {
            return ResponseEntity.ok(snapshot);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new snapshot
    @PostMapping
    public ResponseEntity<PortfolioSnapshotDTO> createSnapshot(@RequestBody PortfolioSnapshotCreateRequest request) {
        PortfolioSnapshotDTO created = snapshotService.createSnapshot(request);
        return ResponseEntity.ok(created);
    }

    // Delete a snapshot
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnapshot(@PathVariable UUID id) {
       snapshotService.deleteSnapshot(id);
       return ResponseEntity.noContent().build();
    }

    // Get all snapshots for a specific account
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PortfolioSnapshotDTO>> getSnapshotsForAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(snapshotService.getSnapshotsForAccount(accountId));
    }

    // Generate snapshot for today for a specific account
    @PostMapping("/generate/{accountId}")
    public ResponseEntity<Map<String, String>> generateSnapshotForAccount(@PathVariable UUID accountId) {
        try {
            snapshotScheduler.createSnapshotForAccount(accountId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Snapshot generated successfully for account: " + accountId);
            response.put("accountId", accountId.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("accountId", accountId.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Generate snapshots for all accounts
    @PostMapping("/generate-all")
    public ResponseEntity<Map<String, Object>> generateSnapshotsForAllAccounts() {
        try {
            int count = snapshotScheduler.createSnapshotsForAllAccounts();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Snapshot generation completed");
            response.put("snapshotsGenerated", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
