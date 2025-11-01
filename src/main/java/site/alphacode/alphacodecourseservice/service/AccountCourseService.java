package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.response.AccountCourseDto;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountCourse;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;

import java.util.List;
import java.util.UUID;

public interface AccountCourseService {
    AccountCourseDto getAccountCourseById(UUID accountCourseId);
    AccountCourseDto create(CreateAccountCourse createAccountCourse);
    void delete(UUID id);
    PagedResult<AccountCourseDto> getAccountCoursesByAccountId(UUID accountId, int page, int size);
    List<AccountCourseDto> createFromBundle(UUID accountId, UUID bundleId);
    void createFromPayment(CreateAccountCourse createAccountCourse);
    AccountCourseDto getByAccountIdAndCourseId(UUID accountId, UUID courseId);
}
