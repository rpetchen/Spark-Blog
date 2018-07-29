package com.treehouse.blog.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.treehouse.blog.model.NotFoundException;

public class BlogDaoImplementation implements BlogDao {

	private List<Entry> blogEntries;
	
	
	public BlogDaoImplementation() {
		blogEntries = new ArrayList<Entry>();
		blogEntries.add(new Entry("NFL", "Cardinals are the best", LocalDateTime.now()));
		blogEntries.get(0).setComments(new Comment("Ryan", "This is a comment"));
		blogEntries.add(new Entry("Kaiju", "Godzilla is the best", LocalDateTime.now()));
		blogEntries.add(new Entry("Food", "Pizza is the best", LocalDateTime.now()));
	}

	@Override
	public List<Entry> getEntries() {
		return blogEntries;
	}

	@Override
	public Boolean addEntry(Entry entry) {
		return blogEntries.add(entry);
	}

	@Override
	public Boolean deleteEntry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entry findbySlugh(String slug) {
		return blogEntries.stream()
				.filter(entry -> entry.getSlug().equals(slug))
				.findFirst()
				.orElseThrow(NotFoundException::new);
	}
	
	@Override
	public Boolean addCommentBySlugh(Entry entry, Comment comment) {
		 return entry.addComment(comment);
	}

}
