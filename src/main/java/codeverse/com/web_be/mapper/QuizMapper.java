package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.QuizRequest.QuizAnswerCreateRequest;
import codeverse.com.web_be.dto.request.QuizRequest.QuizQuestionCreateRequest;
import codeverse.com.web_be.entity.QuizAnswer;
import codeverse.com.web_be.entity.QuizQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    QuizQuestion quizQuestionCreateRequestToQuizQuestion(QuizQuestionCreateRequest request);

    @Mapping(target = "question", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "correct", target = "isCorrect")
    QuizAnswer quizAnswerCreateRequestToQuizAnswer(QuizAnswerCreateRequest request);

}
