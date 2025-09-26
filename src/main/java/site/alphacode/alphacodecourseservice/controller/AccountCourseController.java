package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.AccountCourseDto;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountCourse;
import site.alphacode.alphacodecourseservice.service.AccountCourseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account-courses")
@RequiredArgsConstructor
@Tag(name = "Account Courses")
public class AccountCourseController {
    private final AccountCourseService accountCourseService;

    @GetMapping("/{id}")
    @Operation(summary = "Get account course by id")
    public AccountCourseDto getAccountCourseById(@PathVariable UUID id) {
        return accountCourseService.getAccountCourseById(id);
    }

    @GetMapping
    @Operation(summary = "Get list of account courses by account id")
    public List<AccountCourseDto> getAccountCoursesByAccountId(@RequestParam UUID accountId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return accountCourseService.getAccountCoursesByAccountId(accountId, page, size);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new action")
    public AccountCourseDto createAccountCourse(@RequestBody CreateAccountCourse createAccountCourse) {
        return accountCourseService.create(createAccountCourse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account course")
    public AccountCourseDto updateAccountCourse(@PathVariable UUID id, @RequestBody AccountCourseDto accountCourseDto) {
        return accountCourseService.update(id, accountCourseDto);
    }



}
