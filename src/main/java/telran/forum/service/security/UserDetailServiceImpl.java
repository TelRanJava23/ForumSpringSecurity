package telran.forum.service.security;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import telran.forum.dao.UserAccountRepository;
import telran.forum.domain.UserAccount;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
	@Autowired
	UserAccountRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserAccount userAccount = repository.findById(username)	.orElse(null);
		if (userAccount == null) {
			throw new UsernameNotFoundException(username);
		}
		String password = userAccount.getPassword();
		Set<String> setRoles = userAccount.getRoles();
		if (userAccount.getExpDate().isBefore(LocalDateTime.now())) {
			setRoles.clear();
			setRoles.add("ROLE_EXPDATE");
		}
		String[] roles = setToArray(setRoles);
		return new User(username, password,
				AuthorityUtils.createAuthorityList(roles));
	}

	private String[] setToArray(Set<String> roles) {
		//return roles.stream().toArray(String[]::new);
		return roles.toArray(new String[0]);
	}

}
