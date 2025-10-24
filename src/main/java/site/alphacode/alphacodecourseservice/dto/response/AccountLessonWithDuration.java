package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.enums.AccountLessonEnum;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AccountLessonWithDuration implements Serializable {
    private UUID id;
    private String title;
    private String slug;
    private Integer duration;
    private Integer status;
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return AccountLessonEnum.fromCode(this.getStatus());
    }
}
