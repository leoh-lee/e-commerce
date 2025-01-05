package kr.hhplus.be.server.support.http.response;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponse<T> {
    private List<T> items;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalItems;

    public PageResponse(Page<T> page) {
        this.items = page.getContent();
        this.currentPage = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalItems = page.getTotalElements();
    }
}
