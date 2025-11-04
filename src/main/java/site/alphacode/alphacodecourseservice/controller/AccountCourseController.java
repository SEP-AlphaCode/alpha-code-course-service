package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.response.AccountCourseDto;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountCourse;
import site.alphacode.alphacodecourseservice.dto.response.AvailableCourse;
import site.alphacode.alphacodecourseservice.dto.response.EnrolledCourses;
import site.alphacode.alphacodecourseservice.dto.response.LearningDashboard;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.service.AccountCourseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account-courses")
@RequiredArgsConstructor
@Tag(name = "Account Courses", description = "Account Course management APIs")
public class AccountCourseController {
    private final AccountCourseService accountCourseService;

    @GetMapping("/by-account/{accountId}")
    @Operation(summary = "Get list of account courses by account id")
    public PagedResult<AccountCourseDto> getAccountCoursesByAccountId(@PathVariable UUID accountId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return accountCourseService.getAccountCoursesByAccountId(accountId, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account course by id")
    public AccountCourseDto getAccountCourseById(@PathVariable UUID id) {
        return accountCourseService.getAccountCourseById(id);
    }

    @GetMapping("/enrolled-courses")
    @Operation(summary = "Get enrolled courses by account id")
    public List<EnrolledCourses> getEnrolledCoursesByAccountId(@RequestParam UUID accountId, @RequestParam Integer size) {
        return accountCourseService.getEnrolledCourses(accountId, size);
    }

    @GetMapping("/available-courses")
    @Operation(summary = "Get available courses by account id that not enrolled yet")
    public List<AvailableCourse> getAvailableCoursesByAccountId(@RequestParam UUID accountId, @RequestParam Integer size) {
        return accountCourseService.getAvailableCourses(accountId, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new account course")
    @PreAuthorize("hasAuthority('ROLE_Parent')")
    public AccountCourseDto createAccountCourse(@RequestBody CreateAccountCourse createAccountCourse) {
        return accountCourseService.create(createAccountCourse);
    }

    @PostMapping(value = "/from-bundle", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new account courses from bundle")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public List<AccountCourseDto> createAccountCoursesFromBundle(@RequestParam UUID accountId, @RequestParam UUID bundleId) {
        return accountCourseService.createFromBundle(accountId, bundleId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete account course")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public void deleteAccountCourse(@PathVariable UUID id) {
        accountCourseService.delete(id);
    }

    @GetMapping("get-by-account-and-course")
    @Operation(summary = "Get account course by account id and course id")
    public AccountCourseDto getByAccountAndCourse(@RequestParam UUID accountId, @RequestParam UUID courseId) {
        return accountCourseService.getByAccountIdAndCourseId(accountId, courseId);
    }

    @GetMapping("/learning-dashboard/{accountId}")
    @Operation(summary = "Get learning dashboard for an account", 
               description = "Returns learning statistics including total courses, completed lessons, learning hours this week (auto-tracked when completing lessons), recent activities, enrolled courses, and available courses")
    @PreAuthorize("hasAnyAuthority('ROLE_Parent', 'ROLE_Children')")
    public LearningDashboard getLearningDashboard(@PathVariable UUID accountId) {
        return accountCourseService.getLearningDashboard(accountId);
    }
}
