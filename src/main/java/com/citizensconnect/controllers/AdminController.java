package com.citizensconnect.controllers;

import com.citizensconnect.models.Politician;
import com.citizensconnect.models.User;
import com.citizensconnect.models.UserStatus;
import com.citizensconnect.payload.response.MessageResponse;
import com.citizensconnect.repository.IssueRepository;
import com.citizensconnect.repository.PoliticianRepository;
import com.citizensconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PoliticianRepository politicianRepository;

    @Autowired
    IssueRepository issueRepository;

    // ─── User Management ────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully."));
    }

    @DeleteMapping("/delete-issue/{id}")
    public ResponseEntity<?> deleteIssue(@PathVariable Long id) {
        if (!issueRepository.existsById(id)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Issue not found."));
        }
        issueRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Issue deleted successfully."));
    }

    // ─── Politician Approval System ──────────────────────────────────────

    /**
     * GET /api/admin/pending-politicians
     * Returns all politicians with status = PENDING
     */
    @GetMapping("/pending-politicians")
    public ResponseEntity<List<Politician>> getPendingPoliticians() {
        List<Politician> pending = politicianRepository.findByStatus(UserStatus.PENDING);
        return ResponseEntity.ok(pending);
    }

    /**
     * POST /api/admin/approve-politician/{id}
     * Sets politician status to APPROVED
     */
    @PostMapping("/approve-politician/{id}")
    public ResponseEntity<?> approvePolitician(@PathVariable Long id) {
        Optional<Politician> politicianOpt = politicianRepository.findById(id);
        if (politicianOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Politician not found."));
        }
        Politician politician = politicianOpt.get();
        politician.setStatus(UserStatus.APPROVED);
        politicianRepository.save(politician);
        return ResponseEntity.ok(new MessageResponse("Politician approved successfully."));
    }

    /**
     * POST /api/admin/reject-politician/{id}
     * Sets politician status to REJECTED
     */
    @PostMapping("/reject-politician/{id}")
    public ResponseEntity<?> rejectPolitician(@PathVariable Long id) {
        Optional<Politician> politicianOpt = politicianRepository.findById(id);
        if (politicianOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Politician not found."));
        }
        Politician politician = politicianOpt.get();
        politician.setStatus(UserStatus.REJECTED);
        politicianRepository.save(politician);
        return ResponseEntity.ok(new MessageResponse("Politician rejected."));
    }

    // Legacy alias kept for backward compatibility
    @PutMapping("/approve-politician/{id}")
    public ResponseEntity<?> approvePoliticianPut(@PathVariable Long id) {
        return approvePolitician(id);
    }
}
