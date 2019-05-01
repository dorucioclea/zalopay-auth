package com.zalopay.auth.payload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    private List<T> list;
    private Pagination pagination;

    public PagedResponse(List<T> list, int total, int pageSize, int current) {
        this.list = list;
        this.pagination = new Pagination(total, pageSize, current);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private class Pagination {
        private int total;
        private int pageSize;
        private int current;
    }
}
