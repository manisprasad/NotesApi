package org.manis.notes.controllers;

import org.bson.types.ObjectId;
import org.manis.notes.models.StickyNote;
import org.manis.notes.services.NotesServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin()
@RequestMapping("/notes")
public class NotesController {

    private final NotesServices notesServices;

    public NotesController(NotesServices notesServices) {
        this.notesServices = notesServices;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllStickyNotes() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            return notesServices.getAllStickyNotes(username);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving all sticky notes: " + e.getMessage());
        }
    }

    @GetMapping("/filterByGroupName/{groupName}")
    public ResponseEntity<?> filterByGroupName(@PathVariable String groupName) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            return notesServices.filterByGroupName(username, groupName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while filtering by group name: " + e.getMessage());
        }
    }

    @GetMapping("/filterByColor/{color}")
    public ResponseEntity<?> filterByColor(@PathVariable String color) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            return notesServices.filterByColor(username, color);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while filtering by color: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNote(@RequestBody StickyNote note) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            System.out.println("try run");
            return notesServices.addNote(username, note);
        } catch (Exception e) {
            System.out.println("exceptio run");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding the note: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    public ResponseEntity<String> check() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.OK).body(name);
    }

    @PutMapping("/update/{noteId}")
    public ResponseEntity<?> updateNote(
            @PathVariable String noteId,
            @RequestBody StickyNote updatedNote) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            return notesServices.updateNote(username, noteId, updatedNote);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the note: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable String noteId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            return notesServices.deleteNote(username, noteId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the note: " + e.getMessage());
        }
    }
}
