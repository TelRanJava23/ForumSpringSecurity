package telran.forum.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.forum.dao.ForumRepository;
import telran.forum.dao.UserAccountRepository;
import telran.forum.domain.Comment;
import telran.forum.domain.Post;
import telran.forum.domain.UserAccount;
import telran.forum.dto.DatePeriodDto;
import telran.forum.dto.NewCommentDto;
import telran.forum.dto.NewPostDto;
import telran.forum.dto.PostUpdateDto;

@Service
public class ForumServiceImpl implements ForumService {

	@Autowired
	ForumRepository repository;
	
	@Autowired
	UserAccountRepository accountRepository;
	
	@Override
	public Post addNewPost(NewPostDto newPost, String author) {
		Post post = convertToPost(newPost, author);
		repository.save(post);
		return post;
	}

	private Post convertToPost(NewPostDto newPost, String author) {
		return new Post(newPost.getTitle(), newPost.getContent(), author, newPost.getTags());
	}

	@Override
	public Post getPost(String id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public Post removePost(String id, String login) {
		Post post = repository.findById(id).orElse(null);
		if (post != null) {
			UserAccount account = accountRepository.findById(login).get();
			Set<String> roles = account.getRoles();
			if (roles.contains("ROLE_MODERATOR") ||
					login.equals(post.getAuthor())) {
				repository.delete(post);
			}
		}
		return post;
	}

	@Override
	public Post updatePost(PostUpdateDto updatePost, String login) {
		Post post = repository.findById(updatePost.getId()).orElse(null);
		if (post != null) {
			UserAccount account = accountRepository.findById(login).get();
			Set<String> roles = account.getRoles();
			if (roles.contains("ROLE_ADMIN") ||
					login.equals(post.getAuthor())) {
				post.setContent(updatePost.getContent());
				repository.save(post);
			}
		}
		return post;
	}

	@Override
	public boolean addLike(String id) {
		Post post = repository.findById(id).orElse(null);
		if (post != null) {
			post.addLike();
			repository.save(post);
			return true;
		}
		return false;
	}

	@Override
	public Post addComment(String id, NewCommentDto newComment) {
		Post post = repository.findById(id).orElse(null);
		if (post != null) {
			Comment comment = new Comment(newComment.getUser(), newComment.getMessage());
			post.addComment(comment);
			repository.save(post);
		}
		return post;
	}

	@Override
	public Iterable<Post> findByTags(List<String> tags) {
		return repository.findByTagsIn(tags);
	}

	@Override
	public Iterable<Post> findByAuthor(String author) {
		return repository.findByAuthor(author);
	}

	@Override
	public Iterable<Post> findByDate(DatePeriodDto period) {
		return repository.findByDateCreatedBetween(LocalDate.parse(period.getFrom()),
				LocalDate.parse(period.getTo()));
	}
}
