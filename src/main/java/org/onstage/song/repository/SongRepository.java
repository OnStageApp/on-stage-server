package org.onstage.song.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.song.client.SongDTO;
import org.onstage.song.client.SongFilter;
import org.onstage.song.client.SongOverview;
import org.onstage.song.model.Song;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
@RequiredArgsConstructor
public class SongRepository {
    private final SongRepo songRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<Song> findById(String id) {
        return songRepo.findById(id);
    }

    public SongDTO findProjectionById(String id) {
        Aggregation aggregation = newAggregation(songProjectionPipeline(Criteria.where(Song.Fields.id).is(id)));
        return mongoTemplate.aggregate(aggregation, Song.class, SongDTO.class).getUniqueMappedResult();
    }

    public Optional<SongOverview> findOverviewById(String id) {
        Aggregation aggregation = newAggregation(songOverviewProjectionPipeline(Criteria.where(Song.Fields.id).is(id), null, null));
        return Optional.ofNullable(mongoTemplate.aggregate(aggregation, Song.class, SongOverview.class)
                .getUniqueMappedResult());
    }

    public List<SongOverview> getAll(SongFilter songFilter) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (songFilter.search() != null && !songFilter.search().isEmpty()) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where(Song.Fields.title).regex(songFilter.search(), "i")
            ));
        }

        Criteria finalCriteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Sort sort = Sort.by(Sort.Order.asc(Song.Fields.title), Sort.Order.asc("artist.name"));
        Aggregation aggregation = newAggregation(songOverviewProjectionPipeline(finalCriteria, songFilter.artistId(), sort));
        return mongoTemplate.aggregate(aggregation, Song.class, SongOverview.class).getMappedResults();
    }

    public Song save(Song song) {
        return songRepo.save(song);
    }

    private List<AggregationOperation> songProjectionPipeline(Criteria criteria) {
        List<AggregationOperation> operations = new ArrayList<>();

        if (criteria != null) {
            operations.add(match(criteria));
        }

        operations.add(lookup("artists", "artistId", "_id", "artist"));
        operations.add(unwind("artist"));
        operations.add(project()
                .and("_id").as("id")
                .and("title").as("title")
                .and("structure").as("structure")
                .and("rawSections").as("rawSections")
                .and("createdAt").as("createdAt")
                .and("updatedAt").as("updatedAt")
                .and("originalKey").as("originalKey")
                .and("tempo").as("tempo")
                .and("artist").as("artist"));

        return operations;
    }

    public List<AggregationOperation> songOverviewProjectionPipeline(Criteria criteria, String artistId, Sort sort) {
        List<AggregationOperation> operations = new ArrayList<>();

        if (criteria != null) {
            operations.add(match(criteria));
        }

        operations.add(lookup("artists", "artistId", "_id", "artist"));
        operations.add(unwind("artist"));

        if (artistId != null && !artistId.isEmpty()) {
            operations.add(match(Criteria.where("artist._id").is(artistId)));
        }

        operations.add(project()
                .and("_id").as("id")
                .and("title").as("title")
                .and("originalKey").as("key")
                .and("tempo").as("tempo")
                .and("artist").as("artist"));

        if (sort != null) {
            operations.add(sort(sort));
        }

        return operations;
    }
}