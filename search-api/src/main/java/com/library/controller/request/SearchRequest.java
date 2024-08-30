package com.library.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "검색요청을 위한 모델")
public class SearchRequest {
    // 50자
    @NotBlank(message = "입력은 비어있을 수 없습니다.")
    @Size(max = 50, message = "query는 최대 50자를 초과할 수 없습니다.")
    @Schema(description = "검색쿼리", example = "HTTP", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    private String query;
    // 1 ~ 50
    @NotNull(message = "페이지 번호는 필수입니다.")
    @Min(value = 1, message = "페이지번호는 1이상이어야 합니다.")
    @Max(value = 10000, message = "페이지번호는 10000이하여야 합니다.")
    @Schema(description = "페이지 번호", example = "1", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 10000)
    private Integer page;
    // 1 ~ 50
    @NotNull(message = "페이지 사이즈는 필수입니다.")
    @Min(value = 1, message = "페이지크기는 1이상이어야 합니다.")
    @Max(value = 50, message = "페이지크기는 50이하여야 합니다.")
    @Schema(description = "페이지 사이즈", example = "10", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    private Integer size;
}
