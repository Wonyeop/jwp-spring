package next.controller.qna;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import core.mvc.ModelAndView;
import next.CannotDeleteException;
import next.controller.UserSessionUtils;
import next.controller.user.CreateUserController;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import next.service.QnaService;

@Controller
public class QNAController {
	private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
	private QuestionDao questionDao = QuestionDao.getInstance();
	private AnswerDao answerDao = AnswerDao.getInstance();
	private QnaService qnaService = QnaService.getInstance();
	
	@RequestMapping(value = "/qna/show", method = RequestMethod.GET)
	public String showQNA(@RequestParam(value="questionId") long questionId, Model model) throws Exception {
		
        Question question = questionDao.findById(questionId);
        List<Answer> answers = answerDao.findAllByQuestionId(questionId);
        
        model.addAttribute("question", question);
        model.addAttribute("answers", answers);
        return "qna/show";
	}
	
	@RequestMapping(value = "/qna/form", method = RequestMethod.GET)
	public String formQNA(HttpSession session) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		return "qna/form";
	}
	
	@RequestMapping(value = "/qna/create", method = RequestMethod.POST)
	public String execute(HttpSession session, Question question) throws Exception {
    	if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
    	User user = UserSessionUtils.getUserFromSession(session);
    	question.setWriter(user.getUserId());
    	question.setQuestionId(0);
    	question.setCreatedDate(new Date());
    	question.setCountOfComment(0);
    	questionDao.insert(question);
		return "redirect:/";
	}
	
	@RequestMapping(value = "/qna/updateForm", method = RequestMethod.GET)
	public String formQNAUpddate(HttpSession session, @RequestParam(value="questionId") long questionId, Model model) throws Exception {
    	if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		Question question = questionDao.findById(questionId);
		if (!question.isSameUser(UserSessionUtils.getUserFromSession(session))) {
			throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
		}
		model.addAttribute("question", question);
		return "qna/update";
	}
	
	@RequestMapping(value = "/qna/update", method = RequestMethod.PUT)
	public String updateQNA(HttpSession session, Question newQuestion) throws Exception {
    	if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		Question question = questionDao.findById(newQuestion.getQuestionId());
		if (!question.isSameUser(UserSessionUtils.getUserFromSession(session))) {
			throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
		}
		newQuestion.setWriter(question.getWriter());
		newQuestion.setQuestionId(0);
		newQuestion.setCreatedDate(new Date());
		newQuestion.setCountOfComment(0);
		question.update(newQuestion);
		questionDao.update(question);
		return "redirect:/";
	}
	
	@RequestMapping(value = "/qna/delete", method = RequestMethod.DELETE)
	public String deleteQuestion(HttpSession session, @RequestParam(value="questionId") long questionId, Model model) throws Exception {
    	if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		try {
			qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
			return "redirect:/";
		} catch (CannotDeleteException e) {
			model.addAttribute("question", qnaService.findById(questionId));
			model.addAttribute("answers", qnaService.findAllByQuestionId(questionId));
			model.addAttribute("errorMessage", e.getMessage());
			return "show";
		}
	}
	
	
}
