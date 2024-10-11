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
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.*;

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
        Criteria criteria = Criteria.where(Song.Fields.id).is(id);

        Map<String, String> projectionFields = new LinkedHashMap<>();
        projectionFields.put("_id", "id");
        projectionFields.put("title", "title");
        projectionFields.put("structure", "structure");
        projectionFields.put("rawSections", "rawSections");
        projectionFields.put("createdAt", "createdAt");
        projectionFields.put("updatedAt", "updatedAt");
        projectionFields.put("originalKey", "originalKey");
        projectionFields.put("tempo", "tempo");
        projectionFields.put("artist", "artist");
        projectionFields.put("teamId", "teamId");

        Aggregation aggregation = buildAggregation(criteria, null, projectionFields, null);

        return mongoTemplate.aggregate(aggregation, Song.class, SongDTO.class).getUniqueMappedResult();
    }

    public Optional<SongOverview> findOverviewById(String id) {
        Criteria criteria = Criteria.where(Song.Fields.id).is(id);

        Map<String, String> projectionFields = new LinkedHashMap<>();
        projectionFields.put("_id", "id");
        projectionFields.put("title", "title");
        projectionFields.put("originalKey", "key");
        projectionFields.put("tempo", "tempo");
        projectionFields.put("artist", "artist");
        projectionFields.put("teamId", "teamId");

        Aggregation aggregation = buildAggregation(criteria, null, projectionFields, null);

        SongOverview result = mongoTemplate.aggregate(aggregation, Song.class, SongOverview.class).getUniqueMappedResult();
        return Optional.ofNullable(result);
    }

    public List<SongOverview> getAll(SongFilter songFilter, String teamId) {
        Criteria preLookupCriteria = new Criteria();
        if (songFilter.search() != null && !songFilter.search().isEmpty()) {
            preLookupCriteria.and(Song.Fields.title).regex(songFilter.search(), "i");
        }
        Criteria teamCriteria = getTeamIdCriteria(teamId, songFilter.includeOnlyTeamSongs());
        if (teamCriteria != null) {
            preLookupCriteria.andOperator(teamCriteria);
        }

        Criteria postLookupCriteria = null;
        if (songFilter.artistId() != null && !songFilter.artistId().isEmpty()) {
            postLookupCriteria = Criteria.where("artist._id").is(songFilter.artistId());
        }

        Map<String, String> projectionFields = new LinkedHashMap<>();
        projectionFields.put("_id", "id");
        projectionFields.put("title", "title");
        projectionFields.put("originalKey", "key");
        projectionFields.put("tempo", "tempo");
        projectionFields.put("artist", "artist");
        projectionFields.put("teamId", "teamId");

        Sort sort = Sort.by(Sort.Order.asc(Song.Fields.title), Sort.Order.asc("artist.name"));

        Aggregation aggregation = buildAggregation(preLookupCriteria, postLookupCriteria, projectionFields, sort);

        return mongoTemplate.aggregate(aggregation, Song.class, SongOverview.class).getMappedResults();
    }

    public Song save(Song song) {
        return songRepo.save(song);
    }

    private Aggregation buildAggregation(Criteria preLookupCriteria, Criteria postLookupCriteria, Map<String, String> projectionFields, Sort sort) {
        List<AggregationOperation> operations = new ArrayList<>();

        if (preLookupCriteria != null && !preLookupCriteria.getCriteriaObject().isEmpty()) {
            operations.add(match(preLookupCriteria));
        }

        operations.add(lookup("artists", "artistId", "_id", "artist"));
        operations.add(unwind("artist"));

        if (postLookupCriteria != null && !postLookupCriteria.getCriteriaObject().isEmpty()) {
            operations.add(match(postLookupCriteria));
        }

        ProjectionOperation projection = project();
        for (Map.Entry<String, String> entry : projectionFields.entrySet()) {
            projection = projection.and(entry.getKey()).as(entry.getValue());
        }
        operations.add(projection);

        if (sort != null) {
            operations.add(sort(sort));
        }

        return newAggregation(operations);
    }

    private Criteria getTeamIdCriteria(String teamId, Boolean includeOnlyTeamSongs) {
        if (includeOnlyTeamSongs != null && includeOnlyTeamSongs) {
            return Criteria.where(Song.Fields.teamId).is(teamId);
        } else {
            return new Criteria().orOperator(
                    Criteria.where(Song.Fields.teamId).is(null),
                    Criteria.where(Song.Fields.teamId).is(teamId)
            );
        }
    }
}