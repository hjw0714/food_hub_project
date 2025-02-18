package com.application.foodhub.commentLike;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentLikeServiceTest {

	@Mock
	private CommentLikeDAO commentLikeDAO;
	
	@InjectMocks
	private CommentLikeServiceImpl commentLikeServiceImpl;
	
	private CommentLikeDTO testLike;
	
	@BeforeEach
	void beforeSetUpData() {
		
		
	}
	
}
