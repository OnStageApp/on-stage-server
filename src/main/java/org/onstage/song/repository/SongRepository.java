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

        Map<String, String> projectionFields = getProjectionFieldsForSongDTO();

        Aggregation aggregation = buildAggregation(criteria, projectionFields, null);

        return mongoTemplate.aggregate(aggregation, Song.class, SongDTO.class).getUniqueMappedResult();
    }

    public Optional<SongOverview> findOverviewById(String id) {
        Criteria criteria = Criteria.where(Song.Fields.id).is(id);

        Map<String, String> projectionFields = getProjectionFieldsForSongOverview();

        Aggregation aggregation = buildAggregation(criteria, projectionFields, null);

        SongOverview result = mongoTemplate.aggregate(aggregation, Song.class, SongOverview.class).getUniqueMappedResult();
        return Optional.ofNullable(result);
    }

    public List<SongOverview> getAll(SongFilter songFilter, String teamId) {
        Criteria criteria = new Criteria();

        List<Criteria> preLookupCriteriaList = new ArrayList<>();

        if (songFilter.search() != null && !songFilter.search().isEmpty()) {
            preLookupCriteriaList.add(Criteria.where(Song.Fields.title).regex(songFilter.search(), "i"));
        }

        if (songFilter.tempoRange() != null) {
            if(songFilter.tempoRange().min() != null) {
                preLookupCriteriaList.add(Criteria.where(Song.Fields.tempo).gte(songFilter.tempoRange().min()));
            }
            if(songFilter.tempoRange().max() != null) {
                preLookupCriteriaList.add(Criteria.where(Song.Fields.tempo).lte(songFilter.tempoRange().max()));
            }
        }

        Criteria teamCriteria = getTeamCriteria(teamId, songFilter.includeOnlyTeamSongs());
        if (teamCriteria != null) {
            preLookupCriteriaList.add(teamCriteria);
        }

        if (!preLookupCriteriaList.isEmpty()) {
            criteria.andOperator(preLookupCriteriaList.toArray(new Criteria[0]));
        }

        Criteria postLookupCriteria = null;
        if (songFilter.artistId() != null && !songFilter.artistId().isEmpty()) {
            postLookupCriteria = Criteria.where("artist._id").is(songFilter.artistId());
        }

        Map<String, String> projectionFields = getProjectionFieldsForSongOverview();

        Sort sort = Sort.by(Sort.Order.asc(Song.Fields.title), Sort.Order.asc("artist.name"));

        Aggregation aggregation = buildAggregation(criteria, projectionFields, sort, postLookupCriteria);

        return mongoTemplate.aggregate(aggregation, Song.class, SongOverview.class).getMappedResults();
    }

    public Song save(Song song) {
        return songRepo.save(song);
    }

    private Aggregation buildAggregation(Criteria criteria, Map<String, String> projectionFields, Sort sort) {
        return buildAggregation(criteria, projectionFields, sort, null);
    }

    private Aggregation buildAggregation(Criteria criteria, Map<String, String> projectionFields, Sort sort, Criteria postLookupCriteria) {
        List<AggregationOperation> operations = new ArrayList<>();

        if (criteria != null && !criteria.getCriteriaObject().isEmpty()) {
            operations.add(match(criteria));
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

    private Criteria getTeamCriteria(String teamId, Boolean includeOnlyTeamSongs) {
        if (Boolean.TRUE.equals(includeOnlyTeamSongs)) {
            return Criteria.where(Song.Fields.teamId).is(teamId);
        } else {
            return new Criteria().orOperator(
                    Criteria.where(Song.Fields.teamId).is(null),
                    Criteria.where(Song.Fields.teamId).is(teamId)
            );
        }
    }

    private Map<String, String> getProjectionFieldsForSongDTO() {
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
        projectionFields.put("theme", "theme");
        projectionFields.put("genre", "genre");
        return projectionFields;
    }

    private Map<String, String> getProjectionFieldsForSongOverview() {
        Map<String, String> projectionFields = new LinkedHashMap<>();
        projectionFields.put("_id", "id");
        projectionFields.put("title", "title");
        projectionFields.put("originalKey", "key");
        projectionFields.put("tempo", "tempo");
        projectionFields.put("artist", "artist");
        projectionFields.put("teamId", "teamId");
        projectionFields.put("theme", "theme");
        projectionFields.put("genre", "genre");
        return projectionFields;
    }
}
