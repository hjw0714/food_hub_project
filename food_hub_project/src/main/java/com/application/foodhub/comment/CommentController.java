package com.application.foodhub.comment;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.application.foodhub.commentLike.CommentLikeDTO;
import com.application.foodhub.commentLike.CommentLikeService;
import com.application.foodhub.commentReport.CommentReportDTO;
import com.application.foodhub.commentReport.CommentReportService;
import com.application.foodhub.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/comments")
public class CommentController {

	@Autowired
	private UserService userService;

	@Autowired
	private CommentService commentService;
	
	@Autowired
	private CommentLikeService commentLikeService;
	
	@Autowired
	private CommentReportService commentReportService;

	// 특정 게시글의 모든 댓글 조회 (원댓글 + 대댓글 포함)
	@GetMapping("/post/{postId}")
	@ResponseBody
	public List<CommentDTO> getCommentsByPostId(@PathVariable("postId") Long postId) { // ⬅️ "postId" 명시
		return commentService.getCommentsByPostId(postId);
	}

	@GetMapping("/post/{postId}/parents")
	@ResponseBody
	public List<CommentDTO> getParentComments(@PathVariable("postId") Long postId ,  @RequestParam(value = "userId", required = false) String userId , @RequestParam(value = "sortOrder", required = false, defaultValue = "oldest") String sortOrder) {  
	    return commentService.getParentComments(postId, userId, sortOrder);
	}

	// 특정 댓글의 대댓글 조회
	@GetMapping("/parent/{parentId}")
	@ResponseBody
	public List<CommentDTO> getChildComments(@PathVariable("parentId") Long parentId , @RequestParam(value = "userId", required = false) String userId) {
		return commentService.getChildComments(parentId , userId);
	}

	// 댓글 추가 (원댓글 또는 대댓글)
	@PostMapping("/add")
	@ResponseBody
	public CommentDTO insertComment(@ModelAttribute CommentDTO commentDTO) {

		commentService.insertComment(commentDTO);

		// ✅ 방금 등록한 댓글 정보 다시 가져오기 (프로필 이미지 포함)
		CommentDTO newComment = commentService.getLastInsertedComment(commentDTO.getPostId() , commentDTO.getUserId());
		return newComment;
	}

	@PostMapping("/update")
	@ResponseBody
	public String updateComment(@RequestParam("commentId") long commentId ,@ModelAttribute CommentDTO commentDTO , HttpSession session) {
		
		String userId = (String) session.getAttribute("userId");
		CommentDTO comment = commentService.getCommentById(commentId);

		if (comment == null) {
			return "존재하지 않는 댓글입니다.";
		}

		if (!comment.getUserId().equals(userId)) {
			return "본인이 작성한 댓글만 수정할 수 있습니다.";
		}


		commentService.updateComment(commentDTO);
		return "댓글이 성공적으로 수정되었습니다.";
	}

	// 댓글 삭제 (상태 변경)
	@PostMapping("/delete")
	@ResponseBody
	public String deleteComment(@RequestParam("commentId") Long commentId, @RequestParam("userId") String userId) {
		CommentDTO comment = commentService.getCommentById(commentId);

		// 존재하지 않는 댓글이면 삭제 실패
		if (comment == null) {
			return "존재하지 않는 댓글입니다.";
		}

		// 본인이 작성한 댓글이 아니면 삭제 불가
		if (!comment.getUserId().equals(userId)) {
			return "본인이 작성한 댓글만 삭제할 수 있습니다.";
		}

		// 댓글 삭제 처리
		commentService.deleteComment(commentId);

		return "댓글이 삭제 처리되었습니다.";
	}
	
	@PostMapping("/like")
    @ResponseBody
    public CommentLikeDTO toggleCommentLike(@RequestParam("commentId") Long commentId,
                                                 @RequestParam("userId") String userId) {
        CommentLikeDTO liked = commentLikeService.toggleCommentLike(commentId, userId);
        int likeCount = commentLikeService.getCommentLikeCount(commentId);

        CommentLikeDTO response = new CommentLikeDTO();
        response.setCommentId(commentId);
        response.setUserId(userId);
        response.setLiked(true); 
        response.setLikeCount(likeCount); 
        response.setSuccess(true);
        
        return response;
    }
	
	@PostMapping("/report")
	@ResponseBody
	public CommentReportDTO report(@RequestBody CommentReportDTO commentReportDTO) {
	    return commentReportService.reportComment(commentReportDTO);
	}

	

}
