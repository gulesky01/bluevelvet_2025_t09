package com.musicstore.bluevelvet.api.request;

import lombok.*;

import java.util.Optional;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    private String name;

    private Long parent_Id;

}
