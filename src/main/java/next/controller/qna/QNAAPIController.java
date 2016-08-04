package next.controller.qna;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import core.jdbc.DataAccessException;
import core.mvc.ModelAndView;
import next.CannotDeleteException;
import next.controller.UserSessionUtils;
import next.controller.user.CreateUserController;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Question;
import next.model.Result;
import next.model.User;
import next.service.QnaService;

public class QNAAPIController {
	private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
	private QnaService qnaService = QnaService.getInstance();
	private QuestionDao questionDao = QuestionDao.getInstance();
	private AnswerDao answerDao = AnswerDao.getInstance();
	
	@RequestMapping(value = "/api/qna/deleteQuestion", method = RequestMethod.DELETE)
	public Result deleteQuestion(HttpSession session, @RequestParam(value="questionId") long questionId) throws Exception {
    	if (!UserSessionUtils.isLogined(session)) {
			return Result.fail("Login is required");
		}
		
		try {
			qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
			return Result.ok();
		} catch (CannotDeleteException e) {
			return Result.fail(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/api/qna/list", method = RequestMethod.GET)
	public List<Question> listQNA() throws Exception {
		return questionDao.findAll();
	}
	
	@RequestMapping(value = "/api/qna/addAnswer", method = RequestMethod.POST)
	public Result addAnswer(HttpSession session, Question question, Model model) throws Exception {
    	if (!UserSessionUtils.isLogined(session)) {
			return Result.fail("Login is required");
		}
    	
    	User user = UserSessionUtils.getUserFromSession(session);
		Answer answer = new Answer(user.getUserId(), 
				question.getContents(), 
				question.getQuestionId());
		log.debug("answer : {}", answer);
		
		Answer savedAnswer = answerDao.insert(answer);
		questionDao.updateCountOfAnswer(savedAnswer.getQuestionId());
		model.addAttribute("answer", savedAnswer);
		return Result.ok();
	}
	
	@RequestMapping(value = "/api/qna/deleteAnswer", method = RequestMethod.DELETE)
	public Result deleteAnswer(@RequestParam(value="answerId") long answerId) throws Exception {
		try {
			answerDao.delete(answerId);
			return Result.ok();
		} catch (DataAccessException e) {
			return Result.fail(e.getMessage());
		}
	}
}
