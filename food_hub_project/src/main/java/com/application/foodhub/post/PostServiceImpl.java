package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.foodhub.comment.CommentService;
import com.application.foodhub.postLike.PostLikeService;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostDAO postDAO;
	
	@Autowired
	private PostLikeService postLikeService;
	
	@Autowired
	private CommentService commentService;

	@Override
	public List<Map<String, Object>> getPostList(int pageSize, int offset) {
		return postDAO.getPostList(pageSize, offset);
	}
	
	@Override
	public List<Map<String, Object>> getBestPostList(int pageSize, int offset) {
		return postDAO.getBestPostList(pageSize, offset);
	}

	@Override
	public long getAllPostCnt() {
		return postDAO.getAllPostCnt();
	}

	@Override
	public long getPostCnt() {
		long count = postDAO.getPostCnt();
		return Math.max(count, 0); // 0 미만이 되지 않도록 보장
	}

	@Override
    public Map<String, Object> getPostDetail(long postId, boolean isIncreaseReadCnt) {
        // ✅ 삭제된 게시글인지 확인
        Map<String, Object> postMap = postDAO.getPostDetail(postId);
        if (postMap == null || "DELETED".equals(postMap.get("status"))) {
            return null; // 삭제된 게시글이면 null 반환
        }

        // ✅ 조회수 증가 처리
        if (isIncreaseReadCnt) {
            postDAO.updateReadCnt(postId);
        }

        // ✅ 추천수 추가
        int likeCount = postLikeService.getPostLikeCount(postId);
        postMap.put("likeCount", likeCount);

        // ✅ 프로필 이미지가 없는 경우 기본 이미지 설정
        if (!postMap.containsKey("profileUUID") || postMap.get("profileUUID") == null || postMap.get("profileUUID").toString().isEmpty()) {
            postMap.put("profileUUID", "default-profile.png"); // 기본 프로필 이미지
        }

        return postMap;
    }


	@Override
	public Long createPost(PostDTO postDTO) {
		postDAO.createPost(postDTO);
		return postDTO.getPostId();
	}

	@Override
	public List<Map<String, Object>> myPostList(String userId) {
		return postDAO.myPostList(userId);
	}

	 @Override
	    public void markPostAsDeleted(long postId) {
	        postDAO.markPostAsDeleted(postId); // 게시글 상태 변경
	        commentService.markCommentsAsDeletedByPostId(postId); // 해당 게시글의 댓글들 숨기기
	    }

	@Override
	public void updatePost(PostDTO postDTO) {
		postDAO.updatePost(postDTO);
	}

	@Override
	public Long getPrevPostId(long postId, long categoryId) {
		return postDAO.getPrevPostId(postId, categoryId);
	}

	@Override
	public Long getNextPostId(long postId, long categoryId) {
		return postDAO.getNextPostId(postId, categoryId);
	}

	@Override
	public List<Map<String, Object>> getPostListByCategory(Long categoryId, int pageSize, int offset) {
		return postDAO.getPostListByCategory(categoryId, pageSize, offset);
	}

	@Override
	public long getPostCntByCategory(Long categoryId) {
		long count = postDAO.getPostCntByCategory(categoryId);
		return Math.max(count, 0); // 0 미만이 되지 않도록 보장
	}

	@Override
	public String getCategoryName(Long categoryId) {
		return postDAO.getCategoryName(categoryId);
	}

	@Override
	public List<Map<String, Object>> getLatestPostsByCategoryId(long categoryId, int limit) {
		return postDAO.getLatestPostsByCategoryId(categoryId, limit);
	}
	

	@Override
	public List<Map<String, Object>> searchPostsByTitle(String keyword, int pageSize, int offset) {
	    return postDAO.searchPostsByTitle(keyword, pageSize, offset);
	}

	@Override
	public long countPostsByTitle(String keyword) {
	    return postDAO.countPostsByTitle(keyword);
	}

	@Override
	public List<Map<String, Object>> searchPostsByTitleAndContent(String keyword, int pageSize, int offset) {
	    return postDAO.searchPostsByTitleAndContent(keyword, pageSize, offset);
	}

	@Override
	public long countPostsByTitleAndContent(String keyword) {
	    return postDAO.countPostsByTitleAndContent(keyword);
	}
	
	@Override
	public String getCategoryNameById(Long categoryId) {
	    return postDAO.getCategoryNameById(categoryId);
	}
	
	@Override
	public long countPostsByCategoryTitle(Long categoryId, String keyword) {
	    return postDAO.countPostsByCategoryTitle(categoryId, keyword);
	}

	@Override
	public long countPostsByCategoryTitleAndContent(Long categoryId, String keyword) {
	    return postDAO.countPostsByCategoryTitleAndContent(categoryId, keyword);
	}

	@Override
	public List<Map<String, Object>> searchPostsByCategoryTitle(Long categoryId, String keyword, int pageSize, int offset) {
	    return postDAO.searchPostsByCategoryTitle(categoryId, keyword, pageSize, offset);
	}

	@Override
	public List<Map<String, Object>> searchPostsByCategoryTitleAndContent(Long categoryId, String keyword, int pageSize, int offset) {
	    return postDAO.searchPostsByCategoryTitleAndContent(categoryId, keyword, pageSize, offset);
	}


	@Override
	public long getBestPostCnt() {
		long count = postDAO.getBestPostCnt();
		return Math.max(count, 0); // 0 미만이 되지 않도록 보장
	}

	@Override
	public List<Map<String, Object>> searchBestPostsByTitle(String keyword, int pageSize, int offset) {
		return postDAO.searchBestPostsByTitle(keyword, pageSize, offset);
	}

	@Override
	public List<Map<String, Object>> searchBestPostsByTitleAndContent(String keyword, int pageSize, int offset) {
		return postDAO.searchBestPostsByTitleAndContent(keyword, pageSize, offset);
	}


	 @Override
	    public Map<String, Object> getPostById(Long postId) {
	        // MyBatis 매퍼를 통해 게시글 정보를 가져옴
	        Map<String, Object> postMap = postDAO.getPostDetail(postId);

	        // 게시글이 없거나, 상태가 'DELETED'라면 null 반환 (컨트롤러에서 404 페이지로 이동)
	        if (postMap == null || "DELETED".equals(postMap.get("status"))) {
	            return null;
	        }

	        return postMap;
	    }

	@Override
	public String getSubCateNameById(Long subCateId) {
		return postDAO.getSubCateNameById(subCateId);
	}

	@Override
	public long countPostsBySubCategoryTitle(Long subCateId, String keyword) {
		return postDAO.countPostsBySubCategoryTitle(subCateId , keyword);
	}

	@Override
	public List<Map<String, Object>> searchPostsBySubCategoryTitle(Long subCateId, String keyword, int pageSize,int offset) {
		return postDAO.searchPostsBySubCategoryTitle(subCateId , keyword , pageSize , offset);
	}

	@Override
	public long countPostsBySubCategoryTitleAndContent(Long subCateId, String keyword) {
		return postDAO.countPostsBySubCategoryTitleAndContent(subCateId , keyword);
	}

	@Override
	public List<Map<String, Object>> searchPostsBySubCategoryTitleAndContent(Long subCateId, String keyword,int pageSize, int offset) {
		return postDAO.searchPostsBySubCategoryTitleAndContent(subCateId , keyword , pageSize , offset);
	}

	@Override
	public long getPostCntBySubCategory(Long subCateId) {
		return postDAO.getPostCntBySubCategory(subCateId);
	}

	@Override
	public List<Map<String, Object>> getPostListBySubCategory(Long subCateId, int pageSize, int offset) {
		return postDAO.getPostListBySubCategory(subCateId, pageSize , offset);
	}



}
