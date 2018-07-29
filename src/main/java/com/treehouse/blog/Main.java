package com.treehouse.blog;

import static spark.Spark.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.treehouse.blog.model.*;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class Main {

	private static Map<String, Object> model;
	private static final String FLASH_MESSAGE_KEY = "flash_message";
	private static final String PATH = "logIn_path";

	public static void main(String[] args) {

		staticFileLocation("/public");
		BlogDao dao = new BlogDaoImplementation();

		before("/addBlog", (req, res) -> {
			if (req.cookie("password") == null || !req.cookie("password").equals("admin")) {
				String path = req.raw().getPathInfo();
				setForwardPath(req, path);
				res.redirect("/logIn");
				halt();
			}

		});

		before("/editBlog/:slug", (req, res) -> {
			if (req.cookie("password") == null || !req.cookie("password").equals("admin")) {
				String path = req.raw().getPathInfo();
				setForwardPath(req, path);
				res.redirect("/logIn");
				halt();
			}
		});

		before((req, res) -> {

			model = new HashMap<>();
			model.put("flashmessage", captureFlashmessage(req));
		});

		get("/", (req, res) -> {
			Map<String, Object> model = new HashMap<>();
			model.put("blogEntries", dao.getEntries());

			return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
		});

		get("/editBlog/:slug", (req, res) -> {
			model.put("entry", dao.findbySlugh(req.params("slug")));
			return new HandlebarsTemplateEngine().render(new ModelAndView(model, "edit-blog.hbs"));
		});

		post("/editBlog/:slug", (req, res) -> {
			if (req.queryParams("blogTitle").length() > 0 && req.queryParams("blogBody") != null) {
				String title = req.queryParams("blogTitle");
				String body = req.queryParams("blogBody");

				Entry entry = dao.findbySlugh(req.params("slug"));

				if (!title.equals(entry.getTitle())) {
					entry.setTitle(title);
				}

				if (!body.equals(entry.getBody())) {
					entry.setBody(body);
				}

				res.redirect("/entry/" + req.params("slug"));
			} else {
				setFlashMessage(req, "Blog Title and Blog Body are required!");
				res.redirect("/addBlog");
			}
			return null;
		});

		get("/entry/:slug", (req, res) -> {
			model.put("entry", dao.findbySlugh(req.params("slug")));
			return new HandlebarsTemplateEngine().render(new ModelAndView(model, "entry-details.hbs"));
		});

		exception(NotFoundException.class, (exc, req, res) -> {
			res.status(404);
			System.out.println("THIS IS MY EXCEPTION" + NotFoundException.class);
			HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
			String html = engine.render(new ModelAndView(null, "not-found.hbs"));
			res.body(html);
		});

		get("/addBlog", (req, res) -> {
			return new HandlebarsTemplateEngine().render(new ModelAndView(model, "add-blog.hbs"));
		});

		get("/logIn", (req, res) -> {
			String patha = req.attribute("path");
			System.out.println("forwarded path attribute: " + patha);
			return new HandlebarsTemplateEngine().render(new ModelAndView(model, "log-in.hbs"));
		});

		post("/addBlog", (req, res) -> {
			if (req.queryParams("blogTitle").length() > 0 && req.queryParams("blogBody") != null) {
				Entry newBlog = new Entry(req.queryParams("blogTitle"), req.queryParams("blogBody"),
						LocalDateTime.now());
				dao.addEntry(newBlog);
				res.redirect("/");
			} else {
				setFlashMessage(req, "Blog Title and Blog Body are required!");
				res.redirect("/addBlog");
			}
			return null;
		});

		post("/logIn", (req, res) -> {
			if (req.queryParams("password").isEmpty()) {
				setFlashMessage(req, "Password required");
				res.redirect("/logIn");
			}

			else if (!req.queryParams("password").equals("admin")) {
				setFlashMessage(req, "Invalid Password!");
				res.redirect("/logIn");
			}

			else {
				res.cookie("password", req.queryParams("password"));
				String redirect = invokeForwardPath(req);
				res.redirect(redirect);
			}
			return null;
		});

		post("/addComment/:slug", (req, res) -> {
			String slug = req.params("slug");
			if (req.queryParams("name").length() > 0 && req.queryParams("comment").length() > 0) {
				Comment newComment = new Comment(req.queryParams("name"), req.queryParams("comment"));
				Entry entry = dao.findbySlugh(req.params("slug"));
				dao.addCommentBySlugh(entry, newComment);
				res.redirect("/entry/" + slug);
			} else {
				setFlashMessage(req, "Name and Comment are required when adding a comment");
				res.redirect("/entry/" + slug);
			}
			return null;
		});

		notFound((req, res) -> {
			res.type("application/json");
			return "{\"message\":\"Custom 404\"}";
		});
	}

	private static void setFlashMessage(Request req, String message) {
		req.session().attribute(FLASH_MESSAGE_KEY, message);

	}

	private static String getFlashMessage(Request req) {
		if (req.session(false) == null) {
			return null;
		}

		if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
			return null;
		}
		return (String) req.session().attribute(FLASH_MESSAGE_KEY);
	}

	private static String captureFlashmessage(Request req) {
		String message = getFlashMessage(req);
		if (message != null) {
			req.session().removeAttribute(FLASH_MESSAGE_KEY);
		}
		return message;
	}

	private static void setForwardPath(Request req, String message) {
		req.session().attribute(PATH, message);

	}

	private static String getForwardPath(Request req) {
		if (req.session(false) == null) {
			return null;
		}
		if (!req.session().attributes().contains(PATH)) {
			return null;
		}
		return (String) req.session().attribute(PATH);
	}

	private static String invokeForwardPath(Request req) {
		String message = getForwardPath(req);
		if (message != null) {
			req.session().removeAttribute(PATH);
		}
		return message;
	}
}
