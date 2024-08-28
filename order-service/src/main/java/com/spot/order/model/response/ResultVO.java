package com.spot.order.model.response;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

import static com.spot.order.util.Constants.SUCCESS;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResultVO<T> {

    @Builder.Default
    private int code = SUCCESS;
    @Builder.Default
    private String msg = StringUtils.EMPTY;
    @Builder.Default
    private List<T> data = Collections.emptyList();

    public static <T> ResultVO<T> success() {
        return ResultVO.<T>builder().code(SUCCESS).build();
    }
}
