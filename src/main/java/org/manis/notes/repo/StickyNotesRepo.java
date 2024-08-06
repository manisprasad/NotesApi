package org.manis.notes.repo;

import org.bson.types.ObjectId;
import org.manis.notes.models.StickyNote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface StickyNotesRepo extends MongoRepository<StickyNote, ObjectId> {

    Collection<Object> findByColor(String color);
}
