package org.onstage.song.client;

import lombok.Builder;

import java.util.List;

@Builder
public record GetAllSongsResponse(List<SongOverview> songs, boolean hasMore){
}
