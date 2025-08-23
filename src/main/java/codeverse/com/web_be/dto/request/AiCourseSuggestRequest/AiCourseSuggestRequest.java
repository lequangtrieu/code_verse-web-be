package codeverse.com.web_be.dto.request.AiCourseSuggestRequest;

import lombok.Data;
import java.util.List;

@Data
public class AiCourseSuggestRequest {

    public Base base;
    public Structure structure;
    public Exercises exercises;
    public Quiz quiz;
    public Scoring scoring;

    @Data
    public static class Base {
        public String courseTitle;
        public String courseDescription;
        public String language;
        public String levelId;
        public Long   categoryId;
        public Boolean isPaid;
        public Integer price;
    }

    @Data
    public static class Structure {
        public Integer moduleCount;
        public String  lessonStrategy;
        public Integer lessonsPerModule;
        public Integer timePerLesson;
    }

    @Data
    public static class Exercises {
        public Boolean include;
    }

    @Data
    public static class Quiz {
        public Boolean include;
        public String  style;
        public Integer questionsPerQuiz;
        public List<String> types;
    }

    @Data
    public static class Scoring {
        public Integer pointsPerLesson;
        public Integer pointsPerQuizQuestion;
    }
}
