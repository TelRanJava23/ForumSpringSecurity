package telran.forum.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import telran.forum.domain.Post;
import telran.forum.dto.DatePeriodDto;
import telran.forum.dto.NewCommentDto;
import telran.forum.dto.NewPostDto;
import telran.forum.dto.PostUpdateDto;
import telran.forum.service.ForumService;

@RestController
@RequestMapping("/forum")
public class ForumController {
	
	@Autowired
	ForumService service;
	
	@PostMapping("/post")
	public Post addPost(@RequestBody NewPostDto newPost, Principal principal) {
		return service.addNewPost(newPost, principal.getName());
	}
	
	@GetMapping("/post/{id}")
	public Post getPost(@PathVariable String id) {
		return service.getPost(id);
	}
	
	@DeleteMapping("/post/{id}")
	public Post removePost(@PathVariable String id, Principal principal) {
		return service.removePost(id, principal.getName());
	}
	
	@PutMapping("/post")
	public Post updatePost(@RequestBody PostUpdateDto postUpdateDto,
			Principal principal) {
		return service.updatePost(postUpdateDto, principal.getName());
	}
	
	@PutMapping("/post/{id}/like")
	public boolean addLike(@PathVariable String id) {
		return service.addLike(id);
	}
	
	@PutMapping("/post/{id}/comment")
	@PreAuthorize("#newCommentDto.user == authentication.name and hasRole('USER')")
	public Post addComment(@PathVariable String id,
			@RequestBody NewCommentDto newCommentDto) {
		return service.addComment(id, newCommentDto);
	}
	
	@PostMapping("/posts/tags")
	public Iterable<Post> getPostsByTags(@RequestBody List<String> tags){
		return service.findByTags(tags);
	}
	
	@GetMapping("/posts/author/{author}")
	public Iterable<Post> getPostsByAuthor(String author){
		return service.findByAuthor(author);
	}
	
	@PostMapping("/posts/period")
	public Iterable<Post> getPostsBetweenDate(@RequestBody DatePeriodDto periodDto){
		return service.findByDate(periodDto);
	}

}





