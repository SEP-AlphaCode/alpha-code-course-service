package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.LearnLesson;
import site.alphacode.alphacodecourseservice.dto.response.LessonDto;
import site.alphacode.alphacodecourseservice.dto.response.LessonWithSolution;
import site.alphacode.alphacodecourseservice.entity.Lesson;

public class LessonMapper {

    public static LearnLesson toLearnLesson(Lesson lesson) {
        if(lesson == null) return null;

        LearnLesson learnLesson = new LearnLesson();
        learnLesson.setId(lesson.getId());
        learnLesson.setTitle(lesson.getTitle());
        learnLesson.setContent(lesson.getContent());
        learnLesson.setContentType(lesson.getContentType());
        learnLesson.setDuration(lesson.getDuration());
        learnLesson.setRequireRobot(lesson.getRequireRobot());
        return learnLesson;
    }

    public static LessonDto toDto(Lesson lesson) {
        if(lesson == null) return null;
        LessonDto lessonDto = new LessonDto();

        lessonDto.setId(lesson.getId());
        lessonDto.setTitle(lesson.getTitle());
        lessonDto.setContentType(lesson.getContentType());
        lessonDto.setContent(lesson.getContent());
        lessonDto.setDuration(lesson.getDuration());
        lessonDto.setRequireRobot(lesson.getRequireRobot());
        lessonDto.setCourseId(lesson.getCourseId());
        lessonDto.setOrderNumber(lesson.getOrderNumber());
        lessonDto.setStatus(lesson.getStatus());
        lessonDto.setCreatedDate(lesson.getCreatedDate());
        lessonDto.setLastUpdated(lesson.getLastUpdated());

        return lessonDto;
    }

    public static LessonWithSolution toLessonWithSolution(Lesson lesson) {
        if(lesson == null) return null;
        LessonWithSolution lessonWithSolution = new LessonWithSolution();

        lessonWithSolution.setId(lesson.getId());
        lessonWithSolution.setTitle(lesson.getTitle());
        lessonWithSolution.setContentType(lesson.getContentType());
        lessonWithSolution.setContent(lesson.getContent());
        lessonWithSolution.setDuration(lesson.getDuration());
        lessonWithSolution.setRequireRobot(lesson.getRequireRobot());
        lessonWithSolution.setCourseId(lesson.getCourseId());
        lessonWithSolution.setOrderNumber(lesson.getOrderNumber());
        lessonWithSolution.setStatus(lesson.getStatus());
        lessonWithSolution.setCreatedDate(lesson.getCreatedDate());
        lessonWithSolution.setLastUpdated(lesson.getLastUpdated());
        lessonWithSolution.setSolution(lesson.getSolution());

        return lessonWithSolution;
    }
}
