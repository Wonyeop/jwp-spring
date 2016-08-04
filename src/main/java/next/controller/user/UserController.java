package next.controller.user;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import next.controller.UserSessionUtils;
import next.dao.UserDao;
import next.model.User;

@Controller
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

	private UserDao userDao = UserDao.getInstance();
	
	@RequestMapping(value = "/users/create", method = RequestMethod.POST)
	public String createUser(User user){
		log.debug("User : {}", user);
		userDao.insert(user);
		return "redirect:/";
	}
	
	@RequestMapping(value = "/users/form", method = RequestMethod.GET)
	public String formCreate(){
		return "user/form";
	}
	
	@RequestMapping(value = "/users" , method = RequestMethod.GET)
	public String indexUser(HttpSession session, Model model) throws Exception{
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		model.addAttribute("users",userDao.findAll());
        return "/user/list";
	}
	
	@RequestMapping(value = "/users/loginForm" , method = RequestMethod.GET)
	public String formLogin() throws Exception{
        return "/user/login";
	}
	
	@RequestMapping(value = "/users/login" , method = RequestMethod.POST)
	public String login(HttpSession session, @RequestParam(value="userId") String userId, @RequestParam(value="password") String password) throws Exception {
		User user = userDao.findByUserId(userId);
	        
		if (user == null) {
			throw new NullPointerException("사용자를 찾을 수 없습니다.");
		}
	        
		if (user.matchPassword(password)) {
			session.setAttribute("user", user);
			return "redirect:/";
		} else {
			throw new IllegalStateException("비밀번호가 틀립니다.");
		}
	}
	
	@RequestMapping(value = "/users/logout" , method = RequestMethod.GET)
	public String logout(HttpSession session) throws Exception {
        session.removeAttribute("user");
        return "redirect:/qna/list";
    }
	
	@RequestMapping(value = "/users/profile" , method = RequestMethod.GET)
	public String showProfile(@RequestParam(value="userId") String userId, Model model) throws Exception {
        model.addAttribute("user", userDao.findByUserId(userId));
        return "user/profile";
    }
	
	@RequestMapping(value = "/users/updateForm" , method = RequestMethod.GET)
	public String formUpdate(@RequestParam(value="userId") String userId, HttpSession session, Model model) throws Exception {
    	User user = userDao.findByUserId(userId);

    	if (!UserSessionUtils.isSameUser(session, user)) {
        	throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }
    	model.addAttribute("user", user);
    	return "user/updateForm";
	}
	
	@RequestMapping(value = "/users/update" , method = RequestMethod.PUT)
	public String update(User updateUser, HttpSession session) throws Exception {
		User user = userDao.findByUserId(updateUser.getUserId());
		
        if (!UserSessionUtils.isSameUser(session, user)) {
        	throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }
        
        log.debug("Update User : {}", updateUser);
        user.update(updateUser);
        userDao.update(user);
        return "redirect:/";
	}
	
	
}
