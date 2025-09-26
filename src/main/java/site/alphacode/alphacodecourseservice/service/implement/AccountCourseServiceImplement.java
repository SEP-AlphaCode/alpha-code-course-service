package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.alphacode.alphacodecourseservice.dto.response.AccountCourseDto;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountCourse;
import site.alphacode.alphacodecourseservice.entity.AccountCourse;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.mapper.AccountCourseMapper;
import site.alphacode.alphacodecourseservice.repository.AccountCourseRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.service.AccountCourseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountCourseServiceImplement implements AccountCourseService {
    private final AccountCourseRepository repository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    @CachePut(value = "account_course", key = "{#accountCourseId}")
    public AccountCourseDto getAccountCourseById(UUID accountCourseId) {
        // update last accessed
        repository.updateLastAccessed(accountCourseId, LocalDateTime.now());
        var accountCourse = repository.findById(accountCourseId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy AccountCourse với id: " + accountCourseId));
        return AccountCourseMapper.toDto(accountCourse);
    }

    @Override
    @Transactional
    @CachePut(value = "account_course", key = "{#result.id}")
    @CacheEvict(value = "account_courses", allEntries = true)
    public AccountCourseDto create(CreateAccountCourse createAccountCourse) {
        if(repository.existsByAccountIdAndCourseId(createAccountCourse.getAccountId(), createAccountCourse.getCourseId())) {
            throw new ConflictException("Khóa học đã được mua trước đó");
        }
        AccountCourse accountCourse = new AccountCourse();
        accountCourse.setAccountId(createAccountCourse.getAccountId());
        accountCourse.setCourseId(createAccountCourse.getCourseId());
        accountCourse.setStatus(1);
        accountCourse.setPurchaseDate(LocalDateTime.now());
        accountCourse.setLastAccessed(null);
        accountCourse.setCompletedLesson(0);
        accountCourse.setCompleted(false);
        accountCourse.setProgressPercent(0);
        accountCourse.setTotalLesson(lessonRepository.countByCourseId(createAccountCourse.getCourseId()));

        accountCourse = repository.save(accountCourse);
        return AccountCourseMapper.toDto(accountCourse);
    }

    @Override
    @Transactional
    @CachePut(value = "account_course", key = "{#id}")
    @CacheEvict(value = "account_courses", allEntries = true)
    public AccountCourseDto update(UUID id, AccountCourseDto dto) {
        var accountCourse = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy AccountCourse với id: " + id));

        // Map từ DTO sang entity
        AccountCourseMapper.updateEntityFromDto(dto, accountCourse);

        // Cập nhật lastAccessed
        accountCourse.setLastAccessed(LocalDateTime.now());

        accountCourse = repository.save(accountCourse);
        return AccountCourseMapper.toDto(accountCourse);
    }

    @Override
    @Transactional
    @CachePut(value = "account_course", key = "{#id}")
    @CacheEvict(value = "account_courses", allEntries = true)
    public AccountCourseDto patchUpdate(UUID id, AccountCourseDto dto) {
        var accountCourse = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy AccountCourse với id: " + id));

        if (dto.getCompleted()) {
            dto.setProgressPercent(100);
            dto.setCompletedLesson(accountCourse.getTotalLesson());
        }

        if (dto.getCompletedLesson() != null) {
            accountCourse.setCompletedLesson(dto.getCompletedLesson());
        }

        if (dto.getProgressPercent() != null) {
            accountCourse.setProgressPercent(dto.getProgressPercent());
        }

        accountCourse.setLastAccessed(LocalDateTime.now());

        accountCourse = repository.save(accountCourse);
        return AccountCourseMapper.toDto(accountCourse);
    }

    @Override
    @Cacheable(value = "account_courses", key = "{#accountId, #page, #size}")
    public List<AccountCourseDto> getAccountCoursesByAccountId(UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<AccountCourse> pageResult = repository.findByAccountId(accountId, pageable);
        return pageResult.getContent()
                .stream()
                .map(AccountCourseMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "account_course", key = "{#id}")
    public void delete(UUID id) {
        var accountCourse = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy AccountCourse với id: " + id));
        repository.softDeleteById(accountCourse.getId());
    }
}
