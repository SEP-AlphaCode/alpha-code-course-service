package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.LearnLesson;
import site.alphacode.alphacodecourseservice.entity.Lesson;

public class LessonMapper {

    public static LearnLesson toLearnLesson(Lesson lesson) {
        if(lesson == null) return null;

        LearnLesson learnLesson = new LearnLesson();
        learnLesson.setId(lesson.getId());
        learnLesson.setTitle(lesson.getTitle());
        learnLesson.setContent(lesson.getContent());
        learnLesson.setContentType(lesson.getContentType());
        learnLesson.setContentUrl(lesson.getContentUrl());
        learnLesson.setDuration(lesson.getDuration());
        learnLesson.setRequireRobot(lesson.getRequireRobot());
        return learnLesson;
    }
}
