package site.alphacode.alphacodecourseservice.grpc.server;

import course.Course;
import course.CourseServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import site.alphacode.alphacodecourseservice.service.BundleService;
import site.alphacode.alphacodecourseservice.service.CourseService;

import javax.annotation.PostConstruct;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class CourseServiceServer extends CourseServiceGrpc.CourseServiceImplBase {
      private final BundleService bundleService;
      private final CourseService courseService;

      @PostConstruct
      public void init(){
             log.info("CourseServiceServer initialized");
      }

    @Override
    public void getCourse(Course.GetIdRequest request, StreamObserver<Course.CourseInformation> responseObserver) {log.info("Received getCourse request for ID: {}", request.getId());
        String requestId = request.getId();
        log.info("Received getCourse request for ID: {}", requestId);

        try{
            UUID id = UUID.fromString(requestId);

            var courseDto = courseService.getNoneDeleteCourseById(id);
            if(courseDto == null){
                log.warn("Course not found for ID: {}", requestId);
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Course not found")
                        .asRuntimeException());
                return;
            }

            Course.CourseInformation course = Course.CourseInformation.newBuilder()
                    .setId(courseDto.getId().toString())
                    .setName(courseDto.getName())
                    .setDescription(courseDto.getDescription())
                    .setCategoryId(courseDto.getCategoryId().toString())
                    .setImageUrl(courseDto.getImageUrl())
                    .setLevel(courseDto.getLevel())
                    .setPrice(courseDto.getPrice())
                    .setSlug(courseDto.getSlug())
                    .setTotalDuration(courseDto.getTotalDuration())
                    .setTotalLessions(courseDto.getTotalLessons())
                    .build();

            responseObserver.onNext(course);
            responseObserver.onCompleted();
        }catch (IllegalArgumentException e) {
            // UUID không hợp lệ
            log.error("Invalid courseId format: {}", requestId, e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid courseId format")
                    .withCause(e)
                    .asRuntimeException());
        } catch (Exception e) {
            // Lỗi server khác
            log.error("Internal error while getting course for courseId={}", requestId, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getBundle(Course.GetIdRequest request, StreamObserver<Course.BundleInformation> responseObserver) {
        log.info("Received getBundle request for ID: {}", request.getId());
        String requestId = request.getId();
        log.info("Received getBundle request for ID: {}", requestId);

        try{
            UUID id = UUID.fromString(requestId);

            var bundleDto = bundleService.getById(id);
            if(bundleDto == null){
                log.warn("Bundle not found for ID: {}", requestId);
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Bundle not found")
                        .asRuntimeException());
                return;
            }

            Course.BundleInformation.Builder bundleBuilder = Course.BundleInformation.newBuilder()
                    .setId(bundleDto.getId().toString())
                    .setName(bundleDto.getName())
                    .setDescription(bundleDto.getDescription())
                    .setCoverImage(bundleDto.getCoverImage())
                    .setDiscountPrice(bundleDto.getDiscountPrice())
                    .setPrice(bundleDto.getPrice());

            responseObserver.onNext(bundleBuilder.build());
            responseObserver.onCompleted();
        }catch (IllegalArgumentException e) {
            // UUID không hợp lệ
            log.error("Invalid bundleId format: {}", requestId, e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid bundleId format")
                    .withCause(e)
                    .asRuntimeException());
        } catch (Exception e) {
            // Lỗi server khác
            log.error("Internal error while getting bundle for bundleId={}", requestId, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
