package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.response.AccountCourseDto;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountCourse;

import java.util.List;
import java.util.UUID;

public interface AccountCourseService {
    AccountCourseDto getAccountCourseById(UUID accountCourseId);
    AccountCourseDto create(CreateAccountCourse createAccountCourse);
    AccountCourseDto update(UUID id, AccountCourseDto accountCourseDto);
    void delete(UUID id);
    List<AccountCourseDto> getAccountCoursesByAccountId(UUID accountId, int page, int size);
    AccountCourseDto patchUpdate(UUID id, AccountCourseDto accountCourseDto);
}
