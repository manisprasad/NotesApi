package org.manis.notes.services;

import org.manis.notes.models.StickyNote;
import org.manis.notes.models.StickyNoteGroup;
import org.manis.notes.models.User;
import org.manis.notes.models.UserDetailsResponse;
import org.manis.notes.repo.UserRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }




    public UserDetailsResponse getUserDetails(String username) {

        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            int totalGroupCount = getTotalNumberOfGroups(user);
            int totalStickyNotesCount = getTotalNumberOfStickyNotes(user);
            int totalfav = getTotalfavStickyNotesCount(user);
            List<String> stickyNoteGroupNames = getAllGroupNames(user);

            UserDetailsResponse userDetails = new UserDetailsResponse();
            userDetails.setName(user.getUsername());
            userDetails.setTotalFav(totalfav);
            userDetails.setCreatedDate(user.getCreatedDate());
            user.setRole(user.getRole());
            userDetails.setRecentActivities(getRecentActivities(user));
            userDetails.setStickyNoteGroupNames(stickyNoteGroupNames);
            userDetails.setEmail(user.getEmail());
            userDetails.setTotalGroup(totalGroupCount);
            userDetails.setTotalStickyNotes(totalStickyNotesCount);

            return userDetails;
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public List<StickyNote> getRecentActivities(User user) {
        // Get the list of sticky note groups and flatten them to a single list of sticky notes
        List<StickyNote> allStickyNotes = user.getStickyNoteGroups().stream()
                .flatMap(group -> group.getStickyNotes().stream())
                .toList();

        // Sort sticky notes by createdDate in descending order and limit to 5 notes
        List<StickyNote> recentNotes = allStickyNotes.stream()
                .sorted(Comparator.comparing(StickyNote::getCreatedAt).reversed())
                .limit(5) // Limit to 5 most recent notes or less if less than 5 notes exist
                .collect(Collectors.toList());

        return recentNotes;
    }

    private int getTotalNumberOfGroups(User user) {
        return user.getStickyNoteGroups().size();
    }

    private int getTotalNumberOfStickyNotes(User user) {
        return user.getStickyNoteGroups().stream()
                .mapToInt(group -> group.getStickyNotes().size())
                .sum();
    }
    private int getTotalfavStickyNotesCount(User user) {
        return user.getStickyNoteGroups().stream()
                .mapToInt(group -> (int) group.getStickyNotes().stream()
                        .filter(StickyNote::isFavourite)
                        .count()
                ).sum();
    }

    private List<String> getAllGroupNames(User user) {
        return user.getStickyNoteGroups().stream()
                .map(StickyNoteGroup::getName)
                .collect(Collectors.toList());
    }



}
