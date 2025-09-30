package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.entity.Submission;

public interface CheckerService {
    boolean autoCheck(Submission submission);
}
