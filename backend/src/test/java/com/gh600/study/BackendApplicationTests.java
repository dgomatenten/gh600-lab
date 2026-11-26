package com.gh600.study;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gh600.study.question.QuestionRepository;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	void contextLoadsAndSeedsSampleQuestions() {
		assertThat(questionRepository.count()).isGreaterThanOrEqualTo(3);
	}

}
