package com.application.foodhub.comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.application.foodhub.commentReport.CommentReportDTO;
import com.application.foodhub.commentReport.CommentReportService;
import com.application.foodhub.user.UserService;

@Controller
@RequestMapping("/comments")
public class CommentController {

	@Autowired
	private UserService userService;

	@Autowired
	private CommentService commentService;
	
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
	public CommentDTO insertComment(@RequestParam("postId") Long postId, @RequestParam("userId") String userId,
			@RequestParam(value = "parentId", required = false) Long parentId,
			@RequestParam("content") String content) {
		Map<String, Object> params = new HashMap<>();
		params.put("postId", postId);
		params.put("userId", userId);
		params.put("parentId", parentId);
		params.put("content", content);

		commentService.insertComment(params);

		// ✅ 방금 등록한 댓글 정보 다시 가져오기 (프로필 이미지 포함)
		CommentDTO newComment = commentService.getLastInsertedComment(postId, userId);
		return newComment;
	}

	@PostMapping("/update")
	@ResponseBody
	public String updateComment(@RequestParam("commentId") Long commentId, @RequestParam("content") String content,
			@RequestParam("userId") String userId // 현재 로그인한 사용자 ID 추가
	) {
		CommentDTO comment = commentService.getCommentById(commentId);

		if (comment == null) {
			return "존재하지 않는 댓글입니다.";
		}

		if (!comment.getUserId().equals(userId)) {
			return "본인이 작성한 댓글만 수정할 수 있습니다.";
		}

		Map<String, Object> params = new HashMap<>();
		params.put("commentId", commentId);
		params.put("content", content);

		commentService.updateComment(params);
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
    public Map<String, Object> toggleCommentLike(@RequestParam("commentId") Long commentId,
                                                 @RequestParam("userId") String userId) {
        boolean liked = commentService.toggleCommentLike(commentId, userId);
        int likeCount = commentService.getCommentLikeCount(commentId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("liked", liked);
        response.put("likeCount", likeCount);
        return response;
    }
	
	@PostMapping("/report")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> report(@RequestBody CommentReportDTO commentReportDTO) {
		long commentId = commentReportDTO.getCommentId();
		String userId = commentReportDTO.getUserId();
		String content = commentReportDTO.getContent();
		
		boolean reportSuccess = commentReportService.reportComment(commentId, userId, content);
		
		Map<String, Object> response = new HashMap<>();
		
		if (!reportSuccess) {
			response.put("success", false);
			response.put("message", "이미 신고한 댓글입니다.");
			response.put("redirectUrl", "/foodhub/post/postDetail?postId=" + commentReportDTO.getCommentId());
			return ResponseEntity.ok(response); // 🚨 클라이언트가 알 수 있도록 JSON 반환
		} else {
			// 신고 성공 시
			response.put("success", true);
			response.put("message", "댓글이 신고되었습니다.");
			response.put("redirectUrl", "/foodhub/post/postDetail?postId=" + commentReportDTO.getCommentId());

			return ResponseEntity.ok(response);
		}
	}
	

}
