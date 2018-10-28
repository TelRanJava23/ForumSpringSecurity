package telran.forum.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import telran.forum.configuration.AccountConfiguration;
import telran.forum.dao.UserAccountRepository;
import telran.forum.domain.UserAccount;
import telran.forum.dto.UserProfileDto;
import telran.forum.dto.UserRegisterDto;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	UserAccountRepository userRepository;
	
	@Autowired
	AccountConfiguration accountConfiguration;
	
	@Autowired
	PasswordEncoder encoder;


	@Override
	public UserProfileDto addUser(UserRegisterDto userRegDto) {
		if (userRepository.existsById(userRegDto.getId())) {
			throw new UserExistException();
		}
		String hashPassword = encoder.encode(userRegDto.getPassword());
		UserAccount userAccount = UserAccount.builder()
				.id(userRegDto.getId())
				.password(hashPassword)
				.firstName(userRegDto.getFirstName())
				.lastName(userRegDto.getLastName())
				.role("ROLE_USER")
				.expDate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod()))
				.build();
		userRepository.save(userAccount);
		return new UserProfileDto(userRegDto.getId(),
				userRegDto.getFirstName(), userRegDto.getLastName());
	}

	@Override
	public UserProfileDto editUser(UserRegisterDto userRegDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfileDto removeUser(String id) {
		UserAccount userAccount = userRepository.findById(id).get();
		userRepository.delete(userAccount);
		return new UserProfileDto(userAccount.getId(), userAccount.getFirstName(), userAccount.getLastName());
	}

	@Override
	public Set<String> addRole(String id, String role) {
		UserAccount userAccount = userRepository.findById(id).orElse(null);
		if (userAccount == null) {
			return null;
		}
		Set<String> roles = userAccount.getRoles();
		roles.add("ROLE_"+role.toUpperCase());
		userRepository.save(userAccount);
		return roles;
	}

	@Override
	public Set<String> removeRole(String id, String role) {
		UserAccount userAccount = userRepository.findById(id).orElse(null);
		if (userAccount == null) {
			return null;
		}
		Set<String> roles = userAccount.getRoles();
		roles.remove("ROLE_"+role.toUpperCase());
		userRepository.save(userAccount);
		return roles;
	}

	@Override
	public void changePassword(String password) {
		// TODO Auto-generated method stub

	}

}
