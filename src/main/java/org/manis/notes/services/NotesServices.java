package org.manis.notes.services;

import org.bson.types.ObjectId;
import org.manis.notes.models.StickyNote;
import org.manis.notes.models.StickyNoteGroup;
import org.manis.notes.models.User;
import org.manis.notes.repo.StickyNotesGroupRepo;
import org.manis.notes.repo.StickyNotesRepo;
import org.manis.notes.repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotesServices {

    private final UserRepo userRepo;
    private final StickyNotesRepo stickyNotesRepo;
    private final StickyNotesGroupRepo stickyNotesGroupRepo;

    public NotesServices(UserRepo userRepo, StickyNotesRepo stickyNotesRepo, StickyNotesGroupRepo stickyNotesGroupRepo) {
        this.userRepo = userRepo;
        this.stickyNotesRepo = stickyNotesRepo;
        this.stickyNotesGroupRepo = stickyNotesGroupRepo;
    }




    public ResponseEntity<String> deleteNote(String username, String noteId) {
        // Find user by username
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build(); // User not found
        }
        User user = optionalUser.get();

        // Find sticky note by ID
        Optional<StickyNote> noteOptional = stickyNotesRepo.findById(noteId);
        if (noteOptional.isEmpty()) {
            return ResponseEntity.notFound().build(); 
        }
        StickyNote note = noteOptional.get();

        
        boolean noteBelongsToUser = user.getStickyNoteGroups().stream()
                .flatMap(group -> group.getStickyNotes().stream())
                .anyMatch(stickyNote -> stickyNote.getId().equals(noteId));

        if (!noteBelongsToUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to delete this note.");
        }

      
        boolean noteRemoved = false;

      
        for (StickyNoteGroup group : user.getStickyNoteGroups()) {
            if (group.getStickyNotes().remove(note)) {
                noteRemoved = true;
                // Save the updated group
                stickyNotesGroupRepo.save(group);
            }
        }

        System.out.println("reached herer");
        if (!noteRemoved) {
            return ResponseEntity.notFound().build(); // Sticky note was not found in any group
        }

        // Delete the sticky note from the repository
        stickyNotesRepo.delete(note);

        // Save the updated user
        userRepo.save(user);

        return ResponseEntity.ok("Note deleted successfully");
    }



    public ResponseEntity<String> updateNote(String username, String noteId, StickyNote updatedNote) {
        // Find user by username
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build(); // User not found
        }
        User user = optionalUser.get();

        // Find sticky note by ID
        Optional<StickyNote> noteOptional = stickyNotesRepo.findById(noteId);
        if (noteOptional.isEmpty()) {
            return ResponseEntity.notFound().build(); // Sticky note not found
        }
        StickyNote existingNote = noteOptional.get();

        // Check if the sticky note belongs to the user
        if (!user.getStickyNoteGroups().stream()
                .flatMap(group -> group.getStickyNotes().stream())
                .anyMatch(note -> note.getId().equals(noteId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update this note.");
        }

        // Update the sticky note attributes
        String oldGroupName = existingNote.getGroupName();
        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.setColor(updatedNote.getColor());
        existingNote.setGroupName(updatedNote.getGroupName());
        existingNote.setFavourite(updatedNote.isFavourite());
        existingNote.setUpdatedAt(System.currentTimeMillis());

        // Handle group changes
        if (!oldGroupName.equals(updatedNote.getGroupName())) {
            // Find or create the new group
            StickyNoteGroup newGroup = user.getStickyNoteGroups().stream()
                    .filter(g -> g.getName().equals(updatedNote.getGroupName()))
                    .findFirst()
                    .orElseGet(() -> {
                        StickyNoteGroup newGroupObj = new StickyNoteGroup();
                        newGroupObj.setName(updatedNote.getGroupName());
                        return stickyNotesGroupRepo.save(newGroupObj);
                    });

            // Remove note from the old group
            StickyNoteGroup oldGroup = user.getStickyNoteGroups().stream()
                    .filter(g -> g.getName().equals(oldGroupName))
                    .findFirst()
                    .orElse(null);

            System.out.println(oldGroup);
            if (oldGroup != null) {
                System.out.println(oldGroup.getStickyNotes());
                oldGroup.getStickyNotes().remove(existingNote);
                System.out.println(oldGroup.getStickyNotes());
                stickyNotesGroupRepo.save(oldGroup); // Save changes to the old group
            }

            // Add note to the new group
            newGroup.getStickyNotes().add(existingNote);
            stickyNotesGroupRepo.save(newGroup); // Save changes to the new group
        }

        // Save the updated sticky note
        stickyNotesRepo.save(existingNote);

        return ResponseEntity.ok("Note updated successfully");
    }







//    public ResponseEntity<String> addNote(String username, StickyNote addedNote) {
//        stickyNotesRepo.save(addedNote);
//        long currentTime = System.currentTimeMillis();
//        addedNote.setCreatedAt(currentTime);
//        addedNote.setUpdatedAt(currentTime);
//        return ResponseEntity.ok("Note added successfully");
//    }


    public ResponseEntity<String> addNote(String username, StickyNote addedNote) {
        // Find user by username
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();

        // Assign created and updated timestamps
        long currentTime = System.currentTimeMillis();
        addedNote.setCreatedAt(currentTime);
        addedNote.setUpdatedAt(currentTime);

        // Save sticky note
        StickyNote savedNote = stickyNotesRepo.save(addedNote);

        // Find or create the sticky note group
        StickyNoteGroup group = user.getStickyNoteGroups().stream()
                .filter(g -> g != null && g.getName().equals(addedNote.getGroupName())) // Added null check
                .findFirst()
                .orElseGet(() -> {
                    // If the group does not exist, create a new one
                    StickyNoteGroup newGroup = new StickyNoteGroup();
                    newGroup.setName(addedNote.getGroupName());
                    newGroup.getStickyNotes().add(savedNote);
                    newGroup = stickyNotesGroupRepo.save(newGroup);
                    return newGroup;
                });

     
        if (group == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to initialize the group.");
        }

        // Add the sticky note to the group and update the user
        if (!group.getStickyNotes().contains(savedNote)) {
            group.getStickyNotes().add(savedNote);
            stickyNotesGroupRepo.save(group); 
        }

        if (!user.getStickyNoteGroups().contains(group)) {
            user.getStickyNoteGroups().add(group);
        }

        userRepo.save(user);

        return ResponseEntity.ok("Note added successfully");
    }




    public ResponseEntity<List<StickyNote>> getAllStickyNotes(String username) {
        // Fetch the User by username
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build(); // User not found
        }

        User user = userOptional.get();

        // Retrieve all StickyNoteGroups associated with the user
        List<StickyNote> allStickyNotes = user.getStickyNoteGroups().stream()
                .flatMap(group -> group.getStickyNotes().stream())
                .collect(Collectors.toList()); 

        return ResponseEntity.ok(allStickyNotes);
    }

    public ResponseEntity<List<StickyNote>> filterByGroupName(String username, String groupName) {
        // Find the user by username
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOptional.get();




        List<StickyNote> filteredNotes = user.getStickyNoteGroups().stream()
                .flatMap(group -> group.getStickyNotes().stream())
                .filter(note -> groupName.equals(note.getGroupName()))
                .collect(Collectors.toList());

      
        return ResponseEntity.ok(filteredNotes);
    }

    public ResponseEntity<List<StickyNote>> filterByColor(String username, String color) {
        // Retrieve the user by username
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOptional.get();

        // Filter sticky notes by color
        List<StickyNote> colorNotes = user.getStickyNoteGroups().stream()
                .flatMap(group -> group.getStickyNotes().stream())
                .filter(note -> color.equals(note.getColor()))
                .toList();

       

        // Return the filtered notes
        return ResponseEntity.ok(colorNotes);
    }



}
