package com.jsg.courier.search;

import java.time.Instant;

public class JsonSearchResult implements SearchResult<String> {
	
	private String result;
	private String query;
	private int hits;
	private long timeRetrieved;
	
	public JsonSearchResult(String query, String result) {
		this.result = result;
		this.query = query;
		hits = 1;
		timeRetrieved = Instant.now().getEpochSecond();
	}

	@Override
	public void incrementHits() {
		hits++;
	}

	@Override
	public void update(String result) {
		this.result = result;
		this.timeRetrieved = Instant.now().getEpochSecond();
	}

	@Override
	public long checkAge() {
		return Instant.now().getEpochSecond() - timeRetrieved;
	}

	@Override
	public String get() {
		return result;
	}

	@Override
	public int getHits() {
		return hits;
	}

	@Override
	public int compareTo(SearchResult<String> o) {
		return hits - o.getHits();
	}

	@Override
	public String getQuery() {
		return query;
	}

	
}
