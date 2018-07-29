package com.treehouse.blog.model;

import java.util.List;

public interface BlogDao {

	List<Entry> getEntries();
	
	Boolean addEntry(Entry entry);
	
	Boolean deleteEntry();
	
	Entry findbySlugh(String slug);
	
	 Boolean addCommentBySlugh(Entry entry, Comment comment);
}
