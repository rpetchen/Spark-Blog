package com.treehouse.blog.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import com.github.slugify.Slugify;

public class Entry {
	
	private String title;
	private String body;
	private List<Comment> comments;
	private String createdDate;
	private String slug;
	
	public Entry(String title, String body, LocalDateTime date) {
		DateTimeFormatter fmt = DateTimeFormatter
		        .ofLocalizedDateTime(FormatStyle.MEDIUM);
		this.title = title;
		this.body = body;
		Slugify slugify = new Slugify();
		slug = slugify.slugify(title);
		this.createdDate = fmt.format(date);
		this.comments = new ArrayList<Comment>();
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}


	public List<Comment> getComments() {
		return comments;
	}


	public void setComments(Comment comment) {
		comments.add(comment);
	}


	public String getDate() {
	return createdDate;
	}


	@Override
	public String toString() {
		return "Entry [title=" + title + ", body=" + body + ", comments=" + comments + ", createdDate=" + createdDate
				+ ", slug=" + slug + "]";
	}


	public String getSlug() {
		return slug;
	}


	public boolean addComment(Comment comment) {
		return comments.add(comment);
	}
	
	
}
