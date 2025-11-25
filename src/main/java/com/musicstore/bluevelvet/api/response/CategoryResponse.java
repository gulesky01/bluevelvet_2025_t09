package com.musicstore.bluevelvet.api.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;

    private String name;

    private Long parentId;

    private String pictureUrl;

}
